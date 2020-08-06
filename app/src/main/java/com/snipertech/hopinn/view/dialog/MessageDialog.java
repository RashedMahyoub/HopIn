package com.snipertech.hopinn.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.snipertech.hopinn.R;

public class MessageDialog {
    public void initDialog(Activity activity, String mName, String msg){
        Dialog dialog =  new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.message_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        MaterialTextView message = dialog.findViewById(R.id.message);
        MaterialTextView name = dialog.findViewById(R.id.name);
        View close = dialog.findViewById(R.id.close_window);

        message.setText(msg);
        name.setText(mName);
        close.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }
}
