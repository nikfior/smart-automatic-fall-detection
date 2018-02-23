package com.nis.testt2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView,textView2,textView3,textView4,minmax1,minmax2,minmax3,maxtotaltextview;
    private float[][] minmax;
    private float sensorx,sensory,sensorz;

    private Sensor mySensor;
    private SensorManager SM;

    private float total,maxtotal,mintotal;
    private long falldetecttime;
    private static boolean falldetect,impactdetect;

    private SharedPreferences sharedpref;
    private boolean SimpleAdvancedToggle;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //SharedPreferences stuff
        sharedpref = getSharedPreferences("SettingsStuff", Context.MODE_PRIVATE);

        globalvariables.minthresh = sharedpref.getFloat("minthresh", 2);
        globalvariables.maxthresh = sharedpref.getFloat("maxthresh", 14);
        globalvariables.fallimpactdifferencetime = sharedpref.getLong("fallimpactdifferencetime", 1500);
        globalvariables.economymodesetting = sharedpref.getBoolean("economymodesetting", false);
        globalvariables.seekbarvalue = sharedpref.getInt("seekbarvalue", 50);
        globalvariables.callphone = sharedpref.getBoolean("callphone", false);
        globalvariables.telephone = sharedpref.getString("telephone","");
        globalvariables.runinbackground = sharedpref.getBoolean("runinbackground",false);
        SimpleAdvancedToggle = sharedpref.getBoolean("SimpleAdvancedToggle",false);



        /////--------------------------------------




        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , "WakeLock:Accelerometer");




        minmax=new float[3][2];

        for(int i=0;i<3;i++)
            for(int j=0;j<2;j++)
                minmax[i][j]= (j==0)? 9999:-9999 ;

        maxtotal=-9999;
        mintotal=9999;


        falldetect=false;
        impactdetect=false;


        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        minmax1 = (TextView) findViewById(R.id.minmax1);
        minmax2 = (TextView) findViewById(R.id.minmax2);
        minmax3 = (TextView) findViewById(R.id.minmax3);
        maxtotaltextview=(TextView) findViewById(R.id.maxtotaltextview);



        Button button = (Button) findViewById(R.id.button);



        button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        for(int i=0;i<3;i++){
                            for(int j=0;j<2;j++){
                                minmax[i][j]= (j==0)? 9999:-9999 ;  }}
                        maxtotal=-9999;
                        mintotal=9999;



                    }
                }
        );





        if(SimpleAdvancedToggle==true){
            turnToSimpleView();
        }


        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);

        toggle.setChecked(SimpleAdvancedToggle);

        toggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putBoolean("SimpleAdvancedToggle",isChecked);
                        editor.apply();

                        if(isChecked) {
                            turnToSimpleView();
                        }else{
                            turnToAdvancedView();
                        }

                    }
                }
        );



        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        sensorx=event.values[0];
        sensory=event.values[1];
        sensorz=event.values[2];

        textView.setText(""+sensorx);
        textView2.setText(""+sensory);
        textView3.setText(""+sensorz);
        total=(float) Math.sqrt(sensorx*sensorx+sensory*sensory+sensorz*sensorz);
        textView4.setText(""+total);

        if (sensorx<minmax[0][0]){
            minmax[0][0]=sensorx;
        }
        if (sensorx>minmax[0][1]){
            minmax[0][1]=sensorx;
        }

        if (sensory<minmax[1][0]){
            minmax[1][0]=sensory;
        }
        if (sensory>minmax[1][1]){
            minmax[1][1]=sensory;
        }

        if (sensorz<minmax[2][0]){
            minmax[2][0]=sensorz;
        }
        if (sensorz>minmax[2][1]){
            minmax[2][1]=sensorz;
        }

        if(maxtotal<total){
            maxtotal=total;
        }
        if(mintotal>total){
            mintotal=total;
        }


        minmax1.setText("min: "+minmax[0][0]+" |  max: "+minmax[0][1]);
        minmax2.setText("min: "+minmax[1][0]+" |  max: "+minmax[1][1]);
        minmax3.setText("min: "+minmax[2][0]+" |  max: "+minmax[2][1]);
        maxtotaltextview.setText("mintotal: "+mintotal+" | maxtotal: "+maxtotal);





        //detect fall
        if(total<(globalvariables.minthresh)){
            falldetect=true;
            falldetecttime= System.currentTimeMillis();
        }



        //detect impact
        if(falldetect && (total>(globalvariables.maxthresh)) ){
            if((System.currentTimeMillis()-falldetecttime)<(globalvariables.fallimpactdifferencetime)){
                impactdetect=true;
            }
            else{
                falldetect=false;
            }
        }


