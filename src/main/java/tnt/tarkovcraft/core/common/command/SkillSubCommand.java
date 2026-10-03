package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.skill.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class SkillSubCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> node() {
        return Commands.literal("skill")
                .then(
                        Commands.argument("skillId", IdentifierArgument.id())
                                .suggests(SkillSubCommand::suggestSkills)
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
        Identifier identifier = IdentifierArgument.getId(ctx, "skillId");
        int level = IntegerArgumentType.getInteger(ctx, "levelValue");
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill skill = skillData.getSkill(identifier);
        if (skill != null) {
            SkillDefinition definition = skill.getDefinition();
            int maxLevel = definition.configuration().maxLevel();
            int setLevel = Math.min(maxLevel, level);
            skill.forceSetLevel(setLevel, target);
            SkillSystem.synchronize(target);
        }
        return 0;
    }

    private static int addSkillExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        Identifier identifier = IdentifierArgument.getId(ctx, "skillId");
        float exp = FloatArgumentType.getFloat(ctx, "experienceValue");
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill skill = skillData.getSkill(identifier);
        if (skill != null) {
            skillData.addExperience(skill, exp);
            skill.setLastExperienceUpdate(target.level().getGameTime());
            SkillSystem.synchronize(target);
        }
        return 0;
    }

    private static int forgetSkillExperience(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        Identifier identifier = IdentifierArgument.getId(ctx, "skillId");
        float exp = FloatArgumentType.getFloat(ctx, "experienceValue");
        SkillData skillData = target.getData(CoreDataAttachments.SKILL);
        Skill skill = skillData.getSkill(identifier);
        if (skill != null) {
            SkillDefinition definition = skill.getDefinition();
            SkillMemoryConfiguration memoryConfiguration = definition.configuration().memory();
            skill.loseExperience(exp, memoryConfiguration);
            skill.setLastExperienceUpdate(target.level().getGameTime());
            SkillSystem.synchronize(target);
        }
        return 0;
    }

    private static CompletableFuture<Suggestions> suggestSkills(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Collection<Identifier> skills = SkillSystem.listAvailableSkills();
        skills.forEach(id -> builder.suggest(id.toString()));
        return builder.buildFuture();
    }
}
