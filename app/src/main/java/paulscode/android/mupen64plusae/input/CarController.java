package paulscode.android.mupen64plusae.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import paulscode.android.mupen64plusae.jni.CoreFragment;

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

    private boolean isAButtonPressed = false;

    public CarController(CoreFragment coreFragment, Context context) {
        super(coreFragment);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CLUSTER_EVENT);

        context.registerReceiver(receiver, intentFilter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String buttonPressType = intent.getStringExtra(EXTRA_BUTTON_PRESS_TYPE);
            int buttonCode = intent.getIntExtra(EXTRA_BUTTON_CODE, 0);

            switch (buttonCode) {
                // Joy stick
                case EVENT_LEFT:
                    switch (buttonPressType) {
                        case BUTTON_PRESS_TYPE_DOWN:
                            mState.axisFractionX = -0.9f;
                            break;
                        case BUTTON_PRESS_TYPE_UP:
                            mState.axisFractionX = 0f;
                            break;
                    }
                    break;
                case EVENT_RIGHT:
                    switch (buttonPressType) {
                        case BUTTON_PRESS_TYPE_DOWN:
                            mState.axisFractionX = 0.9f;
                            break;
                        case BUTTON_PRESS_TYPE_UP:
                            mState.axisFractionX = 0f;
                            break;
                    }
                    break;
                case EVENT_UP:
                    switch (buttonPressType) {
                        case BUTTON_PRESS_TYPE_DOWN:
                            mState.axisFractionY = 0.9f;
                            break;
                        case BUTTON_PRESS_TYPE_UP:
                            mState.axisFractionY = 0f;
                            break;
                    }
                    break;
                case EVENT_DOWN:
                    switch (buttonPressType) {
                        case BUTTON_PRESS_TYPE_DOWN:
                            mState.axisFractionY = -0.9f;
                            break;
                        case BUTTON_PRESS_TYPE_UP:
                            mState.axisFractionY = 0f;
                            break;
                    }
                    break;

                // Buttons
                case BTN_A:
                    if (buttonPressType.equals(BUTTON_PRESS_TYPE_UP)) {
                        isAButtonPressed = !isAButtonPressed;
                        mState.buttons[BTN_A] = isAButtonPressed;
                    }
                    break;
                default:
                    pressButton(buttonPressType, buttonCode);
                    break;
            }

            notifyChanged();
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
}
