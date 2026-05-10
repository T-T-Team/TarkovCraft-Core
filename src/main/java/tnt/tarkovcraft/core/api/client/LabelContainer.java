package tnt.tarkovcraft.core.api.client;

import tnt.tarkovcraft.core.client.util.IconWithLabel;
import tnt.tarkovcraft.core.util.HorizontalAlignment;

import java.util.List;

public interface LabelContainer {

    List<IconWithLabel> getLabels(HorizontalAlignment alignment);

    void removeLabelAt(HorizontalAlignment alignment, int index);

    void addLabel(HorizontalAlignment alignment, IconWithLabel label, int index);

    void addLabel(HorizontalAlignment alignment, IconWithLabel label);
}
