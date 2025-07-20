package tnt.tarkovcraft.core.client.screen.form;

import dev.toma.configuration.config.validate.ValidationResult;

@FunctionalInterface
public interface FormElementValidator<T> {
    ValidationResult validate(T value);
}
