package com.jt.scoutingapp;

import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;

import com.jt.scoutcore.MatchSubmission;

public class FTC2019 extends MultiPageScoutingActivity {

    public void finalizeSubmission(MatchSubmission m) {

    }

    public static class FirstPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.ftc2019auto);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            // m.put("Foundation Moved (auto)", ((Switch) parent.findViewByTag(R.id.foundation_moved)).isChecked());
            m.put("Stones Delivered (auto)", parent.sumCheckBoxes(R.id.stones_delivered1, R.id.stones_delivered2, R.id.stones_delivered3, R.id.stones_delivered4, R.id.stones_delivered5));
            m.put("Stones Placed (auto)", parent.sumCheckBoxes(R.id.stones_placed1_auto, R.id.stones_placed2_auto, R.id.stones_placed3_auto, R.id.stones_placed4_auto, R.id.stones_placed5_auto));
            m.put("Sky Stones (auto)", parent.sumCheckBoxes(R.id.sky_stones1, R.id.sky_stones2, R.id.sky_stones3, R.id.sky_stones4, R.id.sky_stones5));
            //m.put("Parked (auto)", ((Switch) parent.findViewByTag(R.id.parked)).isChecked());
        }
    }

    public static class SecondPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.ftc2019tele);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put("Stones across correct color bridge", parent.sumCheckBoxes(R.id.stones_correct1, R.id.stones_correct2, R.id.stones_correct3, R.id.stones_correct4, R.id.stones_correct5,
                    R.id.stones_correct6, R.id.stones_correct7, R.id.stones_correct8, R.id.stones_correct9, R.id.stones_correct10, R.id.stones_correct11));
            m.put("Stones Placed", parent.sumCheckBoxes(R.id.stones_placed1, R.id.stones_placed2, R.id.stones_placed3, R.id.stones_placed4, R.id.stones_placed5,
                    R.id.stones_placed6, R.id.stones_placed7, R.id.stones_placed8, R.id.stones_placed9, R.id.stones_placed10,
                    R.id.stones_placed11, R.id.stones_placed12, R.id.stones_placed13, R.id.stones_placed14, R.id.stones_placed15,
                    R.id.stones_placed16, R.id.stones_placed17, R.id.stones_placed18, R.id.stones_placed19, R.id.stones_placed20));
        }
    }

    public static class ThirdPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.ftc2019endgame);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put("Capstone", parent.getRadioGroupStringSelection(R.id.capstone));
            m.put("Move Foundation", parent.getRadioGroupStringSelection(R.id.move_foundation));
            m.put("Parked", parent.getRadioGroupStringSelection(R.id.parked));

            m.put("Comments", ((EditText) parent.findViewById(R.id.comments)).getText().toString());
            m.put("Rating", (int) ((RatingBar) parent.findViewById(R.id.rating)).getRating());//The rating is only ever going to be an int anyways...
        }
    }


    public FTC2019() {
        super(new FTC2019.FirstPage(), new FTC2019.SecondPage(), new FTC2019.ThirdPage());
    }

}

