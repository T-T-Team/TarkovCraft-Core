package tnt.tarkovcraft.core.client.screen.form;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.Util;

import java.util.Map;
import java.util.function.Supplier;

public abstract class FormElement<T, C extends AbstractWidget> implements Supplier<IValidationResult> {

    protected String id;
    protected Component label;
    protected IValidationResult validatorResult = IValidationResult.success();
    protected C component;
    protected FormElementManager<T, C> manager;

    public FormElement(String id, Component label, FormElementManager<T, C> manager) {
        this.id = id;
        this.label = label;
        this.manager = manager;
    }

    public final C init(int left, int top, int width, int height, FormContext context) {
        this.component = this.manager.createWidget(left, top, width, height, context);
        this.onComponentInitialize(this.component, context);
        return this.component;
    }

    protected void onComponentInitialize(C component, FormContext context) {
    }

    protected void setValidatorResult(IValidationResult result) {
        this.validatorResult = Util.orDefault(result, IValidationResult.success());
    }

    @Override
    public IValidationResult get() {
        return this.validatorResult;
    }

    public C getComponent() {
        return component;
    }

    public T getValue() {
        return this.manager.getValue(this.component);
    }

    public Map<String, FormElement<?, ?>> children() {
        return null;
    }

    public String getId() {
        return id;
    }

    public Component getLabel() {
        return label;
    }

    public interface FormElementManager<T, C extends AbstractWidget> {

        C createWidget(int left, int top, int width, int height, FormContext context);

        T getValue(C widget);
    }
}
