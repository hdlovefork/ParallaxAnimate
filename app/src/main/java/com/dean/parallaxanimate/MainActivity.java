package com.dean.parallaxanimate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dean.parallaxanimate.data.Cheeses;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lvMain = (ListView) findViewById(R.id.lv_main);
        View llHeadContainer = LayoutInflater.from(this)
                                             .inflate(R.layout.view_list_head, lvMain, false);
        lvMain.addHeaderView(llHeadContainer);
        lvMain.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
    }
}
