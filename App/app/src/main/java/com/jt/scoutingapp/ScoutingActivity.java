package com.jt.scoutingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.AssignmentsBase;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScoutingActivity extends AppCompatActivity {

    protected AtomicBoolean hasPermission = new AtomicBoolean(false);
    protected AssignmentsBase list;

    private Button submitButton;
    private TextView teamNumber, matchNumber;


    //Called once on app startup to grab objects from the xml
    public abstract void create();

    //Called each time when a new match needs to be displayed
    public abstract void reset();

    //Called each time the user presses submit
    public abstract void onSubmit(MatchSubmission m);


    //Used in this method
    private AssignerEntry nextUnscouted = null;
    private void resetSuperclass() {
        AssignerEntry current = list.getCurrent();
        if(current == null) {
            throw new Error("Current shouldnt be null!");
        }
        File currentFile = ClientUtils.getMatchFile(current.match, current.team);
        for(File file : ClientUtils.ANDROID_MATCHES_DIR.listFiles()) {
            if(currentFile.equals(file)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Match Already Scouted!");
                alertDialog.setMessage("You already scouted match #" + current.match + " for team " + current.team + "\nWhat would you like to do?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Re scout this match (will override old data)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;//Nothing we are already on the match
                    }
                });
                if(list.getRemainingAssignments().size() > 0) {
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Skip to the next assignment", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            list.next();
                            resetSuperclass();
                        }
                    });
                }
                for(AssignerEntry entry : list.getAllAssignments()) {
                    File entryFile = ClientUtils.getMatchFile(entry.match, entry.team);
                    boolean notScouted = true;
                    for(File file1 : ClientUtils.ANDROID_MATCHES_DIR.listFiles()) {
                        if(entryFile.equals(file1)) {
                            notScouted = false;//We found the file. Its already scouted
                        }
                    }
                    if(notScouted) {
                        nextUnscouted = entry;
                        break;
                    }
                }
                if(nextUnscouted != null) {
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Skip to the next unscouted match (match #" + nextUnscouted.match + " team " + nextUnscouted.team, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            list.goToMatch(nextUnscouted.match, nextUnscouted.team);
                            resetSuperclass();
                        }
                    });
                }

                alertDialog.show();
            }
        }

        teamNumber.setText(Html.fromHtml("<font color=#f4ff30>Team " + list.getCurrent().team + "</font>"));
        if (list.getCurrent().red) {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#FF3030>Red</font>"));
        } else {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#0060ff>Blue</font>"));
        }
        reset();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // If do not grant write external storage permission.
            Log.i("Permission", "About to request permission!");
            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Log.i("Permission", "Requesting permission!!");
                hasPermission.set(false);
            } else {
                hasPermission.set(true);
                Log.i("Permission", "No permission needed!");
            }
        } else {
            hasPermission.set(true);
        }
        while(!hasPermission.get()) {
            Log.d("Permission", "Waiting to get permissions...");
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
        //Call create first so that the user can set their layout
        create();
        teamNumber = findViewById(R.id.team);
        matchNumber = findViewById(R.id.match);
        submitButton = findViewById(R.id.submit);
        if(submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ScoutingActivity.this, Popup.class);
                    startActivityForResult(intent, 42);
                }
            });
        } else {
            Log.e("UI", "Unable to find submit button!");
        }
        resetSuperclass();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            MatchSubmission m = new MatchSubmission(list.getCurrent().team, list.getCurrent().match, list.getCurrent().red ? TeamColor.RED : TeamColor.BLUE);
            onSubmit(m);

            File file = ClientUtils.getMatchFile(list.getCurrent().match, list.getCurrent().team);
            ClientUtils.ANDROID_MATCHES_DIR.mkdirs();
            ScoutingUtils.write(m, file);

            if(list.next() == null) {
                setContentView(R.layout.end_of_assignments);
            } else {
                resetSuperclass();
            }
        }
    }

    /**
     * Returns the tag of the selected radio button in the radio group or null if no radio button is currently selected
     * @return The tag of the selected radio button, or null
     */
    public String getRadioGroupStringSelection(RadioGroup group) {
        View view = findViewById(group.getCheckedRadioButtonId());
        if(view == null)
            return null;
        return (view.getTag() == null) ? null : view.getTag().toString();
    }

    public static int getIntOrZero(TextView view) {
        return getIntOrZero(view.getText());
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

    protected static final Range ZERO_TO_POS_INF = new Range(0, Integer.MAX_VALUE), NEG_INF_TO_ZERO = new Range(Integer.MIN_VALUE, 0);


}
