package sample.alibabacloud.notificationdemo.asynctasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;

import java.util.concurrent.atomic.AtomicBoolean;

import sample.alibabacloud.notificationdemo.R;

/**
 * Created by Sarath Chandra
 */

public class RcvNtfcnTask extends AsyncTask<MNSClient, Integer, Void> {

    private final static String TAG = "ReceiveNotificationTask";
    private static AtomicBoolean continueLoop = new AtomicBoolean(true);

    private static AtomicBoolean getContinueLoop() {
        return continueLoop;
    }

    public static void setContinueLoop(AtomicBoolean continueLoop) {
        RcvNtfcnTask.continueLoop = continueLoop;
    }

    Context mContext;

    public RcvNtfcnTask(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    protected Void doInBackground(MNSClient... mnsClients) {


        MNSClient client = mnsClients[0];

        int i = 101;
        while (getContinueLoop().get()) {


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {

                CloudQueue queue = null;
                Message popMsg = null;

                try {
                    queue = client.getQueueRef(mContext.getString(R.string.QueueName_2));// replace with your queue name
                    popMsg = queue.popMessage();
                } catch (NoClassDefFoundError t) {
                    Log.d(TAG, "doInBackground: Throwable : " + t.getMessage());
                }

                if (popMsg != null) {
                    i++;
                    Log.d(TAG, "doInBackground: message handle: " + popMsg.getReceiptHandle());
                    Log.d(TAG, "doInBackground: message body: " + popMsg.getMessageBodyAsString());
                    Log.d(TAG, "doInBackground: message id: " + popMsg.getMessageId());
                    Log.d(TAG, "doInBackground: message dequeue count:" + popMsg.getDequeueCount());

                    Log.d(TAG, "doInBackground: msg Received" + popMsg.getMessageBodyAsString());
                    //remember to  delete message when consume message successfully.

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(mContext)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Notification Demo")
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentText(popMsg.getMessageBodyAsString());

                    NotificationManager mNotificationManager =
                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(i, mBuilder.build());
                    queue.deleteMessage(popMsg.getReceiptHandle());

                    Log.d(TAG, "doInBackground: delete message successfully");
                } else {
                    i = 101;
                    Log.d(TAG, "doInBackground: No message");
                }


            } catch (NoClassDefFoundError e) {
                Log.d(TAG, "doInBackground: No Class Def Dound Error");
                e.printStackTrace();
            } catch (ClientException ce) {
                Log.d(TAG, "doInBackground: Thre is a problem with network and client connection");
                ce.printStackTrace();
            } catch (ServiceException se) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    Log.d(TAG, "doInBackground: Queue is not exist.Please create queue before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    Log.d(TAG, "doInBackground: The request is time expired. Please check your local machine timeclock");
                }

            } catch (Exception e) {
                Log.d(TAG, "doInBackground: Unknown exception happened!");
                e.printStackTrace();
            }
        }
        return null;
    }


}

