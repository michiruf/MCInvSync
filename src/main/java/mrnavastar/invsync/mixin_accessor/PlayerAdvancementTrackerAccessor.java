package mrnavastar.invsync.mixin_accessor;

import com.google.gson.JsonElement;

public interface PlayerAdvancementTrackerAccessor {

    void writeAdvancementData(JsonElement advancementData);

    JsonElement readAdvancementData();
}
