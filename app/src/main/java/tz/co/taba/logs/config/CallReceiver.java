package tz.co.taba.logs.config;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onReceive(final Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences("call_details",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        String source_phone = null, dest_phone = null,start_time = null,end_time = null;
        int call_type = 0;


        if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
            source_phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            dest_phone = Worker.getWorker(context).phone;
            call_type = 1;
        } else {
            source_phone = Worker.getWorker(context).phone;
            dest_phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            call_type = 2;
        }

        editor.putString("source_phone",source_phone);
        editor.putString("dest_phone",dest_phone);
        editor.putInt("call_type",call_type);

        if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            //Call start
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            start_time  = dateFormat.format(new Date());
            editor.putString("start_time",start_time);
            editor.commit();

        }

        if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
            //Call End
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            end_time  = dateFormat.format(new Date());
            editor.putString("end_time",end_time);

            editor.commit();

            source_phone = preferences.getString("source_phone","");
            dest_phone = preferences.getString("dest_phone","");
            start_time = preferences.getString("start_time","");
            end_time = preferences.getString("end_time","");
            call_type = preferences.getInt("call_type",0);

            boolean condition = source_phone != null && dest_phone !=null && call_type != 0&& start_time != null && end_time != null;

            if (condition) {
                String url = Api.BASE_URL+Api.CALL_API;
                url += "&key="+Api.VALIDATION_KEY;
                url += "&source_phone="+source_phone.trim().replace(" ","%20");
                url += "&dest_phone="+dest_phone.trim().replace(" ","%20");
                url += "&type="+call_type;
                url += "&start_time="+start_time.trim().replace(" ","%20");
                url += "&end_time="+end_time.trim().replace(" ","%20");

                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        return new Api().get(5000,5000,strings[0],"");
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        //Testing
                       // Toast.makeText(context,s,Toast.LENGTH_LONG).show();
                    }
                }.execute(url);

                preferences.edit().clear();

            }
        }










    }
}
