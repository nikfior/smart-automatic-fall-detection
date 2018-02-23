package com.nis.testt2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;


public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        Button button5 = (Button) findViewById(R.id.button5);

        button5.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        finish();
                    }
                }
        );




        Button button3 = (Button) findViewById(R.id.button3);

        button3.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        SharedPreferences shared;
                        shared = getSharedPreferences("history_file", MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.remove("dates_list");
                        editor.apply();


                        ListView listview ;

                        listview = (ListView) findViewById(R.id.listview1);

                        listview.setAdapter(null);


                    }
                }
        );



        ListView listview ;

        listview = (ListView) findViewById(R.id.listview1);



        SharedPreferences shared;


        shared = getSharedPreferences("history_file", MODE_PRIVATE);

        String date_set = shared.getString("dates_list", null);

        if(date_set!=null) {
            String[] date_set2 = date_set.split(",");

            ListAdapter adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, date_set2);


            listview.setAdapter(adapter);

        }else{

            listview.setAdapter(null);
        }





    }
}
