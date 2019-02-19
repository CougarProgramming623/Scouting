package com.jt.scoutingapp;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;

public abstract class MultiPageScoutingActivity extends AbstractScoutingActivity {

    private MatchSubmission currentSubmission;
    private ScoutingPage[] pages;
    private int index = 0;

    public MultiPageScoutingActivity(ScoutingPage... pages) {
        this.pages = pages;
    }

    public static abstract class ScoutingPage {
        //Called once each time the layout switches - for each page, for each match
        public abstract void showImpl(MultiPageScoutingActivity parent);

        private void show(MultiPageScoutingActivity parent) {
            showImpl(parent);//Let the user set their layout so we can pull the requisite buttons from it
            View submitButton = setListener(parent, R.string.submit_tag, (v) -> parent.nextPage());
            View nextButton = setListener(parent, R.string.next_tag, (v) -> parent.nextPage());
            View previousButton = setListener(parent, R.string.previous_tag, (v) -> parent.previousPage());

            TextView teamNumber = parent.findViewByTag(R.string.team_tag);
            TextView matchNumber = parent.findViewByTag(R.string.match_tag);
            if(teamNumber == null) throw new RuntimeException("Unable to find a textview with the team tag");
            if(matchNumber == null) throw new RuntimeException("Unable to find a textview with the match tag");

            teamNumber.setText(Html.fromHtml("<font color=#f4ff30>Team " + parent.list.getCurrent().team + "</font>"));
            if (parent.list.getCurrent().red) {
                matchNumber.setText(Html.fromHtml("Match " + parent.list.getCurrent().match + " | <font color=#FF3030>Red</font>"));
            } else {
                matchNumber.setText(Html.fromHtml("Match " + parent.list.getCurrent().match + " | <font color=#0060ff>Blue</font>"));
            }

            if(submitButton == null && nextButton == null && previousButton == null) {
                throw new RuntimeException("Scouting Activity must have at least one navigation element, either submit, next or previous!");
            }
        }

        private View setListener(MultiPageScoutingActivity parent, int tag, View.OnClickListener listener) {
            View view = parent.findViewByTag(tag);
            if(view != null)
                view.setOnClickListener(listener);
            return view;
        }

        //Called when the user goes onto the next page
        public abstract void onSubmit(MatchSubmission m);
    }

    public void nextPage() {
        if(index < pages.length) {
            pages[index].onSubmit(currentSubmission);//Save the info from the current page
            index++;//Go onto the next page
        }
        resetPage();//Display the new page
    }

    public void previousPage() {
        if(index < pages.length) {
            pages[index].onSubmit(currentSubmission);//Save the info from the current page
        }
        if(index >= 1) {
            index--;
            resetPage();
        }
    }

    //Called once on app startup to grab objects from the xml
    public void init() {
        reset();
    }

    private void resetPage() {
        if(index >= pages.length) {
            //Go to the next match
            Intent intent = new Intent(MultiPageScoutingActivity.this, Popup.class);
            startActivityForResult(intent, 42);//Request a submit
        } else {
            pages[index].show(this);
        }
    }

    //Called each time when a new match needs to be displayed
    public void resetImpl() {
        AssignerEntry current = list.getCurrent();
        if(current == null) {
            setContentView(R.layout.end_of_assignments);
            return;
        }
        index = 0;
        currentSubmission = new MatchSubmission(current.team, current.match, current.red ? TeamColor.RED : TeamColor.BLUE);
        resetPage();//Show the first page
    }

    @Override
    protected void handleSubmit() {
        File file = ClientUtils.getMatchFile(list.getCurrent().match, list.getCurrent().team);
        ClientUtils.ANDROID_MATCHES_DIR.mkdirs();
        ScoutingUtils.write(currentSubmission, file);

        if(list.next() == null) {//Handle the next match
            setContentView(R.layout.end_of_assignments);
        } else {
            reset();
        }
    }
}
