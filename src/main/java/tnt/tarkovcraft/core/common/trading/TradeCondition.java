package tnt.tarkovcraft.core.common.trading;

public interface TradeCondition {

    TradeConditionType<?> getType();

    boolean canShowOffer(TradeContext context);

    boolean canCompleteTrade(TradeContext context);
}
