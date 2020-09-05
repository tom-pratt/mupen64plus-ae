package paulscode.android.mupen64plusae.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;

import paulscode.android.mupen64plusae.game.GameActivity;
import paulscode.android.mupen64plusae.jni.CoreFragment;
import vendor.aptiv.hardware.hisip.V1_0.HisipMessageHidl;
import vendor.aptiv.hardware.hisip.V1_0.IHisip;
import vendor.aptiv.hardware.hisip.V1_0.IHisipListener;

import static com.volvocars.clusterinterface.ClusterConstants.EVENT_STOP;

public class CarController extends AbstractController {
    private static final String ACTION_CLUSTER_EVENT = "com.volvocars.clusterservice.ACTION_CLUSTER_EVENT";

    private static final String EXTRA_BUTTON_PRESS_TYPE = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE";
    private static final String EXTRA_BUTTON_CODE = "com.volvocars.clusterservice.BUTTON_CODE";

    private static final String BUTTON_PRESS_TYPE_DOWN = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE_DOWN";
    private static final String BUTTON_PRESS_TYPE_UP = "com.volvocars.clusterservice.BUTTON_PRESS_TYPE_UP";

    private static final int EVENT_UP = 40;
    private static final int EVENT_DOWN = 50;
    private static final int EVENT_LEFT = 60;
    private static final int EVENT_RIGHT = 70;

    private final GameActivity gameActivity;
    private final Context context;
    private final IHisip hisip;

    private boolean isAButtonPressed = false;
    private boolean goStraight = false;

    public CarController(GameActivity gameActivity, CoreFragment coreFragment, Context context) {
        super(coreFragment);
        this.gameActivity = gameActivity;
        this.context = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CLUSTER_EVENT);

        context.registerReceiver(receiver, intentFilter);

        try {
            hisip = IHisip.getService();
            ArrayList<Byte> ids = new ArrayList<>();
            ids.add((byte) 0x74);
            hisip.addListener(CarController.class.getName(), hisipListener, ids);
        } catch (RemoteException e) {
            throw new RuntimeException("Could not connect to IHisip");
        }
    }

    public void destroy() {
        context.unregisterReceiver(receiver);
        try {
            hisip.removeListener(CarController.class.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private final IHisipListener hisipListener = new IHisipListener.Stub() {
        private final byte signBitMask = (byte) 0xc0;
        private final int beginPos = 5;

        @Override
        public void onMessageFromVip(HisipMessageHidl message) {
            if (goStraight) {
                mState.axisFractionX = 0f;
                notifyChanged(false);
            } else {
                if (message.functionId == (byte) 0x09) {
                    int rawValue = ((message.data.get(beginPos + 1) & ~signBitMask) * 256) + message.data.get(beginPos);
                    boolean negative = (message.data.get(beginPos + 1) & signBitMask) != 0;
                    double value = 0.0009765625 * rawValue;
                    if (negative) {
                        value = value - 16;
                    }

                    float inputValue = (float) -root(2 * value, 3) * 0.5f;

                    inputValue -= 0.1f;

                    mState.axisFractionX = inputValue;
                    notifyChanged(false);
                    Log.d("Tom", (negative ? "negative " : "postitive") + " --- " + "rvalueRadios:" + inputValue);
                }
            }
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String buttonPressType = intent.getStringExtra(EXTRA_BUTTON_PRESS_TYPE);
            int buttonCode = intent.getIntExtra(EXTRA_BUTTON_CODE, 0);

            switch (buttonCode) {
                // Joy stick
                case EVENT_LEFT:
                    if (buttonPressType.equals(BUTTON_PRESS_TYPE_UP)) {
                        isAButtonPressed = !isAButtonPressed;
                        mState.buttons[BTN_A] = isAButtonPressed;
                    }
                    break;
                case EVENT_RIGHT:
                    pressButton(buttonPressType, BTN_Z);
                    break;
                case EVENT_UP:
                    pressButton(buttonPressType, BTN_A);
                    break;
                case EVENT_DOWN:
                    pressButton(buttonPressType, BTN_A);
                    goStraight = !buttonPressType.equals(BUTTON_PRESS_TYPE_UP);
                    break;
                case EVENT_STOP:
                    gameActivity.onExitRequested(true);
                    break;

                // Buttons
//                case BTN_A:
//                    if (buttonPressType.equals(BUTTON_PRESS_TYPE_UP)) {
//                        isAButtonPressed = !isAButtonPressed;
//                        mState.buttons[BTN_A] = isAButtonPressed;
//                    }
//                    break;
                default:
                    pressButton(buttonPressType, buttonCode);
                    break;
            }

            notifyChanged(false);
        }
    };

    public static void keyDown(Context context, int keyCode) {
        Intent intent = new Intent(ACTION_CLUSTER_EVENT);
        intent.putExtra(EXTRA_BUTTON_PRESS_TYPE, BUTTON_PRESS_TYPE_DOWN);
        intent.putExtra(EXTRA_BUTTON_CODE, mapKeyEventToControllerButton(keyCode));
        context.sendBroadcast(intent);
    }

    public static void keyUp(Context context, int keyCode) {
        Intent intent = new Intent(ACTION_CLUSTER_EVENT);
        intent.putExtra(EXTRA_BUTTON_PRESS_TYPE, BUTTON_PRESS_TYPE_UP);
        intent.putExtra(EXTRA_BUTTON_CODE, mapKeyEventToControllerButton(keyCode));
        context.sendBroadcast(intent);
    }

    private static int mapKeyEventToControllerButton(int keyCode) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_4:
                return BTN_A;
            case KeyEvent.KEYCODE_5:
                return BTN_B;
            case KeyEvent.KEYCODE_6:
                return BTN_Z;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                return START;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                return EVENT_LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                return EVENT_RIGHT;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return EVENT_UP;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return EVENT_DOWN;
            default:
                return -1;
        }
    }

    private void pressButton(String buttonPressType, int buttonIndex) {
        switch (buttonPressType) {
            case BUTTON_PRESS_TYPE_DOWN:
                mState.buttons[buttonIndex] = true;
                break;
            case BUTTON_PRESS_TYPE_UP:
                mState.buttons[buttonIndex] = false;
                break;
        }
    }

    public static double root(double num, double root) {
        if (num < 0) {
            return -Math.pow(Math.abs(num), (1 / root));
        }
        return Math.pow(num, 1.0 / root);
    }
}
