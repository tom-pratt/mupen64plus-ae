package paulscode.android.mupen64plusae.signalsmanager;

import android.os.Process;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.volvocars.autosar.signals.BaseElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import vendor.volvocars.hardware.signals.V1_0.ISignals;
import vendor.volvocars.hardware.signals.V1_0.ISignalsChangedCallback;

public class SignalsManagerImpl implements SignalsManager {

    private final Gson gson;
    private final ISignals signalsHal;

    SignalsManagerImpl(Gson gson) {
        this.gson = gson;

        try {
            signalsHal = ISignals.getService();
            //TODO death recipient
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(BaseElement element) {
        String json = gson.toJson(element);
        try {
            signalsHal.send(element.getKeyName(), (short)element.getDirection().getCode(), json);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends BaseElement> void subscribe(Class<T> elementClass, Callback<T> callback) {
        try {
            //TODO annoying to use reflection to construct element just to get keyname. Improve code gen.
            Constructor<?> constructor = Arrays.stream(elementClass.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findAny()
                    .get();
            constructor.setAccessible(true);
            T element = (T) constructor.newInstance();
            signalsHal.subscribe(element.getKeyName(), (short) element.getDirection().getCode(), new CallbackWrapper<T>(elementClass, callback), Process.myPid());
        } catch (RemoteException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private class CallbackWrapper<T> extends ISignalsChangedCallback.Stub {
        private final Class elementClass;
        private final Callback<T> callback;

        CallbackWrapper(Class elementClass, Callback<T> callback) {
            this.elementClass = elementClass;
            this.callback = callback;
        }

        @Override
        public void signalChanged(String s, short i, String s1) {
//            Log.d("SignalManager: ",s + " " + i +  " " + s1);
            T element = (T) gson.fromJson(s1, elementClass);
            callback.onSignalChanged(element);
        }
    }
}
