package tnt.tarkovcraft.core.common.statistic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.NumberFormatter;
import tnt.tarkovcraft.core.util.NumberOperator;

import java.util.Collections;
import java.util.List;

public final class DisplayStatistic {

    public static final Codec<DisplayStatistic> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ComponentSerialization.CODEC.fieldOf("label").forGetter(t -> t.label),
            NumberFormatter.CODEC.optionalFieldOf("formatter", NumberFormatter.IDENTITY).forGetter(t -> t.format),
            CoreRegistries.STATISTICS.holderByNameCodec().fieldOf("source").forGetter(t -> t.source),
            Codecs.NON_NEGATIVE_INT.optionalFieldOf("order", 0).forGetter(t -> t.order),
            Codecs.list(ExtraCalculation.CODEC).optionalFieldOf("extra", Collections.emptyList()).forGetter(t -> t.extra)
    ).apply(instance, DisplayStatistic::new));

    private final Component label;
    private final NumberFormatter format;
    private final Holder<Statistic> source;
    private final int order;
    private final List<ExtraCalculation> extra;

    public DisplayStatistic(Component label, NumberFormatter format, Holder<Statistic> source, int order, List<ExtraCalculation> extra) {
        this.label = label;
        this.format = format;
        this.source = source;
        this.order = order;
        this.extra = extra;
    }

    public Component getLabel() {
        return label;
    }

    public int getOrder() {
        return order;
    }

    public String get(StatisticReader reader) {
        double value = reader.get(this.source);
        for (ExtraCalculation calculation : this.extra) {
            double stat = reader.getMinimum(calculation.source, calculation.min);
            value = calculation.operator.applyAsDouble(value, stat);
        }
        return this.format.formatValue(value);
    }

    public record ExtraCalculation(Holder<Statistic> source, long min, NumberOperator operator) {

        public static final Codec<ExtraCalculation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CoreRegistries.STATISTICS.holderByNameCodec().fieldOf("source").forGetter(ExtraCalculation::source),
                Codec.LONG.optionalFieldOf("min", 0L).forGetter(ExtraCalculation::min),
                NumberOperator.CODEC.fieldOf("operator").forGetter(ExtraCalculation::operator)
        ).apply(instance, ExtraCalculation::new));
    }
}
