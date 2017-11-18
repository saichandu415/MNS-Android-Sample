package sample.alibabacloud.notificationdemo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;

import java.util.Calendar;

/**
 * Created by Sarath Chandra
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static String ntfcnTxt = "";
    ConstraintLayout coordinatorLayout;
    MNSClient client, client2;
    TextView opText;
    ReceiveService mBoundService;
    boolean mIsBound = true;
    CloudAccount account, account2;
    Handler handler = new Handler();
    ProgressDialog progressDialog;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: Connected");
            mBoundService = ((ReceiveService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: Not Connected");
            mBoundService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        opText = findViewById(R.id.opText);
        progressDialog = new ProgressDialog(this);
        initClient();


        Button sendButton = findViewById(R.id.sendButton);
        Button receiveButton = findViewById(R.id.receiveButton);
        Button sendNotification = findViewById(R.id.sendNotification);

        sendButton.setOnClickListener(this);
        receiveButton.setOnClickListener(this);
        sendNotification.setOnClickListener(this);

        startRepeatingTimer();
    }

    private void initClient() {

        account = new CloudAccount(getString(R.string.AccessKey), getString(R.string.AccessKeySecret), "http://5465505358903400.mns.ap-southeast-1.aliyuncs.com");
        account2 = new CloudAccount(getString(R.string.AccessKey), getString(R.string.AccessKeySecret), "http://5465505358903400.mns.ap-southeast-3.aliyuncs.com");
        client = account.getMNSClient();
        client2 = account2.getMNSClient();

        Log.d(TAG, "initClient: acct is open" + client.isOpen());

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.sendButton) {
            Log.d(TAG, "onClick: Send Btn Clicked");
            progressDialog.show();
            Thread newThread = new Thread(new SendMessageTask());
            newThread.start();
        } else if (id == R.id.receiveButton) {
            Log.d(TAG, "onClick: Receive Btn Clicked");
            progressDialog.show();
            Thread newThread = new Thread(new ReceiveMessageTask());
            newThread.start();
        } else if (id == R.id.sendNotification) {
            Log.d(TAG, "onClick: Subscribing to the Topic msg to Topic");
//            progressDialog.show();


            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.inputdialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    ntfcnTxt = userInput.getText().toString();
                                    Thread newThread = new Thread(new SendNotificationTask());
                                    newThread.start();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

    }

    private void startRepeatingTimer() {
        Log.d(TAG, "startRepeatingTimer: start Repeating Timer");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, ReceiveService.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), System.currentTimeMillis() + (1000 * 60 * 5), pi);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onClick: Starting the Service");
        doBindService();
    }

    private void doBindService() {
        Log.d(TAG, "doBindService: ");
        bindService(new Intent(this, ReceiveService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "doBindService: bindService Called");
        mIsBound = true;
        if (mBoundService != null) {
            Log.d(TAG, "doBindService: is Boundable");
            mBoundService.isBoundable();
        }


    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    class SendMessageTask implements Runnable {
        private static final String TAG = "MainAct.SndMessTask";


        StringBuilder msgsRcvd = new StringBuilder();

        @Override
        public void run() {
            CloudAccount account2 = new CloudAccount(getApplicationContext().getString(R.string.AccessKey), getApplicationContext().getString(R.string.AccessKeySecret), getApplicationContext().getString(R.string.Endpoint));
            MNSClient client2 = account2.getMNSClient();

            try {
                Log.d(TAG, "doInBackground: inside try");
                CloudQueue queue = client2.getQueueRef(getApplicationContext().getString(R.string.QueueName_1));// replace with your queue name
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "doInBackground: inside for loop :: " + i + 1);
                    final int k = i + 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Sending Message : " + (k + 1));
                        }
                    });

                    Message message = new Message();
                    message.setMessageBody("This is message" + i);
                    Message putMsg = queue.putMessage(message);
                    Log.d(TAG, "Send message id is: " + putMsg.getMessageId());
                }

                handler.post(new Runnable() {
                    public void run() {
                        opText.setText("All Messages Sent.");
                        progressDialog.dismiss();
                    }
                });
            } catch (ClientException ce) {
                Log.d(TAG, "sendButton: Something wrong with the network connection between client and MNS service.Please check your network and DNS availablity.");
                ce.printStackTrace();
            } catch (ServiceException se) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    Log.d(TAG, "sendButton: Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    Log.d(TAG, "sendButton: The request is time expired. Please check your local machine timeclock");
                }
                se.printStackTrace();
            } catch (Exception e) {
                Log.d(TAG, "sendButton: Unknown exception happened!");
                e.printStackTrace();
            }

        }
    }

    class ReceiveMessageTask implements Runnable {
        private static final String TAG = "MainAct.SndMessTask";

        @Override
        public void run() {
            CloudAccount account2 = new CloudAccount(getApplicationContext().getString(R.string.AccessKey), getApplicationContext().getString(R.string.AccessKeySecret), getApplicationContext().getString(R.string.Endpoint));
            final StringBuilder outputMsg = new StringBuilder();

            MNSClient client2 = account2.getMNSClient();
            try {
                Log.d(TAG, "doInBackground: inside try");
                CloudQueue queue = client2.getQueueRef(getApplicationContext().getString(R.string.QueueName_1));
                for (int i = 0; i < 10; i++) {
                    Message popMsg = queue.popMessage();
                    if (popMsg != null) {
                        Log.d(TAG, "doInBackground: message handle: " + popMsg.getReceiptHandle());
                        Log.d(TAG, "doInBackground: message body: " + popMsg.getMessageBodyAsString());
                        Log.d(TAG, "doInBackground: message id: " + popMsg.getMessageId());
                        Log.d(TAG, "doInBackground: message dequeue count:" + popMsg.getDequeueCount());


                        final int k = i + 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Receive Message " + k);
                            }
                        });


                        outputMsg.append(popMsg.getMessageBodyAsString()).append(".\n");
                        Log.d(TAG, "doInBackground: msg Received" + popMsg.getMessageBodyAsString());


                        queue.deleteMessage(popMsg.getReceiptHandle());

                        Log.d(TAG, "doInBackground: delete message successfully");
                    } else {
                        Log.d(TAG, "doInBackground: No message");

                    }

                }

                handler.post(new Runnable() {
                    public void run() {
                        opText.setText("Messages Received :\n" + outputMsg);
                        progressDialog.dismiss();
                    }
                });
            } catch (ClientException ce) {
                Log.d(TAG, "sendButton: Something wrong with the network connection between client and MNS service.Please check your network and DNS availablity.");
                ce.printStackTrace();
            } catch (ServiceException se) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    Log.d(TAG, "sendButton: Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    Log.d(TAG, "sendButton: The request is time expired. Please check your local machine timeclock");
                }
            /*
            you can get more MNS service error code from following link:
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
                se.printStackTrace();
            } catch (Exception e) {
                Log.d(TAG, "sendButton: Unknown exception happened!");
                e.printStackTrace();
            }

        }
    }

    class SendNotificationTask implements Runnable {
        private static final String TAG = "SendNotificationTask";

        @Override
        public void run() {
            CloudAccount account = new CloudAccount(getApplicationContext().getString(R.string.AccessKey), getApplicationContext().getString(R.string.AccessKeySecret), getApplicationContext().getString(R.string.Endpoint));
            MNSClient client = account.getMNSClient();

            try {
                Log.d(TAG, "doInBackground: inside try");
                CloudQueue queue = client.getQueueRef(getApplicationContext().getString(R.string.QueueName_2));// replace with your queue name
                Log.d(TAG, "doInBackground: inside for loop :: ");
                Message message = new Message();
                message.setMessageBody(ntfcnTxt); // use your own message body here
                Message putMsg = queue.putMessage(message);
                Log.d(TAG, "Send message id is: " + putMsg.getMessageId());

                handler.post(new Runnable() {
                    public void run() {
                        progressDialog.setMessage("Notification Sent");
                        opText.setText("Notification sent.");
                        progressDialog.dismiss();
                    }
                });
            } catch (ClientException ce) {
                Log.d(TAG, "sendButton: Something wrong with the network connection between client and MNS service.Please check your network and DNS availablity.");
                ce.printStackTrace();
            } catch (ServiceException se) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    Log.d(TAG, "sendButton: Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    Log.d(TAG, "sendButton: The request is time expired. Please check your local machine timeclock");
                }
                se.printStackTrace();
            } catch (Exception e) {
                Log.d(TAG, "sendButton: Unknown exception happened!");
                e.printStackTrace();
            }


        }
    }


}
