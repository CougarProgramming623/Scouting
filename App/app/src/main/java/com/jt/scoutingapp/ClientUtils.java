package com.jt.scoutingapp;

import android.os.Environment;

import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.AssignerList;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ClientUtils {
    public static final File ANDROID_SAVE_DIR;

    static {
        ANDROID_SAVE_DIR = new File(Environment.getExternalStorageDirectory(), ScoutingConstants.FOLDER_NAME);
        ANDROID_SAVE_DIR.mkdirs();
    }

    public static final File ANDROID_ASSIGNMENTS_FILE = new File(ANDROID_SAVE_DIR, ScoutingConstants.ANDROID_ASSIGNMENTS_FILE_NAME),
    ANDROID_MATCHES_DIR = new File(ANDROID_SAVE_DIR, ScoutingConstants.ANDROID_MATCHES_SAVE_DIRECTORY_NAME);

    public static AssignerList readAssignments() {
        return ScoutingUtils.read(ANDROID_ASSIGNMENTS_FILE, AssignerList.class);
    }
}
