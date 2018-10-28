package com.jt.scoutingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    TextView goldValue;
    TextView silverValue;
    TextView penValue;

    Button submitData;

    int goldcounter = 0;
    int silvercounter = 0;
    int pencounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //System.out.print(getExternalFilesDir());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goldValue = findViewById(R.id.goldcounter);
        silverValue = findViewById(R.id.silvercounter);
        penValue = findViewById(R.id.pencounter);

        submitData = findViewById(R.id.sumbit);
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Popup.class));
            }
        });
    }

    public void goldUp (View view) {
        goldcounter++;
        goldValue.setText(Integer.toString(goldcounter));
    }

    public void goldDown (View view) {
        if(goldcounter > 0) {
            goldcounter--;
            goldValue.setText(Integer.toString(goldcounter));
        }
    }
    public void silverUp (View view) {
        silvercounter++;
        silverValue.setText(Integer.toString(silvercounter));
    }

    public void silverDown (View view) {
        if(silvercounter > 0) {
            silvercounter--;
            silverValue.setText(Integer.toString(silvercounter));
        }
    }

    public void penUp (View view) {
        if(pencounter < 0) {
            pencounter += 5;
            penValue.setText(Integer.toString(pencounter));
        }
    }

    public void penDown (View view) {
            pencounter -= 5;
            penValue.setText(Integer.toString(pencounter));
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


}
