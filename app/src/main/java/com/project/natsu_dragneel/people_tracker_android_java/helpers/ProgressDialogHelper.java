package com.project.natsu_dragneel.people_tracker_android_java.helpers;

import android.app.Activity;
import android.app.ProgressDialog;

public class ProgressDialogHelper {
    ProgressDialog dialog;

    public void progress_dialog(Activity activity, String message){
        dialog = new ProgressDialog(activity);
    }
}
