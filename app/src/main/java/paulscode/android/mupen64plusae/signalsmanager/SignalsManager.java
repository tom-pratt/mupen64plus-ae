package paulscode.android.mupen64plusae.signalsmanager;

import androidx.annotation.NonNull;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volvocars.autosar.signals.BaseElement;

public interface SignalsManager {
    static SignalsManager create() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
                // TODO fix carsim and remove this
                .addDeserializationExclusionStrategy(new SkipTimestamp())
                .create();
        return new SignalsManagerImpl(gson);
    }

    interface Callback<T> {
        void onSignalChanged(@NonNull T s1);
    }

    <T extends BaseElement> void send(T element);

    <T extends BaseElement> void subscribe(Class<T> elementClass, Callback<T> callback);

    // TODO fix carsim and remove this
    // Carsim is sending through decimal timestamps, should be floats
    class SkipTimestamp implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals("mTimeStamp");
        }
    }
}
