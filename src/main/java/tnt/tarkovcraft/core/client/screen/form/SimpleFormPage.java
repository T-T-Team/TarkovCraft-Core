package tnt.tarkovcraft.core.client.screen.form;

import java.util.Map;

public record SimpleFormPage(Map<String, FormElement<?, ?>> elements) implements FormPage {

}
