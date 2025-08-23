package tnt.tarkovcraft.core.common.config.validate;

import dev.toma.configuration.config.validate.ValidationResult;
import dev.toma.configuration.config.validate.Validator;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLEnvironment;

public class DevelopmentModeValidator implements Validator<Object> {

    public static final Component WARNING = Component.translatable("label.tarkovcraft_core.config.validation.not_development_mode");

    @Override
    public ValidationResult validate(Object t, IConfigValueReadable<Object> iConfigValueReadable) {
        return FMLEnvironment.production
                ? ValidationResult.warning(WARNING)
                : ValidationResult.success();
    }
}
