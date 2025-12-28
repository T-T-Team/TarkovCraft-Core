package tnt.tarkovcraft.core.client.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.IconWithLabel;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class PlayerProfileLabelContainer {

    public static final Identifier ROW_PLAYER_NAME = TarkovCraftCore.createIdentifier("profile_name");
    public static final Identifier ROW_STAT = TarkovCraftCore.createIdentifier("stat");
    public static final Identifier ROW_STAT_SEPARATOR = TarkovCraftCore.createIdentifier("separator/status");
    private final List<ProfileLabelRow> rows = new ArrayList<>();

    public void addRow(ProfileLabelRow row) {
        rows.add(row);
    }

    public void addEmptyRow(Identifier rowId) {
        this.addRow(new ProfileLabelRow(rowId, null, null, null));
    }

    @Nullable
    public ProfileLabelRow findRow(Identifier rowIdentifier) {
        return rows.stream()
                .filter(row -> row.identifier().equals(rowIdentifier))
                .findFirst()
                .orElse(null);
    }

    public boolean replaceOrDeleteRow(Identifier identifier, UnaryOperator<ProfileLabelRow> replacement) {
        for (int i = 0; i < rows.size(); i++) {
            ProfileLabelRow row = rows.get(i);
            ProfileLabelRow newRow = replacement.apply(row);
            if (row.identifier().equals(identifier)) {
                if (newRow == null) {
                    rows.remove(i);
                } else {
                    rows.set(i, newRow);
                }
                return true;
            }
        }
        return false;
    }

    public List<ProfileLabelRow> getRows() {
        return ImmutableList.copyOf(this.rows);
    }

    public record ProfileLabelRow(Identifier identifier, @Nullable IconWithLabel left, @Nullable IconWithLabel center, @Nullable IconWithLabel right) {

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ProfileLabelRow that)) return false;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(identifier);
        }

        public static ProfileLabelRow left(Identifier identifier, IconWithLabel left) {
            return new ProfileLabelRow(identifier, left, null, null);
        }

        public static ProfileLabelRow center(Identifier identifier, IconWithLabel center) {
            return new ProfileLabelRow(identifier, null, center, null);
        }

        public static ProfileLabelRow right(Identifier identifier, IconWithLabel right) {
            return new ProfileLabelRow(identifier, null, null, right);
        }
    }
}
