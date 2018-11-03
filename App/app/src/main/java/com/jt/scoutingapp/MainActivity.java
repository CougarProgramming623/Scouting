package com.jt.scoutingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import static com.jt.scoutcore.FRCEnums.*;



public class MainActivity extends AppCompatActivity {

    TextView switchTele;
    TextView silverValue;
    TextView penValue;
    TextView vault;
    TextView switchValue;
    TextView scaleValue;
    TextView teamNumber;
    TextView matchAndColor;
    AtomicBoolean hasPermission = new AtomicBoolean(false);

    AssignerList list;

    Button submitData;

    boolean baseline = false;

    int goldcounter = 0;
    int silvercounter = 0;
    int pencounter = 0;
    int vaultcounter = 0;
    int switchcounter = 0;
    int scalecounter = 0;

    SwitchAuto switcha = SwitchAuto.NO_ATTEMPT;
    ScaleAuto scalea = ScaleAuto.NO_ATTEMPT;
    FinalPos posa = FinalPos.NO_ATTEMPT;

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

        list = ClientUtils.readAssignments();
        if(list.matchStart == 0) {
            list.matchStart = 0;
        } else {
            for(int i = 0; i < list.entries.size(); i++) {
                if(list.entries.get(i).match == list.matchStart) {
                    list.matchStart = i;
                    System.err.println("Starting with index " + i);
                    break;
                }
            }
        }

        setContentView(R.layout.frc2018);

        //matchlist = ScoutingUtils.readAssignments();

        switchTele = findViewById(R.id.switchtcounter);
        silverValue = findViewById(R.id.scaletcounter);
        penValue = findViewById(R.id.pencounter);
        vault = findViewById(R.id.vaultcounter);
        switchValue = findViewById(R.id.goldcounter2);
        scaleValue = findViewById(R.id.goldcounter3);
        teamNumber = findViewById(R.id.textView2);
        matchAndColor = findViewById(R.id.textView);
        //intent.putExtra("goldVal", goldcounter);
        /*
        intent.putExtra("silverVal", silverCounter);
        intent.putExtra("penaltyVal", penCounter);
        intent.putExtra("Claiming", claiming);
        intent.putExtra("Landing", landing);
        intent.putExtra("Parking", parking);
        intent.putExtra("Sampling", sample);
        intent.putExtra("Latch", latch);
        intent.putExtra("Position", pos);
        */

        teamNumber.setText("Team " + list.getCurrent().team);
        if(list.getCurrent().red) {
            matchAndColor.setText("Match " + list.getCurrent().match + " | Red");
        } else {
            matchAndColor.setText("Match " + list.getCurrent().match + " | Blue");
        }


        submitData = findViewById(R.id.submit);
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Popup.class);
                startActivityForResult(intent, 42);
            }
        });
    }

    public void goldUp (View view) {
        goldcounter++;
        switchTele.setText(Integer.toString(goldcounter));
    }

    public void goldDown (View view) {
        if(goldcounter > 0) {
            goldcounter--;
            switchTele.setText(Integer.toString(goldcounter));
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
        switchcounter += 1;
        switchValue.setText(Integer.toString(switchcounter));
    }

    public void switchDown (View view) {
        if(switchcounter >= 1) {
            switchcounter -= 1;
            switchValue.setText(Integer.toString(switchcounter));
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

    public void autoSwitchWrong (View view) {
        switcha = SwitchAuto.WRONG_SIDE;
    }

    public void autoSwitchRight (View view) {
        switcha = SwitchAuto.RIGHT_SIDE;
    }

    public void autoSwitchNo (View view) {
        switcha = SwitchAuto.WRONG_SIDE;
    }

    public void autoScaleWrong (View view) {
        scalea = ScaleAuto.WRONG_SIDE;
    }

    public void autoScaleRight (View view) {
        scalea = ScaleAuto.RIGHT_SIDE;
    }

    public void autoScaleNo (View view) {
        scalea = ScaleAuto.NO_ATTEMPT;
    }

    public void autoPosClimbed (View view) {
        posa = FinalPos.CLIMBED;
    }

    public void autoPosPlat (View view) {
        posa = FinalPos.PLATFORM;
    }

    public void autoPosRamp (View view) {
        posa = FinalPos.RAMP;
    }

    public void autoPosNo (View view) {
        posa = FinalPos.NO_ATTEMPT;
    }

    public void baselineValue (View view) {
        baseline = ((Switch)findViewById(R.id.baseline)).isChecked();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

                MatchSubmission m = new MatchSubmission(list.getCurrent().team, list.getCurrent().match, list.getCurrent().red ? TeamColor.RED : TeamColor.BLUE);
                File file = new File(ClientUtils.ANDROID_MATCHES_DIR, "Match_" + list.getCurrent().match + "_Team_" + list.getCurrent().team + "." + ScoutingConstants.EXTENSION);

                m.put("Switch Cubes (Tele)", goldcounter);
                m.put("Scale Cubes (Tele)", silvercounter);
                m.put("Vault Cubes (Tele)", vaultcounter);
                m.put("Switch Cubes (Auto)", switchcounter);
                m.put("Scale Cubes (Auto)", scalecounter);
                m.put("Switch Attempt (Auto)", switcha);
                m.put("Scale Attempt (Auto)", scalea);
                m.put("Final Position (End)", posa);
                m.put("Crossed Baseline (Tele)", baseline);

                file.getParentFile().mkdirs();
                ScoutingUtils.write(m, file);

                list.matchStart++;
                teamNumber.setText("Team " + list.getCurrent().team);
                if(list.getCurrent().red) {
                    matchAndColor.setText("Match " + list.getCurrent().match + " | Red");
                } else {
                    matchAndColor.setText("Match " + list.getCurrent().match + " | Blue");
                }

            /*
            System.out.println("*************************************************************");
            System.out.println();
            System.out.println();
            System.out.println("WOWOWOOWOWOWOWOWOWOWOOWOOWO");
            System.out.println(data);
            System.out.println();
            System.out.println();
            System.out.println("*************************************************************");
            */
        }
    }
}
