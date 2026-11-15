package com.mechanica;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * One block entity for every machine. It stores the current rotation speed and
 * (on sources) pushes that speed through the connected network.
 */
public class KineticBlockEntity extends BlockEntity {
    /** Signed rotation speed. 0 = standing still. */
    public float speed = 0f;
    /** Client-side spin angle in degrees (for the renderer). */
    public float angle = 0f;
    /** Last game tick at which a source powered this block. */
    public long lastPowered = 0L;
    /** Hand crank: powered until this game tick. */
    public long poweredUntil = 0L;

    private static final int MAX_NODES = 256;

    public KineticBlockEntity(BlockPos pos, BlockState state) {
        super(Mechanica.KINETIC_BE, pos, state);
    }

    // ------------------------------------------------------------------ saving / syncing

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat("Speed", speed);
        nbt.putLong("PoweredUntil", poweredUntil);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        speed = nbt.getFloat("Speed");
        poweredUntil = nbt.getLong("PoweredUntil");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void setSpeed(float s) {
        if (s == speed || world == null) return;
        speed = s;
        markDirty();
        BlockState state = getCachedState();
        if (state.getBlock() == Mechanica.BELT) {
            world.setBlockState(pos, state.with(BeltBlock.RUNNING, s != 0f), Block.NOTIFY_ALL);
        } else {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    // ------------------------------------------------------------------ ticking

    public static void tick(World world, BlockPos pos, BlockState state, KineticBlockEntity be) {
        if (world.isClient) {
            be.angle = (be.angle + be.speed * 0.3f) % 360f;
            return;
        }
        long time = world.getTime();
        Block block = state.getBlock();
        if (!(block instanceof KineticBlock kb)) return;

        if (kb.isSource()) {
            if (time % 4 == 0) {
                float s = be.sourceSpeed(world, pos, state, time);
                be.setSpeed(s);
                if (s != 0f) {
                    be.lastPowered = time;
                    be.propagate(world, pos, s, time);
                }
            }
        } else if (be.speed != 0f && time - be.lastPowered > 8) {
            be.setSpeed(0f);
        }

        if (be.speed == 0f) return;

        if (block == Mechanica.PRESS && Math.abs(be.speed) >= 8f && time % 10 == 5) {
            be.work(world, pos, Recipes.PRESS, false);
        } else if (block == Mechanica.CRUSHER && Math.abs(be.speed) >= 8f && time % 20 == 10) {
            be.work(world, pos, Recipes.CRUSH, true);
        } else if (block == Mechanica.BELT) {
            be.moveEntities(world, pos, state);
        }
    }

    private float sourceSpeed(World world, BlockPos pos, BlockState state, long time) {
        Block block = state.getBlock();
        if (block == Mechanica.HAND_CRANK) {
            return time < poweredUntil ? 32f : 0f;
        }
        if (block == Mechanica.WATER_WHEEL) {
            Direction.Axis axis = state.get(Properties.AXIS);
            int water = 0;
            for (Direction d : Direction.values()) {
                if (d.getAxis() == axis) continue;
                if (world.getFluidState(pos.offset(d)).isIn(FluidTags.WATER)) water++;
            }
            return Math.min(32f, water * 8f);
        }
        return 0f;
    }

    // ------------------------------------------------------------------ rotation network

    private record Node(BlockPos pos, float speed) {}

    private void propagate(World world, BlockPos origin, float s, long time) {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<Node> queue = new ArrayDeque<>();
        seen.add(origin);
        queue.add(new Node(origin, s));

        while (!queue.isEmpty() && seen.size() < MAX_NODES) {
            Node n = queue.poll();
            BlockState st = world.getBlockState(n.pos());
            if (!(st.getBlock() instanceof KineticBlock kb)) continue;

            // 1) straight through shafts / casings
            for (Direction d : Direction.values()) {
                if (!kb.emits(st, d)) continue;
                BlockPos np = n.pos().offset(d);
                if (seen.contains(np)) continue;
                BlockState ns = world.getBlockState(np);
                if (ns.getBlock() instanceof KineticBlock nk && nk.accepts(ns, d.getOpposite())) {
                    visit(world, np, n.speed(), time, seen, queue);
                }
            }

            // 2) cogwheels that touch each other turn in opposite directions
            if (kb.isCog()) {
                Direction.Axis axis = kb.cogAxis(st);
                for (Direction d : Direction.values()) {
                    if (d.getAxis() == axis) continue;
                    BlockPos np = n.pos().offset(d);
                    if (seen.contains(np)) continue;
                    BlockState ns = world.getBlockState(np);
                    if (ns.getBlock() == Mechanica.COGWHEEL && ns.get(Properties.AXIS) == axis) {
                        visit(world, np, -n.speed(), time, seen, queue);
                    }
                }
            }
        }
    }

    private static void visit(World world, BlockPos np, float spd, long time, Set<BlockPos> seen, ArrayDeque<Node> queue) {
        seen.add(np);
        if (world.getBlockEntity(np) instanceof KineticBlockEntity k) {
            k.lastPowered = time;
            k.setSpeed(spd);
            queue.add(new Node(np, spd));
        }
    }

    // ------------------------------------------------------------------ press / crusher

    private void work(World world, BlockPos pos, java.util.Map<net.minecraft.item.Item, ItemStack> recipes, boolean crusher) {
        BlockPos cell = pos.down();
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, new Box(cell), e -> !e.getStack().isEmpty());
        boolean did = false;
        for (ItemEntity ie : items) {
            ItemStack out = recipes.get(ie.getStack().getItem());
            if (out == null) continue;

            ItemStack rest = ie.getStack().copy();
            rest.decrement(1);
            if (rest.isEmpty()) ie.discard(); else ie.setStack(rest);

            ItemEntity result = new ItemEntity(world, ie.getX(), ie.getY() + 0.1, ie.getZ(), out.copy());
            result.setVelocity(0, 0.12, 0);
            world.spawnEntity(result);
            did = true;
        }
        if (did && world instanceof ServerWorld sw) {
            sw.spawnParticles(crusher ? ParticleTypes.POOF : ParticleTypes.CRIT,
                    cell.getX() + 0.5, cell.getY() + 0.3, cell.getZ() + 0.5, 8, 0.25, 0.1, 0.25, 0.05);
            world.playSound(null, pos, crusher ? SoundEvents.BLOCK_GRINDSTONE_USE : SoundEvents.BLOCK_PISTON_EXTEND,
                    SoundCategory.BLOCKS, 0.7f, crusher ? 0.9f : 1.3f);
        }
    }

    // ------------------------------------------------------------------ conveyor belt

    private void moveEntities(World world, BlockPos pos, BlockState state) {
        Direction f = state.get(BeltBlock.FACING);
        double v = 0.0025 * speed;
        Box box = new Box(pos.getX(), pos.getY() + 0.2, pos.getZ(), pos.getX() + 1, pos.getY() + 1.2, pos.getZ() + 1);

        for (Entity e : world.getOtherEntities(null, box)) {
            if (!(e instanceof ItemEntity) && !(e instanceof LivingEntity)) continue;

            double dx = f.getOffsetX() * v;
            double dz = f.getOffsetZ() * v;
            // gently pull the entity towards the middle of the belt
            if (f.getAxis() == Direction.Axis.X) {
                dz += Math.max(-0.03, Math.min(0.03, (pos.getZ() + 0.5 - e.getZ()) * 0.15));
            } else {
                dx += Math.max(-0.03, Math.min(0.03, (pos.getX() + 0.5 - e.getX()) * 0.15));
            }

            if (e instanceof PlayerEntity p) {
                if (p.isSneaking()) continue;
                p.addVelocity(dx * 0.15, 0, dz * 0.15);
            } else {
                Vec3d vel = e.getVelocity();
                e.setVelocity(dx, vel.y, dz);
            }
            e.velocityModified = true;
            e.fallDistance = 0f;
        }
    }
}
