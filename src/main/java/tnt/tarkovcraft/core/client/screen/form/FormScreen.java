package tnt.tarkovcraft.core.client.screen.form;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.client.screen.DialogScreen;
import tnt.tarkovcraft.core.client.screen.renderable.AbstractTextRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.ValidationMessageRenderable;

import java.util.*;
import java.util.function.Function;

public class FormScreen extends DialogScreen {

    private final Context context;
    private int page;

    public FormScreen(FormBuilder builder) {
        super(builder);
        this.context = builder.context;
    }

    @Override
    protected void addControlButtons() {
        // TODO add page nav buttons
        if (this.page == this.context.getPages().size() - 1) {
            // last page
            super.addControlButtons();
        }
    }

    @Override
    protected void init() {
        super.init();
        FormPage page = this.getPage();
        Collection<FormElement<?, ?>> elements = page.elements().values();
        int index = 0;
        for (FormElement<?, ?> element : elements) {
            this.initFormElement(element, index++);
        }
    }

    protected <T, C extends AbstractWidget> void initFormElement(FormElement<T, C> element, int index) {
        int left = this.left + this.getButtonMargin();
        int top = this.top + this.getButtonMargin() + index * 35; // 10 title, 15 element, 10 errors
        int width = this.windowWidth - this.getButtonMargin() * 2;

        Component label = element.getLabel();
        // Field name
        this.addRenderableOnly(new AbstractTextRenderable.ComponentRenderable(left, top, width, 10, 0xFFFFFF, false, this.font, label));
        // Field
        this.addRenderableWidget(element.init(left, top + 10, width, 15, this.context));
        // Error field
        this.addRenderableOnly(new ValidationMessageRenderable(left, top + 25, width, 10, true, this.font, element));
    }

    public <T> T getFormValue(String id) {
        return this.context.getValue(id);
    }

    @Override
    public void handleConfirmed() {
        if (this.context.isFormValid()) {
            super.handleConfirmed();
        }
    }

    @Override
    protected void addTitle() {
    }

    @Override
    protected void addBody() {
    }

    @Override
    protected void addTitleSeparator() {
    }

    private FormPage getPage() {
        return this.context.getPage(this.page);
    }

    @SuppressWarnings("unchecked")
    public static final class Context implements FormContext {

        private final Map<String, FormElement<?, ?>> element = new HashMap<>();
        private final Map<String, Object> valueMap = new HashMap<>();
        private final Map<String, FormElementValidator<?>> formValidators = new HashMap<>();
        private final List<FormPage> pages = new ArrayList<>();

        @Override
        public <T> T getValue(String name) {
            return (T) this.valueMap.get(name);
        }

        @Override
        public <T> void updateValue(String id, T value) {
            this.valueMap.put(id, value);
        }

        @Override
        public <T> FormElementValidator<T> getValidator(String id) {
            return (FormElementValidator<T>) this.formValidators.get(id);
        }

        @Override
        public boolean isFormValid() {
            for (String id : this.element.keySet()) {
                if (!this.validate(id)) {
                    return false;
                }
            }
            return true;
        }

        public <T> boolean validate(String id) {
            FormElementValidator<T> validator = (FormElementValidator<T>) this.formValidators.get(id);
            if (validator == null) {
                return true;
            }
            IValidationResult result = validator.validate(this.getValue(id));
            FormElement<?, ?> formElement = this.element.get(id);
            formElement.setValidatorResult(result);
            return result.severity().isValid();
        }

        public FormPage getPage(int index) {
            return this.pages.get(index);
        }

        public List<FormPage> getPages() {
            return pages;
        }

        public void addPage(FormPage page) {
            this.pages.add(page);
            this.addElementMappings(page.elements());
        }

        public void addElementMappings(Map<String, FormElement<?, ?>> mapping) {
            for (Map.Entry<String, FormElement<?, ?>> entry : mapping.entrySet()) {
                if (this.element.put(entry.getKey(), entry.getValue()) != null) {
                    throw new RuntimeException("Duplicate key: " + entry.getKey());
                }
                Map<String, FormElement<?, ?>> children = entry.getValue().children();
                if (children != null) {
                    this.addElementMappings(children);
                }
            }
        }
    }

    public static class FormBuilder extends Builder {

        private final Context context = new Context();

        public FormBuilder(Screen parent, Component title) {
            super(parent, title);
        }

        public PageBuilder newPage() {
            return new PageBuilder(this);
        }

        public FormBuilder addPage(FormPage page) {
            this.context.addPage(page);
            return this;
        }

        public <T> FormBuilder addValidator(String id, FormElementValidator<T> validator) {
            this.context.formValidators.put(id, validator);
            return this;
        }
    }

    public static class PageBuilder {

        private final FormBuilder parent;
        private final Map<String, FormElement<?, ?>> elements = new HashMap<>();

        protected PageBuilder(FormBuilder parent) {
            this.parent = parent;
        }

        public <T, C extends AbstractWidget> PageBuilder addElement(T defaultValue, FormElement<T, C> element) {
            this.parent.context.updateValue(element.getId(), defaultValue);
            return this.addElement(element);
        }

        public <T, C extends AbstractWidget> PageBuilder addElement(FormElement<T, C> element) {
            this.elements.put(element.getId(), element);
            return this;
        }

        public FormBuilder buildDefault() {
            return this.buildPage(SimpleFormPage::new);
        }

        public FormBuilder buildPage(Function<Map<String, FormElement<?, ?>>, FormPage> builder) {
            return this.parent.addPage(builder.apply(this.elements));
        }
    }
}
