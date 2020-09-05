package paulscode.android.mupen64plusae.input;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.mupen64plusae.v3.alpha.R;

import static com.volvocars.clusterinterface.ClusterConstants.ACTION_CLUSTER_EVENT;
import static com.volvocars.clusterinterface.ClusterConstants.BUTTON_PRESS_TYPE_DOWN;
import static com.volvocars.clusterinterface.ClusterConstants.BUTTON_PRESS_TYPE_UP;
import static com.volvocars.clusterinterface.ClusterConstants.EVENT_DOWN;
import static com.volvocars.clusterinterface.ClusterConstants.EVENT_LEFT;
import static com.volvocars.clusterinterface.ClusterConstants.EVENT_RIGHT;
import static com.volvocars.clusterinterface.ClusterConstants.EVENT_STOP;
import static com.volvocars.clusterinterface.ClusterConstants.EVENT_UP;
import static com.volvocars.clusterinterface.ClusterConstants.EXTRA_BUTTON_CODE;
import static com.volvocars.clusterinterface.ClusterConstants.EXTRA_BUTTON_PRESS_TYPE;
import static paulscode.android.mupen64plusae.input.AbstractController.BTN_A;
import static paulscode.android.mupen64plusae.input.AbstractController.BTN_B;
import static paulscode.android.mupen64plusae.input.AbstractController.START;

public class GalleryControlsViewHolder {
    private final View view;
    private final Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    public GalleryControlsViewHolder(View view, Activity activity) {
        this.view = view;
        this.activity = activity;

        view.findViewById(R.id.textView_up).setOnClickListener(v -> sendButtonPress(EVENT_UP));
        view.findViewById(R.id.textView_down).setOnClickListener(v -> sendButtonPress(EVENT_DOWN));
        view.findViewById(R.id.textView_left).setOnClickListener(v -> sendButtonPress(EVENT_LEFT));
        view.findViewById(R.id.textView_right).setOnClickListener(v -> sendButtonPress(EVENT_RIGHT));
        view.findViewById(R.id.textView_start).setOnClickListener(v -> sendButtonPress(START));
        view.findViewById(R.id.textView_a).setOnClickListener(v -> sendButtonPress(BTN_A));
        view.findViewById(R.id.textView_b).setOnClickListener(v -> sendButtonPress(BTN_B));
        view.findViewById(R.id.textView_stop).setOnClickListener(v -> sendButtonPress(EVENT_STOP));
    }

    public void sendButtonPress(int button) {
        Intent intent = new Intent(ACTION_CLUSTER_EVENT);
        intent.putExtra(EXTRA_BUTTON_PRESS_TYPE, BUTTON_PRESS_TYPE_UP);
        intent.putExtra(EXTRA_BUTTON_CODE, button);
        activity.sendBroadcast(intent);

        view.postDelayed(() -> {
            Intent intent2 = new Intent(ACTION_CLUSTER_EVENT);
            intent2.putExtra(EXTRA_BUTTON_PRESS_TYPE, BUTTON_PRESS_TYPE_DOWN);
            intent2.putExtra(EXTRA_BUTTON_CODE, button);
            activity.sendBroadcast(intent2);

        }, 20);
    }
}
