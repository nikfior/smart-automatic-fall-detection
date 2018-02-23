package com.nis.testt2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;


import java.util.Calendar;
import java.util.Date;



public class SettingsActivity extends AppCompatActivity {

    CountDownTimer timer;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //-------add to history------------------

        SharedPreferences shared;
        shared = getSharedPreferences("history_file", MODE_PRIVATE);
        String date_set = shared.getString("dates_list", null);

        StringBuilder sb = new StringBuilder();



        SharedPreferences.Editor editor = shared.edit();

        Date currentTime = Calendar.getInstance().getTime();
        String dates=currentTime.toString();

        sb.append(dates).append(",");

        if(date_set!=null){
            String[] date_set2 = date_set.split(",");

            for (int i = 0; i < date_set2.length; i++) {
                sb.append(date_set2[i]).append(",");
            }
        }


        editor.putString("dates_list", sb.toString());
        editor.apply();

        //---------------------------------------



        Button button = (Button) findViewById(R.id.button2);


        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){


                        finish();

                    }
                }
        );



       if(globalvariables.callphone) {


           timer = new CountDownTimer(10000, 10000) {
               @Override
               public void onTick(long millisUntilFinished) {

               }

               @Override
               public void onFinish() {
                   Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + globalvariables.telephone));


                   if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {


                       startActivity(intent);
                       Toast.makeText(SettingsActivity.this, "Calling phone number... ", Toast.LENGTH_LONG).show();
                   } else if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                       Toast.makeText(SettingsActivity.this, "Cannot call. No permission granted to the app.", Toast.LENGTH_LONG).show();
                   } else {
                       Toast.makeText(SettingsActivity.this, "Problem calling", Toast.LENGTH_LONG).show();
                   }


                   alert.dismiss();
               }
           };


           AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
           builder.setTitle("Checking your condition");
           builder.setMessage("Press OK to confirm that you are ok, otherwise we will call your requested number within 10 seconds");
           builder.setCancelable(false);
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   timer.cancel();
                   dialog.dismiss();
               }
           });

           alert = builder.create();

           timer.start();
           alert.show();



       }





    }
}
