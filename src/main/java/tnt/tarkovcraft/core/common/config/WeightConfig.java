package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Configurable;

public final class WeightConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment({
            "Allows you to toggle weight system",
            "Beware as users affected by negative weight effects will be stuck with those effects after disabling"
    })
    public boolean enableWeightSystem = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Calculates weight based on item storage too - bundles, shulker boxes, ...")
    public boolean calculateContainerWeight = true;

    @Configurable
    @Configurable.DecimalRange(min = -1.0F, max = 0.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Gui.Slider
    public float overweightSpeedReduction = -0.15F;

    @Configurable
    @Configurable.DecimalRange(min = -1.0F, max = 0.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Gui.Slider
    public float overweightJumpReduction = -0.125F;

    @Configurable
    @Configurable.DecimalRange(min = -1.0F, max = 0.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Gui.Slider
    public float overweightSafeFallDistanceReduction = -0.4F;

    @Configurable
    @Configurable.DecimalRange(min = -1.0F, max = 0.0F)
    @Configurable.Gui.NumberFormat("0.0##")
    @Configurable.Gui.Slider
    public float overweightStepHeightReduction = -0.15F;
}
