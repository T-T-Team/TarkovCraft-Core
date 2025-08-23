package tnt.tarkovcraft.core.common.weight;

public interface WeightProvider {

    int getWeight(WeightContext context);

    WeightSource getSource();

    enum WeightSource {

        ITEM,
        ENTITY;

        public boolean isItem() {
            return this == ITEM;
        }

        public boolean isEntity() {
            return this == ENTITY;
        }
    }
}
