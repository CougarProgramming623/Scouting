package com.jt.scoutingapp;

import android.util.Log;

import com.jt.scoutcore.MatchSubmission;

public class FRC2019 extends MultiPageScoutingActivity {

    public static class FirstPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019pg1);
            Log.e("UI", "Set layout!");
        }

        @Override
        public void onSubmit(MatchSubmission m) {

        }
    }
    public static class SecondPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019pg2);
        }

        @Override
        public void onSubmit(MatchSubmission m) {

        }
    }
    public static class ThirdPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019pg3);
        }

        @Override
        public void onSubmit(MatchSubmission m) {

        }
    }

    public FRC2019() {
        super(new FirstPage(), new SecondPage(), new ThirdPage());
    }

}
