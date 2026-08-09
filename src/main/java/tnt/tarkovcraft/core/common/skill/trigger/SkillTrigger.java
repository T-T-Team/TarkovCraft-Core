package tnt.tarkovcraft.core.common.skill.trigger;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record SkillTrigger(ResourceLocation identifier) {

    public Component getDisplayName() {
        return Component.translatable(this.identifier.toLanguageKey("skill.trigger", "info"));
    }
}
