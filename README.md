# TarkovCraft - Core
Main library for our Tarkov inspired project. Purpose of this library is to have common systems in one place so that it
can be easily integrated into our projects. These common systems currently include skills, weights, attributes, statistics and more.

On its own this project offers barely any functionality for common user, however you are free to use it as dependency in any mod you like.

All systems can be disabled in the mod configuration menu.

---

# About the TarkovCraft Project
The TarkovCraft project is fan-made project heavily inspired by games like Escape From Tarkov, Grayzone Warfare and Arma.
Since on its own it would be massive mod, we have decided to split it into separate submodules (see section below) which can be used in isolation should
anyone want it - for example if you care only about the health system, you can play only with the health mod and so on.

We plan to make custom server with open-world gameplay on custom map with custom game mode - allowing capturing map sections from
other player factions, just driving around in custom vehicles, ambushing enemies or just looting near your main base.

---

## TarkovCraft project ecosystem
* **[TarkovCraft: Core](https://www.curseforge.com/minecraft/mc-mods/tarkovcraft-core)** - *Released* - common functionalities for other subprojects
* **[Medical system](https://www.curseforge.com/minecraft/mc-mods/med-system)** - *Released* - vanilla friendly implementation of custom health system, contains custom hitboxes for mobs, status effects and medical items
* **Weapons** – *Planned* – A focus on realistic firearm mechanics and customization, drawing inspiration from Tarkov while adapting to Minecraft’s mechanics.
* **Vehicles** - *Planned* - Custom vehicle framework with vehicles ranging from land/water to air vehicles. Very early concept
* **TarkovCraft** – *In development* – The final project supposed to bring all projects above together to deliver full Tarkov-like experience

---

# Version support
Here you can find an overview of all currently maintained versions

| Minecraft | Mod version                                                                                                                                                                                     | Note                                 |
|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| 26.2      | ![26.2](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.repsy.io/toma/public/tnt/tarkovcraft/tarkovcraft_core/maven-metadata.xml&versionSuffix=26.2&label=&color=00AA00)       | maintained                           |
| 26.1      | ![26.1](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.repsy.io/toma/public/tnt/tarkovcraft/tarkovcraft_core/maven-metadata.xml&versionSuffix=26.1.2&label=&color=00AA00)     | maintained                           |
| 1.21.11   | ![1.21.11](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.repsy.io/toma/public/tnt/tarkovcraft/tarkovcraft_core/maven-metadata.xml&versionSuffix=1.21.11&label=&color=DD0000) | no support                           |
| 1.21.1    | ![1.21.1](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.repsy.io/toma/public/tnt/tarkovcraft/tarkovcraft_core/maven-metadata.xml&versionSuffix=1.21.1&label=&color=CCCC00)   | maintained until stable 26.2 release |


