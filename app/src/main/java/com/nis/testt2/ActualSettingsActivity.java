package com.nis.testt2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;


public class ActualSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_settings);

        EditText editText3=(EditText) findViewById(R.id.editText3);
        EditText editText4=(EditText) findViewById(R.id.editText4);
        EditText editText5=(EditText) findViewById(R.id.editText5);

        editText3.setText(""+globalvariables.minthresh);
        editText4.setText(""+globalvariables.maxthresh);
        editText5.setText(""+globalvariables.fallimpactdifferencetime);


        SwitchCompat switch1 = (SwitchCompat) findViewById(R.id.switch1);
        switch1.setChecked(globalvariables.economymodesetting);

        SwitchCompat switch3 = (SwitchCompat) findViewById(R.id.switchCompat);
        switch3.setChecked(globalvariables.runinbackground);

        SeekBar seekbar= (SeekBar) findViewById(R.id.seekBar);
        seekbar.setProgress(globalvariables.seekbarvalue);

        SwitchCompat switch2 = (SwitchCompat) findViewById(R.id.switchCompat2);
        switch2.setChecked(globalvariables.callphone);
        EditText editText6=(EditText) findViewById(R.id.editText);
        editText6.setText(""+globalvariables.telephone);



        Button button = (Button) findViewById(R.id.button4);



        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        globalvariables.minthresh = Float.parseFloat( ((EditText) findViewById(R.id.editText3)).getText().toString());
                        globalvariables.maxthresh = Float.parseFloat( ((EditText) findViewById(R.id.editText4)).getText().toString());
                        globalvariables.fallimpactdifferencetime = Long.parseLong( ((EditText) findViewById(R.id.editText5)).getText().toString(),10);


                        globalvariables.economymodesetting=((SwitchCompat) findViewById(R.id.switch1)).isChecked();
                        globalvariables.runinbackground=((SwitchCompat) findViewById(R.id.switchCompat)).isChecked();

                        globalvariables.callphone= ((SwitchCompat) findViewById(R.id.switchCompat2)).isChecked();
                        globalvariables.telephone= ((EditText) findViewById(R.id.editText)).getText().toString();


                        //-----SharedPreferences stuff
                        SharedPreferences sharedpref = getSharedPreferences("SettingsStuff", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putFloat("minthresh",globalvariables.minthresh);
                        editor.putFloat("maxthresh",globalvariables.maxthresh);
                        editor.putLong("fallimpactdifferencetime",globalvariables.fallimpactdifferencetime);
                        editor.putBoolean("economymodesetting",globalvariables.economymodesetting);
                        editor.putInt("seekbarvalue",globalvariables.seekbarvalue);
                        editor.putBoolean("callphone",globalvariables.callphone);
                        editor.putString("telephone",globalvariables.telephone);
                        editor.putBoolean("runinbackground",globalvariables.runinbackground);
                        editor.apply();


                        /////--------------------------------------




                        Toast.makeText(ActualSettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        );




        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        globalvariables.seekbarvalue= seekBar.getProgress();

                        globalvariables.maxthresh =  30-((float) seekBar.getProgress())*3/10;

                        if(seekBar.getProgress()<60){
                            globalvariables.minthresh = ((float)seekBar.getProgress())*4/100;
                        }
                        else{
                            globalvariables.minthresh = (float) ((((float)seekBar.getProgress())-60)*6.5/40+2.5);
                        }



                        ((EditText) findViewById(R.id.editText3)).setText(""+globalvariables.minthresh);
                        ((EditText) findViewById(R.id.editText4)).setText(""+globalvariables.maxthresh);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


    }
}
