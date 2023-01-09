package de.michiruf.invsync.mixin;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import de.michiruf.invsync.InvSync;
import de.michiruf.invsync.Logger;
import de.michiruf.invsync.mixin_accessor.PlayerAdvancementTrackerAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.text.MessageFormat;
import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin implements PlayerAdvancementTrackerAccessor {

    private static final String DATA_VERSION_PROPERTY = "DataVersion";

    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancementToProgress;

    @Shadow
    protected abstract void initProgress(Advancement advancement, AdvancementProgress progress);

    @Shadow
    protected abstract void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader);

    @Shadow
    @Final
    private static Gson GSON;

    @Shadow
    @Final
    private static TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE;

    @Shadow
    @Final
    private DataFixer dataFixer;

    @Override
    public synchronized void writeAdvancementData(JsonElement advancementData) {
        // NOTE If everything is done, that is normally done when handling advancements, a
        // ConcurrentModificationException occurs
        // But since we want just the state to be up-to-date, it is totally okay not to do everything

        // Therefore, this should not be needed to do so?
        //clearCriteria();
        //advancementToProgress.clear();
        //visibleAdvancements.clear();
        //visibilityUpdates.clear();
        //progressUpdates.clear();
        //dirty = true;
        //currentDisplayTab = null;

        Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, GSON.fromJson(advancementData, JsonElement.class));
        // Code got from PlayerAdvancementTracker#load(ServerAdvancementLoader)
        if (dynamic.get(DATA_VERSION_PROPERTY).asNumber().result().isEmpty()) {
            dynamic = dynamic.set(DATA_VERSION_PROPERTY, dynamic.createInt(1343));
        }
        dynamic = dataFixer.update(
                DataFixTypes.ADVANCEMENTS.getTypeReference(),
                dynamic,
                dynamic.get(DATA_VERSION_PROPERTY).asInt(0),
                SharedConstants.getGameVersion().getWorldVersion());
        dynamic = dynamic.remove(DATA_VERSION_PROPERTY);

        var map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
        if (map == null) {
            throw new JsonParseException("Found null for advancements");
        }

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    Advancement advancement = InvSync.instance.advancementLoader.get(entry.getKey());
                    if (advancement == null) {
                        Logger.log(Level.WARN, MessageFormat.format("Ignored advancement '{0}' - it doesn't exist anymore?", entry.getKey()));
                        return;
                    }

                    this.initProgress(advancement, entry.getValue());
                });

        /* {@link PlayerAdvancementTracker#load(ServerAdvancementLoader) */
        // This should not be needed to do so?
        //rewardEmptyAdvancements(InvSync.instance.advancementLoader);
        //updateCompleted();
        beginTrackingAllAdvancements(InvSync.instance.advancementLoader);
    }

    @Override
    public synchronized JsonElement readAdvancementData() {
        Map<Identifier, AdvancementProgress> map = Maps.newHashMap();
        for (var entry : advancementToProgress.entrySet()) {
            AdvancementProgress advancementProgress = entry.getValue();
            if (!advancementProgress.isAnyObtained())
                continue;
            map.put(entry.getKey().getId(), advancementProgress);
        }

        JsonElement jsonElement = GSON.toJsonTree(map);
        jsonElement.getAsJsonObject().addProperty(DATA_VERSION_PROPERTY, SharedConstants.getGameVersion().getWorldVersion());
        return jsonElement;
    }
}
