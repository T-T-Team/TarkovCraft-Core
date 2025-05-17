# TarkovCraft - Core

**TarkovCraft - Core** is the foundational library for our expansive modding project, **TarkovCraft**. Its primary purpose is to consolidate common systems and functionalities—such as skills, quests, and other shared mechanics—into a single, reusable module. This architecture allows submodules to seamlessly build upon a consistent and expandable base.

As this is a backend library, it does not offer direct features or content for end-users. However, mod and modpack developers are welcome to use this library as a dependency for their own projects. If you believe a feature is missing or could improve the scope of the project, feel free to open a suggestion or contribute via pull request.

---

# FAQ

**Q: How do I check my skills, statistics, etc.?**
**A:** All available screens can be accessed via the `O` key (default). Once opened, use the navigation menu at the top to switch between different views.

**Q: I don't want to use the skills system. Can I disable it?**
**A:** Yes! Skills (and other features) can be disabled through the configuration menu—either from the main menu or directly by editing the config file.

**Q: Will the mod be backported to older Minecraft versions?**
**A:** No. The project relies on features only available in newer Minecraft versions. Supporting older versions would require extensive rewrites of core systems, which would slow down overall development.

**Q: Which Minecraft version is supported?**
**A:** We're currently targeting Minecraft 1.22 once it's available. We may port to newer versions in the future, unless major rewrites in the Minecraft codebase make it impractical. Once the mod reaches a stable state with robust features, we'll evaluate the best version to continue development on.

---

# About the TarkovCraft Project

**TarkovCraft** is an ambitious modding initiative inspired by the gameplay systems of *Escape from Tarkov*, reimagined in a way that fits Minecraft’s sandbox nature. Rather than attempting a direct 1:1 recreation of Tarkov, we’re blending its complex mechanics—like modular weapons, medical systems, and inventory management—with Minecraft's open-world experience.

Due to the massive scope of the project, we’ve chosen a modular approach. Each subproject is a standalone mod that can be used independently or as part of the full TarkovCraft suite. This flexibility allows players and developers to pick and choose the systems they want—whether it’s the medical overhaul, weapon realism, or inventory changes.

We are not implementing a raid system. Instead, TarkovCraft is designed around an open-world concept where all interactions and progression take place in a continuous world. If the project gains enough interest, we plan to launch an official server featuring enhanced gameplay elements such as factions and custom content.

---

## Subprojects

* **Core Library** – (This repository) Provides the foundational systems shared across all submodules.
* **Medsystem** – An advanced health system that breaks down the default entity hitbox into multiple zones (head, torso, arms, legs). Fully extensible via datapacks for both vanilla and modded entities.
* **ModularSlots** – *Planned* – A complete overhaul of the inventory system, enabling items to occupy varied space based on size and type, along with an integrated weight system.
* **Weapons** – *Planned* – A focus on realistic firearm mechanics and customization, drawing inspiration from Tarkov while adapting to Minecraft’s mechanics.
* **AddonLib** – *Planned* – A lightweight library to help users create custom items, weapons, and features that integrate seamlessly across the various submodules.
* **TarkovCraft** – *Planned* – The flagship mod that integrates all submodules into a unified experience, offering the full TarkovCraft gameplay package.
