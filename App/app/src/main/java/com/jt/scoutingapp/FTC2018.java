package com.jt.scoutingapp;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jt.scoutcore.MatchSubmission;


public class FTC2018 extends ScoutingActivity {

    TextView silver, gold, penalty;
    Switch claiming, landing, parking, latch;
    RadioGroup craterstatus, samplestatus;
    RadioButton nosample, notincrater;


    public void create() {
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


    public void reset() {
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

    public static int getIntOrZero(CharSequence s) {
        try {
            return Integer.parseInt(s.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static class Range {
        int min, max;
        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int clamp(int input) {
            input = Math.max(input, min);//Make > min
            input = Math.min(input, max);//Make < max
            return input;
        }
    }

    //min and max are inclusive
    public void updateItem(TextView counter, int increment, Range range) {
        int value = getIntOrZero(counter.getText().toString());
        value += increment;
        value = range.clamp(value);
        counter.setText(Integer.toString(value));
    }

    private static final Range ZERO_TO_POS_INF = new Range(0, Integer.MAX_VALUE), NEG_INF_TO_ZERO = new Range(Integer.MIN_VALUE, 0);

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

    public void penDown(View view) {
        updateItem(penalty, -5, NEG_INF_TO_ZERO);
    }

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
