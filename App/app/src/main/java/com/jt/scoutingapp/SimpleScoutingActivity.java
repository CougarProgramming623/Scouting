package com.jt.scoutingapp;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;

/**
 * Represents a "simple" scouting activity. A simple scouting app is one that has only one layout file that contains a submit button, and a text view for the current team and match
 */
public abstract class SimpleScoutingActivity extends AbstractScoutingActivity {
    private Button submitButton;

    //Called each time the user presses submit
    public abstract void onSubmit(MatchSubmission m);

    public abstract void createImpl();

    public void init() {
        //Call create first so that the user can set their layout
        createImpl();
        submitButton = findViewByTag(R.string.submit_tag);
        if(submitButton == null) {
            Log.w("UI", "Unable to find submit button for class: " + this.getClass());
        } else {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SimpleScoutingActivity.this, Popup.class);
                    startActivityForResult(intent, 42);
                }
            });
        }
    }

    protected void handleSubmit() {
        MatchSubmission m = new MatchSubmission(list.getCurrent().team, list.getCurrent().match, list.getCurrent().red ? TeamColor.RED : TeamColor.BLUE);
        onSubmit(m);//Fill m with the data

        File file = ClientUtils.getMatchFile(list.getCurrent().match, list.getCurrent().team);//Save the file
        ClientUtils.ANDROID_MATCHES_DIR.mkdirs();
        ScoutingUtils.write(m, file);
    }

    @Override
    public void resetImpl() {
        setTeamAndMatch();
    }
}
