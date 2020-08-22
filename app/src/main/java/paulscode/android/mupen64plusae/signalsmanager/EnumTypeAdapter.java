package paulscode.android.mupen64plusae.signalsmanager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum adapter for {@link Gson}.
 * Referenced from -> https://stackoverflow.com/a/49199095/966550
 * @author Waqas Aslam
 */
public class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    private final static String TAG = "EnumTypeAdapter";

    private final Map<Integer, T> nameToConstant = new HashMap<>();
    private final Map<T, Integer> constantToName = new HashMap<>();

    /**
     * Private constructor.
     * @param classOfT
     * @throws Exception
     */
    private EnumTypeAdapter(Class<T> classOfT) throws Exception {
        // Log.e(TAG, "Created for: " + classOfT.getName());

        // loop through each enum name
        for (T constant : classOfT.getEnumConstants()) {
            // Integer name = constant.ordinal();

            // get annotation for the enum name
            final SerializedName annotation = classOfT.getField(constant.name()).getAnnotation(SerializedName.class);

            // convert annotation name to int value
            final Integer annotatedValue = Integer.valueOf(annotation.value());

            nameToConstant.put(annotatedValue, constant);
            constantToName.put(constant, annotatedValue);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        final int intVal = in.nextInt();
        final T enumName = nameToConstant.get(intVal);

        // Log.v(TAG, String.format("type: %s, name: %s, annotationVal: %d",
        //        enumName.getClass().getName(), enumName.name(), intVal));

        return enumName;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        // Log.e(TAG, "in write: " + value.name());
        out.value(null == value ? null : constantToName.get(value));
    }

    /**
     * Static factory instance for {@link Enum}.
     */
    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                return null;
            }
            if (!rawType.isEnum()) {
                rawType = rawType.getSuperclass(); // handle anonymous subclasses
            }

            // TODO: 2018-10-04 check if the enum is only for autosar

            TypeAdapter<T> adapter = null;

            try {
                adapter = new EnumTypeAdapter(rawType);
            } catch (Exception e) {
                Log.e(TAG,
                        "Enum name may be missing annotation field or the value is not "
                                + "integer. A probable place to look for is your autosar generated enums",
                        e);
            }

            return adapter;
        }
    };
}
