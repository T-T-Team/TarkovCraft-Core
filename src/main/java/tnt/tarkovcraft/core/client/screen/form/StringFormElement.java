package tnt.tarkovcraft.core.client.screen.form;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class StringFormElement extends FormElement<String, EditBox> {

    public StringFormElement(String id, Component label, Consumer<EditBox> builder) {
        super(id, label, new Manager(id, builder));
    }

    public StringFormElement(String id, Component label) {
        this(id, label, t -> {});
    }

    @Override
    protected void onComponentInitialize(EditBox component, FormContext context) {
        FormElementValidator<String> validator = context.getValidator(this.id);
        if (validator != null) {
            component.setResponder(val -> {
                context.updateValue(this.id, val);
                if (!val.isBlank()) {
                    IValidationResult result = validator.validate(val);
                    this.setValidatorResult(result);
                }
            });
        }
    }

    protected static class Manager implements FormElementManager<String, EditBox> {

        private final String id;
        private final Consumer<EditBox> builder;

        public Manager(String id, Consumer<EditBox> builder) {
            this.id = id;
            this.builder = builder;
        }

        @Override
        public EditBox createWidget(int left, int top, int width, int height, FormContext ctx) {
            Font font = Minecraft.getInstance().font;
            EditBox editBox = new EditBox(font, left, top, width, height, CommonComponents.EMPTY);
            this.builder.accept(editBox);
            String value = ctx.getValue(this.id);
            if (value != null)
                editBox.setValue(value);
            editBox.setResponder(string -> ctx.updateValue(this.id, string));
            return editBox;
        }

        @Override
        public String getValue(EditBox widget) {
            return widget.getValue();
        }
    }
}
