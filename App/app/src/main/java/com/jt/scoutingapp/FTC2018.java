package com.jt.scoutingapp;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jt.scoutcore.MatchSubmission;


public class FTC2018 extends SimpleScoutingActivity {

    private TextView silver, gold, penalty;
    private Switch claiming, landing, parking, latch;
    private RadioGroup craterstatus, samplestatus;
    private RadioButton nosample, notincrater;


    public void createImpl() {
        setContentView(R.layout.ftc2018);

        silver = findViewById(R.id.silvercounter);
        gold = findViewById(R.id.goldcounter);
        penalty = findViewById(R.id.pencounter);
        claiming = findViewById(R.id.claiming);
        landing = findViewById(R.id.landing);
        parking = findViewById(R.id.parking);
        latch = findViewById(R.id.latch);
        craterstatus = findViewById(R.id.craterstatus);
        samplestatus = findViewById(R.id.samplestatus);
        nosample = findViewById(R.id.nosample);
        notincrater = findViewById(R.id.notincrater);
    }


    public void resetImpl() {
        silver.setText("0");
        gold.setText("0");
        penalty.setText("0");
        claiming.setChecked(false);
        landing.setChecked(false);
        parking.setChecked(false);
        latch.setChecked(false);
        craterstatus.check(notincrater.getId());
        samplestatus.check(nosample.getId());
    }

    public void silverUp(View view) {
        updateItem(silver, +1, ZERO_TO_POS_INF);
    }

    public void silverDown(View view) {
        updateItem(silver, -1, ZERO_TO_POS_INF);
    }

    public void goldUp(View view) {
        updateItem(gold, +1, ZERO_TO_POS_INF);
    }

    public void goldDown(View view) {
        updateItem(gold, -1, ZERO_TO_POS_INF);
    }

    public void penUp(View view) {
        updateItem(penalty, +5, NEG_INF_TO_ZERO);
    }

    public void penDown(View view) { updateItem(penalty, -5, NEG_INF_TO_ZERO); }

    public void disableLatch (View v) {
        Switch sw = findViewById(R.id.landing);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    findViewById(R.id.latch).setEnabled(false);
                    //findViewById(R.id.switch2).set
                } else {
                    findViewById(R.id.latch).setEnabled(true);
                }
            }
        });
    }

    public void onSubmit(MatchSubmission m) {
        m.put("Silver Balls", getIntOrZero(silver.getText()));
        m.put("Gold Cubes", getIntOrZero(gold.getText()));
        m.put("Penalty Points", getIntOrZero(penalty.getText()));
        m.put("Claming", claiming.isChecked());
        m.put("Landing", landing.isChecked());
        m.put("Parking", parking.isChecked());
        m.put("Latch", latch.isChecked());
        m.put("Crater Parking", findViewById(craterstatus.getCheckedRadioButtonId()).getTag().toString());
        m.put("Sample (Auto)", findViewById(samplestatus.getCheckedRadioButtonId()).getTag().toString());
    }

}
