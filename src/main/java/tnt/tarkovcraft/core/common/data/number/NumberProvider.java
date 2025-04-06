package tnt.tarkovcraft.core.common.data.number;

public interface NumberProvider {

    NumberProviderType<?> getType();

    double getNumber();
}
