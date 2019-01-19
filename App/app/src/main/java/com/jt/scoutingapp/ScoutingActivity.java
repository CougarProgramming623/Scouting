package com.jt.scoutingapp;

import android.Manifest;
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
import android.widget.TextView;

import com.jt.scoutcore.AssignerList;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScoutingActivity extends AppCompatActivity {

    protected AtomicBoolean hasPermission = new AtomicBoolean(false);
    protected AssignerList list;

    private Button submitButton;
    private TextView teamNumber, matchNumber;


    //Called once on app startup to grab objects from the xml
    public abstract void create();

    //Called each time when a new match needs to be displayed
    public abstract void reset();

    //Called each time the user presses submit
    public abstract void onSubmit(MatchSubmission m);

    private void resetSuperclass() {
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

        teamNumber = findViewById(R.id.team);
        matchNumber = findViewById(R.id.match);
        submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoutingActivity.this, Popup.class);
                startActivityForResult(intent, 42);
            }
        });
        create();
        resetSuperclass();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            MatchSubmission m = new MatchSubmission(list.getCurrent().team, list.getCurrent().match, list.getCurrent().red ? TeamColor.RED : TeamColor.BLUE);
            onSubmit(m);

            File file = new File(ClientUtils.ANDROID_MATCHES_DIR, "Match_" + list.getCurrent().match + "_Team_" + list.getCurrent().team + "." + ScoutingConstants.EXTENSION);
            ClientUtils.ANDROID_MATCHES_DIR.mkdirs();
            ScoutingUtils.write(m, file);

            list.matchStart++;
            if(list.matchStart == list.entries.size()) {
                setContentView(R.layout.end_of_assignments);
            } else {
                resetSuperclass();
            }
        }
    }

}
