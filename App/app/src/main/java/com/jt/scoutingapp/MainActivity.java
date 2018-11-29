package com.jt.scoutingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Button;

import com.jt.scoutcore.AssignerList;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;




public class MainActivity extends AppCompatActivity {

    AtomicBoolean hasPermission = new AtomicBoolean(false);
    AssignerList list;

    Button submitData;
    TextView teamNumber, matchNumber;

    TextView silver, gold, penalty;
    Switch claiming, landing, parking, latch;
    RadioGroup craterstatus, samplestatus;
    RadioButton nosample, notincrater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // If do not grant write external storage permission.
            Log.wtf("Test", "About to request permission!");
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Log.wtf("Test", "Requesting permission!!");
                hasPermission.set(false);
            } else {
                hasPermission.set(true);
                Log.wtf("Test", "No permission needed!");
            }
        } else {
            hasPermission.set(true);
        }
        while(!hasPermission.get()) {
            System.err.println("Waiting to get permissions...");
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        try {
            list = ClientUtils.readAssignments();
        } catch(Exception e) {
            setContentView(R.layout.no_assignments);
            e.printStackTrace();
            return;
        }
        if (list.matchStart == 0) {
            list.matchStart = 0;
        } else {
            for (int i = 0; i < list.entries.size(); i++) {
                if (list.entries.get(i).match == list.matchStart) {
                    list.matchStart = i;
                    System.err.println("Starting with index " + i);
                    break;
                }
            }
        }

        setContentView(R.layout.activity_main);

        teamNumber = findViewById(R.id.team);
        matchNumber = findViewById(R.id.match);

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

        submitData = findViewById(R.id.submit);
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Popup.class);
                startActivityForResult(intent, 42);
            }
        });
        init();
    }

    public void init() {
        silver.setText("0");
        gold.setText("0");
        penalty.setText("0");
        claiming.setChecked(false);
        landing.setChecked(false);
        parking.setChecked(false);
        latch.setChecked(false);
        craterstatus.check(notincrater.getId());
        samplestatus.check(nosample.getId());

        teamNumber.setText(Html.fromHtml("<font color=#f4ff30>Team " + list.getCurrent().team + "</font>"));
        if (list.getCurrent().red) {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#FF3030>Red</font>"));
        } else {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#0060ff>Blue</font>"));
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        silver = findViewById(R.id.silvercounter);
        gold = findViewById(R.id.goldcounter);
        penalty = findViewById(R.id.pencounter);
        claiming = findViewById(R.id.claiming);
        landing = findViewById(R.id.landing);
        parking = findViewById(R.id.parking);
        latch = findViewById(R.id.latch);
        craterstatus = findViewById(R.id.craterstatus);
        samplestatus = findViewById(R.id.samplestatus);

        if(resultCode == RESULT_OK) {
            MatchSubmission m = new MatchSubmission(list.getCurrent().team, list.getCurrent().match, list.getCurrent().red ? TeamColor.RED : TeamColor.BLUE);
            File file = new File(ClientUtils.ANDROID_MATCHES_DIR, "Match_" + list.getCurrent().match + "_Team_" + list.getCurrent().team + "." + ScoutingConstants.EXTENSION);
            m.put("Silver Balls", getIntOrZero(silver.getText()));
            m.put("Gold Cubes", getIntOrZero(gold.getText()));
            m.put("Penalty Points", getIntOrZero(penalty.getText()));
            m.put("Claming", claiming.isChecked());
            m.put("Landing", landing.isChecked());
            m.put("Parking", parking.isChecked());
            m.put("Latch", latch.isChecked());
            m.put("Crater Parking", findViewById(craterstatus.getCheckedRadioButtonId()).getTag().toString());
            m.put("Sample (Auto)", findViewById(samplestatus.getCheckedRadioButtonId()).getTag().toString());

            file.getParentFile().mkdirs();
            ScoutingUtils.write(m, file);

            list.matchStart++;
            if(list.matchStart == list.entries.size()) {
                setContentView(R.layout.out_of_bounds);
            } else {
                init();
            }
        }
    }
}
