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

# FAQ

>**Q: How do I check my skills, statistics, etc.?**
> 
>**A:** All available screens can be accessed via the `O` key (default). Once opened, use the navigation menu at the top to switch between different views.

>**Q: I don't want to use the skill system. Can I disable it?**
> 
>**A:** Yes! Skills (and other features) can be disabled through the configuration menu—either from the main menu or directly by editing the config file.

> **Q: Will the mod be backported to older Minecraft versions?**
> 
> **A:** No. The project relies on features only available in newer Minecraft versions. Supporting older versions would require extensive rewrites of core systems, which would slow down overall development.

> **Q: Which Minecraft version is supported?**
> 
> **A:** We're currently targeting Minecraft 1.22 once it's available. We may port to newer versions in the future, unless major rewrites in the Minecraft codebase make it impractical. Once the mod reaches a stable state with robust features, we'll evaluate the best version to continue development on.


