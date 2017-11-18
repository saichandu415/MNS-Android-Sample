package sample.alibabacloud.notificationdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;

import java.util.concurrent.atomic.AtomicBoolean;

import sample.alibabacloud.notificationdemo.asynctasks.RcvNtfcnTask;

/**
 * Created by Sarath Chandra
 */

public class ReceiveService extends Service {

    private final static String TAG = "ReceiveService";
    private final IBinder myBinder = new LocalBinder();

    public ReceiveService(){

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "onBind: IBinder onBind Method");
        return myBinder;
    }


    public class LocalBinder extends Binder {
        public ReceiveService getService() {
            Log.d(TAG, "getService:");
            return ReceiveService.this;
        }
    }


    public void stpMsgTask(){
        Log.d(TAG, "stpMsgTask: Stop message task is called");
        RcvNtfcnTask.setContinueLoop(new AtomicBoolean(false));
        Log.d(TAG, "stpMsgTask: set atomic booelan to false");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
        

    }

    public void isBoundable(){
        Toast.makeText(this,"Yes, I am Boundable", Toast.LENGTH_LONG).show();
    }


    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: ");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                serviceMethod();

            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: ScreenOnReceiver is unregistered");

    }



    public void serviceMethod(){

        CloudAccount account = new CloudAccount(getString(R.string.AccessKey), getString(R.string.AccessKeySecret), "http://5465505358903400.mns.ap-southeast-1.aliyuncs.com");
        MNSClient client = account.getMNSClient();
        RcvNtfcnTask receiveNotification = new RcvNtfcnTask(this);
        receiveNotification.execute(client);

    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }


}
