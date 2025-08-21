package tnt.tarkovcraft.core.client.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.TarkovCraftCore;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerProfileLabelContainer {

    public static final ResourceLocation ROW_PLAYER_NAME = TarkovCraftCore.createResourceLocation("profile_name");
    public static final ResourceLocation ROW_KDR = TarkovCraftCore.createResourceLocation("kdr");
    private final List<ProfileLabelRow> rows = new ArrayList<>();

    public void addRow(ProfileLabelRow row) {
        rows.add(row);
    }

    @Nullable
    public ProfileLabelRow findRow(ResourceLocation rowIdentifier) {
        return rows.stream()
                .filter(row -> row.identifier().equals(rowIdentifier))
                .findFirst()
                .orElse(null);
    }

    public boolean replaceOrDeleteRow(ResourceLocation identifier, @Nullable ProfileLabelRow replacement) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).identifier().equals(identifier)) {
                if (replacement == null) {
                    rows.remove(i);
                } else {
                    rows.set(i, replacement);
                }
                return true;
            }
        }
        return false;
    }

    public List<ProfileLabelRow> getRows() {
        return ImmutableList.copyOf(this.rows);
    }

    public record ProfileLabelRow(ResourceLocation identifier, @Nullable Component left, @Nullable Component center, @Nullable Component right) {

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ProfileLabelRow that)) return false;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(identifier);
        }

        public static ProfileLabelRow left(ResourceLocation identifier, Component left) {
            return new ProfileLabelRow(identifier, left, null, null);
        }

        public static ProfileLabelRow center(ResourceLocation identifier, Component center) {
            return new ProfileLabelRow(identifier, null, center, null);
        }

        public static ProfileLabelRow right(ResourceLocation identifier, Component right) {
            return new ProfileLabelRow(identifier, null, null, right);
        }
    }
}
