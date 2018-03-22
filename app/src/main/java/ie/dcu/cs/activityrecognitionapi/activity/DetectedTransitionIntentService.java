package ie.dcu.cs.activityrecognitionapi.activity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;


public class DetectedTransitionIntentService extends IntentService {

    private static String TAG = DetectedTransitionIntentService.class.getSimpleName();

    public DetectedTransitionIntentService() {
        super("DetectedTransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                    // chronological sequence of events....
                    Log.i(TAG, "" + event.getActivityType()); // 7 is walking
                    Log.i(TAG, "" + event.getTransitionType()); // 0 -> enter, 1 -> exit
                    Log.i(TAG, "" + event.getElapsedRealTimeNanos()); // 3076261000000
                }
            }
        }
    }

}
