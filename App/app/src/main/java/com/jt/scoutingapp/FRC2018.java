package com.jt.scoutingapp;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.jt.scoutcore.MatchSubmission;

public class FRC2018 extends SimpleScoutingActivity {

    private Switch baseline;
    private EditText switchTele, switchAuto, scaleTele, scaleAuto, penaltyPoints, vault;
    private RadioGroup finalPosition;

    public void createImpl() {
        setContentView(R.layout.frc2018);

        switchTele = findViewById(R.id.switch_tele);
        switchAuto = findViewById(R.id.switch_auto);
        scaleTele = findViewById(R.id.scale_tele);
        scaleAuto = findViewById(R.id.scale_auto);
        penaltyPoints = findViewById(R.id.penalty_counter);
        vault = findViewById(R.id.vault);
        baseline = findViewById(R.id.baseline);
        finalPosition = findViewById(R.id.final_position);
    }

    public void resetImpl() {
        super.resetImpl();
        switchTele.setText("0");
        switchAuto.setText("0");
        scaleTele.setText("0");
        scaleAuto.setText("0");
        penaltyPoints.setText("0");
        vault.setText("0");
        baseline.setChecked(false);
        finalPosition.check(findViewById(R.id.no_attempt).getId());
    }

    public void onSubmit(MatchSubmission m) {
        m.put("Switch Cubes", getIntOrZero(switchTele));
        m.put("Scale Cubes", getIntOrZero(scaleTele));
        m.put("Auto Switch Cubes", getIntOrZero(switchAuto));
        m.put("Auto Scale Cubes", getIntOrZero(scaleAuto));
        m.put("Penalty Points", getIntOrZero(penaltyPoints));
        m.put("Vault Cubes", getIntOrZero(vault));
        m.put("Baseline", Boolean.toString(baseline.isChecked()));

        m.put("Ending Location", getRadioGroupStringSelection(finalPosition));
    }

    public void teleSwitchDown(View view) { updateItem(switchTele, -1, ZERO_TO_POS_INF);}

    public void teleSwitchUp(View view) { updateItem(switchTele, +1, ZERO_TO_POS_INF); }

    public void teleScaleDown(View view) { updateItem(scaleTele, -1, ZERO_TO_POS_INF); }

    public void teleScaleUp(View view) { updateItem(scaleTele, +1, ZERO_TO_POS_INF); }

    public void autoSwitchDown(View view) { updateItem(switchAuto, -1, ZERO_TO_POS_INF); }

    public void autoSwitchUp(View view) { updateItem(switchAuto, +1, ZERO_TO_POS_INF); }

    public void autoScaleDown(View view) { updateItem(scaleAuto, -1, ZERO_TO_POS_INF);}

    public void autoScaleUp(View view) { updateItem(scaleAuto, +1, ZERO_TO_POS_INF); }

    public void vaultDown(View view) { updateItem(vault, -1, ZERO_TO_POS_INF); }

    public void vaultUp(View view) { updateItem(vault, +1, ZERO_TO_POS_INF); }

    public void penaltyDown(View view) { updateItem(penaltyPoints, -5, NEG_INF_TO_ZERO); }

    public void penaltyUp(View view) { updateItem(penaltyPoints, +5, NEG_INF_TO_ZERO); }
}
