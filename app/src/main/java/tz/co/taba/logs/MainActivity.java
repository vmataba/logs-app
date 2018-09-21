package tz.co.taba.logs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tz.co.taba.logs.config.Api;
import tz.co.taba.logs.config.Service;
import tz.co.taba.logs.config.Worker;

public class MainActivity extends AppCompatActivity {

    private EditText fnameView;
    private EditText lnameView;
    private EditText phoneView;
    private EditText emailView;

    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getBaseContext().getSharedPreferences("tz.co.taba.logs", Context.MODE_PRIVATE);
        if (!preferences.contains("worker")){
            this.init();
        } else {
            finish();
            //0676226044 0763839021
        }
    }


    private class Sender extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {

            Api api = new Api();
            return api.get(5000, 5000, strings[0], "");
        }

    }

    //Initializes components
    private void init(){

        fnameView = findViewById(R.id.fnameView);
        lnameView = findViewById(R.id.lnameView);
        phoneView = findViewById(R.id.phoneView);
        emailView = findViewById(R.id.emailView);
        btnSave = findViewById(R.id.btnSave);

        //btnSave Actions
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeWorker();
            }
        });

    }

    //Store worker's details to local storage
    private void storeWorker () {


        Worker w = new Worker();
        w.fname = fnameView.getText().toString();
        w.lname = lnameView.getText().toString();
        w.phone = phoneView.getText().toString();
        //w.modifyPhone();
        w.email = emailView.getText().toString();

        if (this.validate()) {

            if (w.saveDetails(getBaseContext())){
                this.register();

                Intent serviceIntent = new Intent(getBaseContext(),Service.class);
                startService(serviceIntent);
            }

        }
    }

    //Validates form Inputs
    private boolean validate () {
        //fname
        if (fnameView.getText().toString().trim().equals("")){
            fnameView.setError("First name can not be blank");
            fnameView.requestFocus();
            return false;
        }
        //lname
        if (lnameView.getText().toString().trim().equals("")){
            lnameView.setError("Last name can not be blank");
            lnameView.requestFocus();
            return false;
        }
        //phone
        if (phoneView.getText().toString().trim().equals("")){
            phoneView.setError("Phone number can not be blank");
            phoneView.requestFocus();
            return false;
        }
        //email
        if (emailView.getText().toString().trim().equals("")){
            emailView.setError("Email Address can not be blank");
            emailView.requestFocus();
            return false;
        }

        //Validate phone
        String phone = phoneView.getText().toString().trim();
        boolean phone_cond_1;
        phone_cond_1 = phone.length() == 10;
        phone_cond_1 &= phone.startsWith("0");
        boolean phone_cond_2;
        phone_cond_2 = phone.startsWith("+255");
        phone_cond_2 &= phone.length() == 13;
        boolean phone_cond = phone_cond_1 || phone_cond_2;
        if (!phone_cond){
            phoneView.setError("Correct phone number");
            phoneView.requestFocus();
            return false;
        }

        //Validate Email Address
        boolean email_cond = false;
        String email = emailView.getText().toString().trim();
        email_cond = email.contains(".");
        email_cond &= email.contains("@");
        if (!email_cond){
            emailView.setError("Correct email address");
            emailView.requestFocus();
            return false;
        }



        return true;
    }

    //Registers Worker
    @SuppressLint("StaticFieldLeak")
    private void register(){

        String url = Api.BASE_URL + Api.WORKER_REG_API;
        url += "&key="+Api.VALIDATION_KEY;
        url += "&fname="+fnameView.getText().toString().replace(" ","%20");
        url += "&lname="+lnameView.getText().toString().replace(" ","%20");
        url += "&phone="+phoneView.getText().toString().replace(" ","%20");
        url += "&email="+emailView.getText().toString().replace(" ","%20");
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {

                return new Api().get(5000,5000,strings[0],"");
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    int code = object.getInt("code");
                    String res  = object.getString("response");
                    if (code ==1){
                        finish();
                    }
                    Toast.makeText(getBaseContext(),res,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(url);

    }

}
