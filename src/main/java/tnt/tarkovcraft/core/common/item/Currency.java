package tnt.tarkovcraft.core.common.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.common.init.CoreItemDataComponents;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

@Deprecated
public record Currency(Holder<CurrencyType> type, int amount) {

    public static final Codec<Currency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreRegistries.CURRENCY.holderByNameCodec().fieldOf("type").forGetter(t -> t.type),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(Currency::amount)
    ).apply(instance, Currency::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Currency> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(CoreRegistries.Keys.CURRENCY), Currency::type,
            ByteBufCodecs.INT, Currency::amount,
            Currency::new
    );

    public Currency(Holder<CurrencyType> type) {
        this(type, 1);
    }

    public boolean canMerge(Currency currency) {
        if (!this.type.equals(currency.type)) {
            return false;
        }
        return this.amount < this.type.value().stackLimit();
    }

    public Pair<Currency, Currency> merge(Currency other) {
        if (!this.canMerge(other)) {
            return new Pair<>(this, other);
        }
        int insertedAmount = Math.min(this.type.value().stackLimit() - this.amount, other.amount);
        int remainder = other.amount - insertedAmount;
        if (remainder == 0) {
            return new Pair<>(new Currency(this.type, this.amount + insertedAmount), null);
        }
        return new Pair<>(new Currency(this.type, this.amount + insertedAmount), new Currency(other.type, other.amount - insertedAmount));
    }

    public Pair<Currency, Currency> split() {
        int half = this.amount / 2;
        int diff = this.amount - half;
        return new Pair<>(
                new Currency(this.type, diff),
                new Currency(this.type, half)
        );
    }

    public void setToItemStack(ItemStack itemStack) {
        itemStack.set(CoreItemDataComponents.CURRENCY, this);
    }

    public static Currency getFromItemStack(ItemInstance instance) {
        return instance != null ? instance.get(CoreItemDataComponents.CURRENCY) : null;
    }
}
