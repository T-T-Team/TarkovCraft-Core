package tnt.tarkovcraft.core.common.trading;

public interface TradeResource {

    TradeResourceType<?> getType();

    boolean validateAvailability(TradeContext context);

    void consume(TradeContext context);

    void produce(TradeContext context);
}
