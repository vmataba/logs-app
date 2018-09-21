package tz.co.taba.logs.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Worker {

    public String fname;

    public String lname;

    public String phone;

    public String email;

    //Saves worker details to device's internal storage
    public boolean saveDetails (Context context){

        SharedPreferences preferences = context.getSharedPreferences("tz.co.taba.logs",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String worker = gson.toJson(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("worker",worker);
        return editor.commit();

    }

    //Returns User Object from Local details
    public static Worker getWorker(Context context){
        SharedPreferences preferences = context.getSharedPreferences("tz.co.taba.logs",Context.MODE_PRIVATE);
        String worker_string = preferences.getString("worker","");
        Gson gson = new Gson();
        Worker worker = gson.fromJson(worker_string,Worker.class);
        return worker;
    }


    //Modify phone number to only start with +255
    public void modifyPhone () {
        if (this.phone.startsWith("0")){
            this.phone = this.phone.replace(this.phone.substring(0,1),"+255");

        }
        String first_substring = this.phone.substring(0,4);
        String second_substring = this.phone.substring(5,8);
        String third_substring = this.phone.substring(9,11);
        String fourth_substring = this.phone.substring(12,14);
        this.phone = first_substring+" "+second_substring+" "+third_substring+" "+fourth_substring;
    }

}
