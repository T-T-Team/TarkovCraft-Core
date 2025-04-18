package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.context.Context;

public record SkillStatDisplayInformation(Component name, String descriptionKey, int color, ResourceLocation icon) {

    public static final Codec<SkillStatDisplayInformation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(SkillStatDisplayInformation::name),
            Codec.STRING.fieldOf("descriptionKey").forGetter(SkillStatDisplayInformation::descriptionKey),
            Codecs.RGB_COLOR.fieldOf("color").forGetter(SkillStatDisplayInformation::color),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(SkillStatDisplayInformation::icon)
    ).apply(instance, SkillStatDisplayInformation::new));

    public Component getDescription(Context context, SkillStat stat) {
        return Component.translatable(this.descriptionKey, stat.getTranslationData(context)).withStyle(ChatFormatting.GRAY);
    }
}
