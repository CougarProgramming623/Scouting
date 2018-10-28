package com.jt.scoutingapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

public class Popup extends Activity{

    Button close;
    Button submit;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        close = findViewById(R.id.no);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit = findViewById(R.id.yes);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchSubmission m = new MatchSubmission(1, 1, TeamColor.BLUE);
                File file = new File(ScoutingConstants.ANDROID_SAVE_DIRECTORY, "test.dat");
                ScoutingUtils.write(m, file);


                finish();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.6));
    }
}
