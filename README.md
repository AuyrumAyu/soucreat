# Mechanica - a tiny Create parody (Fabric 1.20.1, Java 17)

Blocks: Hand Crank, Water Wheel, Shaft, Cogwheel, Mechanical Press, Crusher, Conveyor Belt.
Items: Iron / Gold / Copper plate.  Everything is in the "Mechanica" creative tab.

## How it works
* Power sources: **Hand Crank** (right-click, spins for 2 s) and **Water Wheel** (needs water touching its sides).
* **Shaft** passes rotation along its axis. **Cogwheel** does the same and also meshes with a cogwheel next to it
  (same axis) turning the opposite way.
* The crank gives power to every block touching it. The water wheel gives power through its axis ends.
* **Press**: put it ABOVE a cell; items lying in the cell under it are stamped
  (iron/gold/copper ingot -> plate, sugar cane -> paper). It needs power from any side.
* **Crusher**: same idea (cobblestone -> gravel -> sand, ores -> 2-3 raw material, bone -> bone meal).
* **Conveyor Belt**: place along a line (it faces where you look). Power one end with a shaft (or crank).
  It carries items and mobs. Put a belt on the floor, one empty cell above it and the press/crusher above that.

## Build on GitHub
Upload this folder to a repo -> Actions tab -> "build" -> download artifact "mechanica-mod".
Put the jar in .minecraft/mods with Fabric API 0.92.2 for 1.20.1.
