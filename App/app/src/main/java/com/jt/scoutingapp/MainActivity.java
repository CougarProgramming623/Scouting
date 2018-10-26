package com.jt.scoutingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.print("test2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void disableLatch (View v) {
        Switch sw = findViewById(R.id.switch11);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    findViewById(R.id.switch2).setEnabled(false);
                    //findViewById(R.id.switch2).set
                } else {
                    findViewById(R.id.switch2).setEnabled(true);
                }
            }
        });
    }

    public void disableCrater (View v) {
        Switch sw = findViewById(R.id.switch11);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    //findViewById(R.id.switch2).set
                } else {
                }
            }
        });
    }

}
