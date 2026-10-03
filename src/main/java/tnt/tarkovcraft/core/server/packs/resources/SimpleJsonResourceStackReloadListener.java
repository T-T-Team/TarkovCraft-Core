package tnt.tarkovcraft.core.server.packs.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class SimpleJsonResourceStackReloadListener<T> extends SimpleJsonResourceReloadListener<T> {

    protected SimpleJsonResourceStackReloadListener(Codec<T> codec, FileToIdConverter lister) {
        super(codec, lister);
    }

    protected abstract void validateResultItem(Identifier id, T item);

    protected abstract T mergeResources(Identifier id, T item, T overridingItem);

    @Override
    protected final Map<Identifier, T> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, T> result = new HashMap<>();
        var ops = this.makeConditionalOps(this.ops);
        var conditionalCodec = ConditionalOps.createConditionalCodec(this.codec);
        for (var entries : this.lister.listMatchingResourceStacks(manager).entrySet()) {
            Identifier fileId = entries.getKey();
            Identifier id = this.lister.fileToId(fileId);
            List<Resource> stack = entries.getValue();
            T item = null;
            try {
                for (Resource resource : stack) {
                    try (Reader reader = resource.openAsReader()) {
                        DataResult<Optional<T>> parseResult = conditionalCodec.parse(ops, StrictJsonParser.parse(reader));
                        if (parseResult.isSuccess()) {
                            Optional<T> optional = parseResult.getOrThrow();
                            // neoforge conditional ops utilize this to skip loading resources if certain conditions are met, skip this resource
                            if (optional.isEmpty()) {
                                TarkovCraftCore.LOGGER.debug("Skipping loading data file '{}' from '{}' as its conditions were not met", id, fileId);
                                continue;
                            }
                            T resultItem = optional.get();
                            item = item != null ? this.mergeResources(id, item, resultItem) : resultItem;
                        } else {
                            parseResult.ifError(e -> TarkovCraftCore.LOGGER.error("Couldn't parse data file '{}' from '{}': {}", id, fileId, e));
                        }
                    }
                }
                if (item != null) {
                    this.validateResultItem(id, item);
                    result.put(id, item);
                }
            } catch (Exception e) {
                TarkovCraftCore.LOGGER.error("Couldn't parse data file '{}' from '{}'", id, fileId, e);
            }
        }
        return result;
    }
}
