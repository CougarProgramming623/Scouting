package com.jt.scoutingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jt.scoutcore.AssignedTeams;
import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.AssignmentsBase;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractScoutingActivity extends AppCompatActivity {

    protected AtomicBoolean hasPermission = new AtomicBoolean(false);
    protected AssignmentsBase list;

    //Called once on app startup to grab objects from the xml
    public abstract void init();

    //Called each time a new match needs to be displayed
    public abstract void resetImpl();

    //Called when the user presses and confirms submit
    protected abstract void handleSubmit();

    public final <T extends View> T findViewByTag(int tag) {
        T screen = findViewById(android.R.id.content);
        return screen.findViewWithTag(getString(tag));
    }


    //Re creates the UI for displaying the next match
    public void reset() {
        AssignerEntry current = list.getCurrent();
        if (current == null) {
            Log.e("Scout", "Current Entry Is Null: ");
            if (list.hasDynamicAssignments()) {
                list.next((entry) -> {
                    if (entry != null) {
                        checkOverride(entry);
                        resetImpl();
                    } else {
                        Log.w("Scout", "Failed to find next dynamic match");
                    }
                });
            } else {
                setContentView(R.layout.end_of_assignments);
                Log.w("Scout", "No new matches available");
            }
        } else {
            list.next((entry) -> {
                if (entry != null) {
                    checkOverride(entry);
                    resetImpl();
                } else {
                    Log.w("Scout", "Failed to find next dynamic match");
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions();
        getAssignments();
        init();
        reset();
    }

    //Called when the user clicks save or dismisses the submit popup
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            handleSubmit();
            reset();
        }
    }

    //Called to ensure we have the permissions to read/write to the SD card. This will prompt the user about accessing files on newer devices
    protected void getPermissions() {
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
    }

    protected void showMatchAlert(final int team, int[] teamsList, final AssignedTeams.OnMatchDetermined listener) {

        AlertDialog.Builder matchAlert = new AlertDialog.Builder(AbstractScoutingActivity.this);
        matchAlert.setTitle("Enter Match Number");
        final EditText input = new EditText(AbstractScoutingActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        matchAlert.setView(input);
        matchAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final int match;
                try {
                    match = Integer.parseInt(input.getText().toString());
                } catch(Exception e) {
                    Toast.makeText(AbstractScoutingActivity.this, "Invalid match number \"" + input.getText().toString() + "\"", Toast.LENGTH_LONG).show();
                    showMatchAlert(team, teamsList, listener);
                    return;
                }

                AlertDialog.Builder colorAlert = new AlertDialog.Builder(AbstractScoutingActivity.this);
                colorAlert.setTitle("Select Color");
                colorAlert.setPositiveButton("Red", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.submit(new AssignerEntry(match, team, true));
                    }
                });
                colorAlert.setNegativeButton("Blue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.submit(new AssignerEntry(match, team, false));
                    }
                });
                colorAlert.show();
            }
        });
        matchAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        matchAlert.show();
    }

    protected void showTeamAlert(int[] teamsList, final AssignedTeams.OnMatchDetermined listener) {
        AlertDialog.Builder teamAlert = new AlertDialog.Builder(this);
        teamAlert.setTitle("Enter Team Number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        teamAlert.setView(input);
        teamAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final int team;
                try {
                    team = Integer.parseInt(input.getText().toString());
                } catch(Exception e) {
                    Toast.makeText(AbstractScoutingActivity.this, "Invalid team number \"" + input.getText().toString() + "\"", Toast.LENGTH_LONG).show();
                    showTeamAlert(teamsList, listener);
                    return;
                }
                boolean found = false;
                for(int teamNum : teamsList) {
                    if(teamNum == team) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(AbstractScoutingActivity.this, "Team \"" + team + "\" Not Found!", Toast.LENGTH_LONG).show();
                    showTeamAlert(teamsList, listener);
                    return;
                }
                showMatchAlert(team, teamsList, listener);
            }
        });
        teamAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        teamAlert.show();
    }

    protected void getAssignments() {
        try {
            list = ClientUtils.readAssignments();
            if(list == null) throw new NullPointerException();//To to catch
            if (list instanceof AssignedTeams) {
                AssignedTeams teams = (AssignedTeams) list;
                teams.setCreator((int[] teamsList, AssignedTeams.OnMatchDetermined listener) -> {
                    showTeamAlert(teamsList, listener);
                });

            }
        } catch(Exception e) {
            setContentView(R.layout.no_assignments);
            e.printStackTrace();
            return;
        }
    }

    //Used in this method by lambdas so it must be an instance variable
    private AssignerEntry nextUnscouted = null;

    //Checks if the the desired match has already been scouted and prompts the user about what to do if so
    private void checkOverride(AssignerEntry current) {
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
                            list.next((entry) -> {
                                reset();//Display the new match
                            });//Go to the next match
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
                if(nextUnscouted != null) {//This option will be hidden if nextUnscouted is null, indicating that there are no more unscouted matches
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Skip to the next unscouted match (match #" + nextUnscouted.match + " team " + nextUnscouted.team, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            list.goToMatch(nextUnscouted.match, nextUnscouted.team);
                            reset();//Display the new match
                        }
                    });
                }

                alertDialog.show();
            }
        }

    }

    public void setTeamAndMatch() {

        TextView teamNumber = findViewByTag(R.string.team_tag);
        TextView matchNumber = findViewByTag(R.string.match_tag);
        if(teamNumber == null) throw new RuntimeException("Unable to find a textview with the team tag");
        if(matchNumber == null) throw new RuntimeException("Unable to find a textview with the match tag");

        teamNumber.setText(Html.fromHtml("<font color=#f4ff30>Team " + list.getCurrent().team + "</font>"));
        if (list.getCurrent().red) {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#FF3030>Red</font>"));
        } else {
            matchNumber.setText(Html.fromHtml("Match " + list.getCurrent().match + " | <font color=#0060ff>Blue</font>"));
        }
    }

    public String getRadioGroupStringSelection(@IdRes int id) {
        View view = findViewById(id);
        if(!(view instanceof RadioGroup))
            return "NOT RADIO GROUP";
        return getRadioGroupStringSelection((RadioGroup) view);
    }

    /**
     * Returns the name-tag of the selected radio button in the radio group or null if no radio button is currently selected
     * @return The name-tag of the selected radio button, or null
     */
    public String getRadioGroupStringSelection(RadioGroup group) {
        View view = findViewById(group.getCheckedRadioButtonId());
        if(view == null)
            return "null";
        return (view.getTag(R.id.name) == null) ? null : view.getTag(R.id.name).toString();
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
