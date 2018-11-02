package com.jt.scoutingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
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

import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.AssignerList;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

enum switchAuto {
    WRONG_SIDE, RIGHT_SIDE, NO_ATTEMPT
}

enum scaleAuto {
    WRONG_SIDE, RIGHT_SIDE, NO_ATTEMPT
}

enum finalPos {
    PLATFORM, RAMP, CLIMBED, NO_ATTEMPT
}

public class MainActivity extends AppCompatActivity {

    TextView switchTele;
    TextView silverValue;
    TextView penValue;
    TextView vault;
    TextView switchValue;
    TextView scaleValue;
    TextView teamNumber;
    TextView matchAndColor;

    AssignerList list;

    int index = 0;

    Button submitData;

    boolean baseline = false;

    int goldcounter = 0;
    int silvercounter = 0;
    int pencounter = 0;
    int vaultcounter = 0;
    int switchcounter = 0;
    int scalecounter = 0;

    switchAuto switcha = switchAuto.NO_ATTEMPT;
    scaleAuto scalea = scaleAuto.NO_ATTEMPT;
    finalPos posa = finalPos.NO_ATTEMPT;

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
            } else {
                Log.wtf("Test", "No permission needed!");
            }
        }

        MatchSubmission sub = new MatchSubmission(1, 1, TeamColor.BLUE);
        File f = new File(ClientUtils.ANDROID_MATCHES_DIR, "testNEW.jtm");
        System.err.println("*****About to create " + f.getParentFile().getAbsolutePath());
        System.err.println("*****Created" + f.getParentFile().mkdir());

        System.err.println("*****file: " + f.getAbsolutePath());
        ScoutingUtils.write(sub, f);

        finish();


        setContentView(R.layout.frc2018);

        matchlist = new ArrayList<AssignerEntry>();
        matchlist.add(new AssignerEntry(10, 2467, true));
        matchlist.add(new AssignerEntry(11, 3410, false));
        matchlist.add(new AssignerEntry(12, 102, false));
        matchlist.add(new AssignerEntry(13, 3, true));
        matchlist.add(new AssignerEntry(14, 981, true));
        matchlist.add(new AssignerEntry(15, 3012, false));
        matchlist.add(new AssignerEntry(16, 6969, true));
        matchlist.add(new AssignerEntry(17, 2346, false));
        matchlist.add(new AssignerEntry(18, 16, true));
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

        teamNumber.setText("Team " + matchlist.get(index).team);
        if(matchlist.get(index).red) {
            matchAndColor.setText("Match " + matchlist.get(index).match + " | Red");
        } else {
            matchAndColor.setText("Match " + matchlist.get(index).match + " | Blue");
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
        switcha = switchAuto.WRONG_SIDE;
    }

    public void autoSwitchRight (View view) {
        switcha = switchAuto.RIGHT_SIDE;
    }

    public void autoSwitchNo (View view) {
        switcha = switchAuto.WRONG_SIDE;
    }

    public void autoScaleWrong (View view) {
        scalea = scaleAuto.WRONG_SIDE;
    }

    public void autoScaleRight (View view) {
        scalea = scaleAuto.RIGHT_SIDE;
    }

    public void autoScaleNo (View view) {
        scalea = scaleAuto.NO_ATTEMPT;
    }

    public void autoPosClimbed (View view) {
        posa = finalPos.CLIMBED;
    }

    public void autoPosPlat (View view) {
        posa = finalPos.PLATFORM;
    }

    public void autoPosRamp (View view) {
        posa = finalPos.RAMP;
    }

    public void autoPosNo (View view) {
        posa = finalPos.NO_ATTEMPT;
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
                /*
                MatchSubmission m = new MatchSubmission(1, 1, TeamColor.BLUE);
                File file = new File(ClientUtils.ANDROID_SAVE_DIR, "test." + ScoutingConstants.EXTENSION);

                m.put("Switch Cubes (Tele)", goldcounter);
                m.put("Scale Cubes (Tele)", silvercounter);
                m.put("Vault Cubes (Tele)", vaultcounter);
                m.put("Switch Cubes (Auto)", switchcounter);
                m.put("Scale Cubes (Auto)", scalecounter);
                m.put("Switch Attempt (Auto)", switcha);
                m.put("Scale Attempt (Auto)", scalea);
                m.put("Final Position (End)", posa);
                m.put("Crossed Baseline (Tele)", baseline);

                boolean made = file.getParentFile().mkdirs();
                System.err.println("\r\n\n\n\n\n\n\n\n***************************************************************************************Created well " + made +" Exists " + file.getParentFile().exists() + " dir " + file.getParentFile().isDirectory() + "\n\n\n\n******************************************************************\n\n\n\n");

                ScoutingUtils.write(m, file);
                */
                index++;
                teamNumber.setText("Team " + matchlist.get(index).team);
                if(matchlist.get(index).red) {
                    matchAndColor.setText("Match " + matchlist.get(index).match + " | Red");
                } else {
                    matchAndColor.setText("Match " + matchlist.get(index).match + " | Blue");
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
