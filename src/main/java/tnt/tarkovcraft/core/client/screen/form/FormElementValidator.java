package tnt.tarkovcraft.core.client.screen.form;

import dev.toma.configuration.config.validate.IValidationResult;

@FunctionalInterface
public interface FormElementValidator<T> {
    IValidationResult validate(T value);
}
