package sample.alibabacloud.notificationdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Sarath Chandra
 */

public class MessageReceiver extends BroadcastReceiver {

    private final static String TAG = "MessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: MessageReceiver");
        context.startService(new Intent(context,ReceiveService.class));
    }



}
