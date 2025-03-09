package tnt.tarkovcraft.core.client.screen.form;

public interface FormContext {

    <T> T getValue(String name);

    <T> void updateValue(String id, T value);

    <T> FormElementValidator<T> getValidator(String name);

    boolean isFormValid();
}
