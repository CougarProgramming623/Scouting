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
    TextView vault;
    TextView switchValue;
    TextView scaleValue;

    Button submitData;

    int goldcounter = 0;
    int silvercounter = 0;
    int pencounter = 0;
    int vaultcounter = 0;
    int switchcouner = 0;
    int scalecounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //System.out.print(getExternalFilesDir());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frc2018);

        goldValue = findViewById(R.id.goldcounter);
        silverValue = findViewById(R.id.silvercounter);
        penValue = findViewById(R.id.pencounter3);
        vault = findViewById(R.id.pencounter);
        switchValue = findViewById(R.id.goldcounter2);
        scaleValue = findViewById(R.id.goldcounter3);

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

    public void vaultUp (View view) {
            vaultcounter += 1;
            vault.setText(Integer.toString(vaultcounter));
    }

    public void vaultDown (View view) {
        if(vaultcounter >= 1) {
            vaultcounter -= 1;
            vault.setText(Integer.toString(vaultcounter));
        }
    }

    public void switchUp (View view) {
        switchcouner += 1;
        switchValue.setText(Integer.toString(switchcouner));
    }

    public void switchDown (View view) {
        if(switchcouner >= 1) {
            switchcouner -= 1;
            switchValue.setText(Integer.toString(switchcouner));
        }
    }

    public void scaleUp (View view) {
        scalecounter += 1;
        scaleValue.setText(Integer.toString(scalecounter));
    }

    public void scaleDown (View view) {
        if(scalecounter >= 1) {
            scalecounter -= 1;
            scaleValue.setText(Integer.toString(scalecounter));
        }
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
