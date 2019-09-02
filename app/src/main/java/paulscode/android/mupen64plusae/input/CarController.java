package paulscode.android.mupen64plusae.input;

import android.util.Log;
import android.view.KeyEvent;

import paulscode.android.mupen64plusae.jni.CoreFragment;

public class CarController extends AbstractController {

    public CarController(CoreFragment coreFragment) {
        super(coreFragment);
    }

    public void onKey(int keyCode, KeyEvent event )
    {
        Log.d("MARIOKART", keyCode + "   " + event.toString());

        switch (keyCode) {

            // Buttons
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                pressButton(event.getAction(), START);
                break;



            // Joystick
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        Log.d("MARIOKART", "left");
                        mState.axisFractionX = -0.9f;
                        break;
                    case KeyEvent.ACTION_UP:
                        Log.d("MARIOKART", "left stop");
                        mState.axisFractionX = 0f;
                        break;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MEDIA_NEXT:

                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        mState.axisFractionX = 0.9f;
                        Log.d("MARIOKART", "right");
                        break;
                    case KeyEvent.ACTION_UP:
                        mState.axisFractionX = 0f;
                        Log.d("MARIOKART", "right stop");
                        break;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
                     Log.d("MARIOKART", "VOLUME UP");

                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        mState.axisFractionY = 0.9f;
                        Log.d("MARIOKART", "right");
                        break;
                    case KeyEvent.ACTION_UP:
                        mState.axisFractionY = 0f;
                        Log.d("MARIOKART", "right stop");
                        break;
                }

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        mState.axisFractionY = -0.9f;
                        Log.d("MARIOKART", "right");
                        break;
                    case KeyEvent.ACTION_UP:
                        mState.axisFractionY = 0f;
                        Log.d("MARIOKART", "right stop");
                        break;
                }

                break;
        }

        notifyChanged();
    }

    private void pressButton(int evenAction, int buttonIndex) {
        switch (evenAction) {
            case KeyEvent.ACTION_DOWN:
                mState.buttons[buttonIndex] = true;
                break;
            case KeyEvent.ACTION_UP:
                mState.buttons[buttonIndex] = false;
                break;
        }
    }
}