//if(sensorx>5){                                    //for testing
        if(falldetect && impactdetect){
            falldetect=false;
            impactdetect=false;
            SM.unregisterListener(this);

            //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //startActivity(intent);

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onPause() {
        super.onPause();

        if((globalvariables.runinbackground)==false){
            SM.unregisterListener(this);

            if(mWakeLock.isHeld()){
                mWakeLock.release();
            }

        }else{
            if( !(mWakeLock.isHeld()) ){
                mWakeLock.acquire();
            }
        }

/*          //re-register sensors when onPause

        Runnable runnable = new Runnable() {
            public void run() {
                SM.unregisterListener(MainActivity.this);
                if(globalvariables.economymodesetting){
                    SM.registerListener(MainActivity.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);}
                else{
                    SM.registerListener(MainActivity.this, mySensor, SensorManager.SENSOR_DELAY_FASTEST);}
                Toast.makeText(MainActivity.this, "In runnable", Toast.LENGTH_SHORT).show();

            }
        };

        new Handler().postDelayed(runnable, 1500);

*/
    }

    //----------re-register sensors when user puts device on sleep
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    SM.unregisterListener(MainActivity.this);
                    if(globalvariables.economymodesetting){
                        SM.registerListener(MainActivity.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);}
                    else{
                        SM.registerListener(MainActivity.this, mySensor, SensorManager.SENSOR_DELAY_FASTEST);}


                }
            };

            new Handler().postDelayed(runnable, 2000);
        }
    };

    //------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        SM.unregisterListener(this);
        if(mWakeLock.isHeld()){
            mWakeLock.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SM.unregisterListener(this);
        if(globalvariables.economymodesetting){
            SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);}
        else{
            SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_FASTEST);}
    }


    public void turnToSimpleView(){

        textView.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        minmax1.setVisibility(View.GONE);
        minmax2.setVisibility(View.GONE);
        minmax3.setVisibility(View.GONE);
        (findViewById(R.id.line1)).setVisibility(View.GONE);
        (findViewById(R.id.line2)).setVisibility(View.GONE);
        (findViewById(R.id.line3)).setVisibility(View.GONE);
        (findViewById(R.id.textView5)).setVisibility(View.GONE);
        (findViewById(R.id.textView6)).setVisibility(View.GONE);
        (findViewById(R.id.textView7)).setVisibility(View.GONE);

        ((ConstraintLayout) findViewById(R.id.layout1)).setBackgroundColor(0xFF142237);

        ((TextView) findViewById(R.id.textView8)).setTextColor(Color.WHITE);
        ((TextView) findViewById(R.id.textView4)).setTextColor(Color.WHITE);
        ((TextView) findViewById(R.id.maxtotaltextview)).setTextColor(Color.WHITE);

        ((ImageView) findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
    }

    public void turnToAdvancedView(){
        textView.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        minmax1.setVisibility(View.VISIBLE);
        minmax2.setVisibility(View.VISIBLE);
        minmax3.setVisibility(View.VISIBLE);
        (findViewById(R.id.line1)).setVisibility(View.VISIBLE);
        (findViewById(R.id.line2)).setVisibility(View.VISIBLE);
        (findViewById(R.id.line3)).setVisibility(View.VISIBLE);
        (findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
        (findViewById(R.id.textView6)).setVisibility(View.VISIBLE);
        (findViewById(R.id.textView7)).setVisibility(View.VISIBLE);

        ((ConstraintLayout) findViewById(R.id.layout1)).setBackgroundColor(Color.WHITE);

        ((TextView) findViewById(R.id.textView8)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.textView4)).setTextColor(Color.BLACK);
        ((TextView) findViewById(R.id.maxtotaltextview)).setTextColor(Color.BLACK);

        ((ImageView) findViewById(R.id.imageView)).setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu_gear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_gear:
                SM.unregisterListener(this);
                Intent intent = new Intent(MainActivity.this, ActualSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.history_icon:
                Intent intent2 = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
