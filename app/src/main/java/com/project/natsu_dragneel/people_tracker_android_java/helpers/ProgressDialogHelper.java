package com.project.natsu_dragneel.people_tracker_android_java.helpers;

import android.app.Activity;
import android.app.ProgressDialog;

public class ProgressDialogHelper {
    ProgressDialog dialog;

    public void build_dialog(Activity activity){
        dialog=new ProgressDialog(activity);
    }

    public void show_dialog(String message){
        dialog.setMessage(message);
        dialog.show();
    }

    public void dismiss_dialog(){
        dialog.dismiss();
    }
}
