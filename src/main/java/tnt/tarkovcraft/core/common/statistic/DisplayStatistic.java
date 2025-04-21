package tnt.tarkovcraft.core.common.statistic;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.data.number.StatisticNumberProvider;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.UnitFormat;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.function.Function;

public final class DisplayStatistic {

    public static final Codec<DisplayStatistic> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ComponentSerialization.CODEC.fieldOf("label").forGetter(t -> t.label),
            UnitFormat.CODEC.optionalFieldOf("format", UnitFormat.IDENTITY).forGetter(t -> t.format),
            Codec.either(NumberProviderType.ID_CODEC, CoreRegistries.STATISTICS.byNameCodec()).fieldOf("source").forGetter(t -> Either.left(t.statisticProvider)),
            Codecs.NON_NEGATIVE_INT.optionalFieldOf("order", 0).forGetter(t -> t.order)
    ).apply(instance, DisplayStatistic::new));

    private final Component label;
    private final UnitFormat format;
    private final NumberProvider statisticProvider;
    private final int order;

    public DisplayStatistic(Component label, UnitFormat format, Either<NumberProvider, Statistic> statisticProvider, int order) {
        this.label = label;
        this.format = format;
        this.statisticProvider = statisticProvider.map(Function.identity(), StatisticNumberProvider::new);
        this.order = order;
    }

    public Component getLabel() {
        return label;
    }

    public int getOrder() {
        return order;
    }

    public String get(Context context) {
        double value = this.statisticProvider.getNumber(context);
        return this.format.format(value);
    }
}
