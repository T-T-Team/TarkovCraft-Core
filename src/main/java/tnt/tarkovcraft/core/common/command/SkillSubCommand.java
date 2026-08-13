package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.*;

public final class SkillSubCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> node(CommandBuildContext context) {
        return Commands.literal("skill")
                .then(
                        Commands.argument("skillId", ResourceArgument.resource(context, CoreRegistries.DatapackKeys.SKILL_DEFINITION))
                                .then(
                                        Commands.argument("target", EntityArgument.entity())
                                                .then(
                                                        Commands.literal("level")
                                                                .then(
                                                                        Commands.argument("levelValue", IntegerArgumentType.integer(0))
                                                                                .executes(SkillSubCommand::setSkillLevel)
                                                                )
                                                )
                                                .then(
                                                        Commands.literal("experience")
                                                                .then(
                                                                        Commands.argument("experienceValue", FloatArgumentType.floatArg(0.001F))
                                                                                .executes(SkillSubCommand::addSkillExperience)
                                                                )
                                                )
                                                .then(
                                                        Commands.literal("forget")
                                                                .then(
                                                                        Commands.argument("experienceValue", FloatArgumentType.floatArg(0.001F))
                                                                                .executes(SkillSubCommand::forgetSkillExperience)
                                                                )
                                                )
                                )
                );
    }

    private static int setSkillLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        SkillDefinition skillDefinition = ResourceArgument.getResource(ctx, "skillId", CoreRegistries.DatapackKeys.SKILL_DEFINITION).value();
        int level = IntegerArgumentType.getInteger(ctx, "levelValue");
        int maxLevel = skillDefinition.configuration().maxLevel();
        int setLevel = Math.min(maxLevel, level);
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill instance = skillData.getSkill(skillDefinition);
        instance.forceSetLevel(setLevel);
        skillData.reloadStats();
        SkillSystem.synchronize(target);
        return 0;
    }

    private static int addSkillExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        SkillDefinition skillDefinition = ResourceArgument.getResource(ctx, "skillId", CoreRegistries.DatapackKeys.SKILL_DEFINITION).value();
        float exp = FloatArgumentType.getFloat(ctx, "experienceValue");
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill instance = skillData.getSkill(skillDefinition);
        skillData.addExperience(instance, exp);
        instance.setLastExperienceUpdate(target.level().getGameTime());
        SkillSystem.synchronize(target);
        return 0;
    }

    private static int forgetSkillExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        SkillDefinition skillDefinition = ResourceArgument.getResource(ctx, "skillId", CoreRegistries.DatapackKeys.SKILL_DEFINITION).value();
        float exp = FloatArgumentType.getFloat(ctx, "experienceValue");
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill instance = skillData.getSkill(skillDefinition);
        SkillMemoryConfiguration memoryCfg = skillDefinition.configuration().memory();
        instance.loseExperience(exp, memoryCfg);
        instance.setLastExperienceUpdate(target.level().getGameTime());
        SkillSystem.synchronize(target);
        return 0;
    }
}
