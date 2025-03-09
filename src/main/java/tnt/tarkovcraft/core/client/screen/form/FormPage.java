package tnt.tarkovcraft.core.client.screen.form;

import java.util.Map;

public interface FormPage {

    Map<String, FormElement<?, ?>> elements();
}
