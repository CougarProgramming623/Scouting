package com.jt.scoutingapp;

import android.widget.EditText;
import android.widget.RatingBar;

import com.jt.scoutcore.MatchSubmission;

public class FRC2019 extends MultiPageScoutingActivity {

    private static final String CARGO_SHIP_HATCH = "Cargo_Ship_Hatch", ROCKET_LOW_HATCH = "Rocket_Low_Hatch", ROCKET_MEDIUM_HATCH = "Rocket_Medium_Hatch", ROCKET_HIGH_HATCH = "Rocket_High_Hatch",
            CARGO_SHIP_CARGO = "Cargo_Ship_Cargo", ROCKET_LOW_CARGO = "Rocket_Low_Cargo", ROCKET_MEDIUM_CARGO = "Rocket_Medium_Cargo", ROCKET_HIGH_CARGO = "Rocket_High_Cargo";

    public void finalizeSubmission(MatchSubmission m) {
        int lowCargo = (Integer) m.remove(ROCKET_LOW_CARGO) + (Integer) m.remove(CARGO_SHIP_CARGO);
        int lowHatch = (Integer) m.remove(ROCKET_LOW_HATCH) + (Integer) m.remove(CARGO_SHIP_HATCH);
        int mediumCargo = (Integer) m.remove(ROCKET_MEDIUM_CARGO);
        int mediumHatch = (Integer) m.remove(ROCKET_MEDIUM_HATCH);
        int highCargo = (Integer) m.remove(ROCKET_HIGH_CARGO);
        int highHatch = (Integer) m.remove(ROCKET_HIGH_HATCH);
        m.put("Low Cargo", lowCargo);
        m.put("Low Hatches", lowHatch);
        m.put("Medium Cargo", mediumCargo);
        m.put("Medium Hatches", mediumHatch);
        m.put("High Cargo", highCargo);
        m.put("High Hatches", highHatch);
    }

    public static class FirstPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019sandstorm);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put("Starting Height", parent.getRadioGroupStringSelection(R.id.starting_height));
            m.put("Starting Position", parent.getRadioGroupStringSelection(R.id.starting_position));
            m.put("Driver Station", parent.getRadioGroupStringSelection(R.id.driver_station));
            m.put("Auto Cargo", parent.sumCheckBoxes(R.id.cargo_auto1, R.id.cargo_auto2, R.id.cargo_auto3));
            m.put("Auto Hatches", parent.sumCheckBoxes(R.id.hatch_auto1, R.id.hatch_auto2, R.id.hatch_auto3));
        }
    }

    public static class SecondPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019hatches);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put(ROCKET_LOW_HATCH, parent.sumCheckBoxes(R.id.hatch_rocket1_low_l, R.id.hatch_rocket1_low_r, R.id.hatch_rocket2_low_l, R.id.hatch_rocket2_low_r));
            m.put(ROCKET_MEDIUM_HATCH, parent.sumCheckBoxes(R.id.hatch_rocket1_medium_l, R.id.hatch_rocket1_medium_r, R.id.hatch_rocket2_medium_l, R.id.hatch_rocket2_medium_r));
            m.put(ROCKET_HIGH_HATCH, parent.sumCheckBoxes(R.id.hatch_rocket1_high_l, R.id.hatch_rocket1_high_r, R.id.hatch_rocket2_high_l, R.id.hatch_rocket2_high_r));
            m.put(CARGO_SHIP_HATCH, parent.sumCheckBoxes(R.id.cargo_ship1_hatch, R.id.cargo_ship2_hatch, R.id.cargo_ship3_hatch, R.id.cargo_ship4_hatch, R.id.cargo_ship5_hatch, R.id.cargo_ship6_hatch, R.id.cargo_ship7_hatch, R.id.cargo_ship8_hatch));
        }
    }

    public static class ThirdPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019cargo);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put(ROCKET_LOW_CARGO, parent.sumCheckBoxes(R.id.cargo_rocket1_low_l, R.id.cargo_rocket1_low_r, R.id.cargo_rocket2_low_l, R.id.cargo_rocket2_low_r));
            m.put(ROCKET_MEDIUM_CARGO, parent.sumCheckBoxes(R.id.cargo_rocket1_medium_l, R.id.cargo_rocket1_medium_r, R.id.cargo_rocket2_medium_l, R.id.cargo_rocket2_medium_r));
            m.put(ROCKET_HIGH_CARGO, parent.sumCheckBoxes(R.id.cargo_rocket2_high_l, R.id.cargo_rocket1_high_r, R.id.cargo_rocket2_high_l, R.id.cargo_rocket2_high_r));
            m.put(CARGO_SHIP_CARGO, parent.sumCheckBoxes(R.id.cargo_ship1, R.id.cargo_ship2, R.id.cargo_ship3, R.id.cargo_ship4, R.id.cargo_ship5, R.id.cargo_ship6, R.id.cargo_ship7, R.id.cargo_ship8));

        }
    }

    public static class FourthPage extends ScoutingPage {

        @Override
        public void showImpl(MultiPageScoutingActivity parent) {
            parent.setContentView(R.layout.frc2019endgame);
        }

        @Override
        public void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m) {
            m.put("Ending Position", parent.getRadioGroupStringSelection(R.id.ending_position));
            m.put("Comments", ((EditText) parent.findViewById(R.id.comments)).getText().toString());
            m.put("Ground Pickup", parent.getRadioGroupStringSelection(R.id.ground_pickup));
            m.put("Direct Climb", parent.getRadioGroupStringSelection(R.id.direct_climb));
            m.put("Rating", (int) ((RatingBar) parent.findViewById(R.id.rating)).getRating());//The rating is only ever going to be an int anyways...
        }
    }

    public FRC2019() {
        super(new FirstPage(), new SecondPage(), new ThirdPage(), new FourthPage());
    }

}
