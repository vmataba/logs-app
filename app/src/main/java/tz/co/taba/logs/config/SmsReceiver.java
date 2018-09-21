package tz.co.taba.logs.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

import tz.co.taba.logs.config.Api;

public class SmsReceiver extends BroadcastReceiver {

    public static final int FROM_CLIENT = 1;

    public static final int RECEIVED = 1;




    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] sms = null;
        String smsBody = "";
        String sourcePhone = "";
        if (bundle != null) {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            sms = new SmsMessage[pdus.length];
            for (int i = 0; i < sms.length; i++) {
                sms[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sourcePhone +=  sms[i].getDisplayOriginatingAddress();
                smsBody += sms[i].getDisplayMessageBody();
                smsBody += "\r\n";
            }

            smsBody = smsBody.trim().replace(" ","%20");

            String url = Api.BASE_URL + "api/";
            url += "receive-message";
            url += "&key="+Api.VALIDATION_KEY;
            url += "&source_phone="+sourcePhone;
            url += "&dest_phone="+Worker.getWorker(context).phone;
            url += "&content="+smsBody;
            url += "&type="+FROM_CLIENT;
            url += "&status="+RECEIVED;


            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {


                    Api api = new Api();
                    return api.get(5000, 5000, strings[0], "");

                }


            }.execute(url);


        }


    }

}