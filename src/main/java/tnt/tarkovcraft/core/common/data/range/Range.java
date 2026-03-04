package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public interface Range {

    boolean isWithinRange(Mode mode, double input);

    double getRandomInRange(RandomSource random);

    enum Mode implements StringRepresentable {

        INCLUSIVE("inclusive", (in, from, to) -> in >= from && in <= to),
        EXCLUSIVE("exclusive", (in, from, to) -> in > from && in < to),
        INCLUSIVE_EXCLUSIVE("inclusive_exclusive", (in, from, to) -> in >= from && in < to),
        EXCLUSIVE_INCLUSIVE("exclusive_inclusive", (in, from, to) -> in > from && in <= to);

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final IntFunction<Mode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Mode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

        private final String name;
        private final RangeCheck rangeCheck;

        Mode(String name, RangeCheck rangeCheck) {
            this.name = name;
            this.rangeCheck = rangeCheck;
        }

        public boolean compare(double input, double from, double to) {
            return this.rangeCheck.check(input, from, to);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @FunctionalInterface
        private interface RangeCheck {
            boolean check(double in, double from, double to);
        }
    }
}
