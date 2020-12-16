package com.giscen.gisredapp.entity;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.giscen.gisredapp.R;
import com.giscen.gisredapp.util.Util;

public class customDialog {

    public void showErrorDialog(String cabecera, String mensaje, String msgButton, Integer accion, Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder( activity, R.style.AlertDialogTheme );
        View view = LayoutInflater.from( activity ).inflate( R.layout.dialog_error,(ConstraintLayout) activity.findViewById( R.id.layoutDialogContainer ));

        alertBuilder.setView( view );

        ((TextView) view.findViewById( R.id.textTitle )).setText( cabecera );
        TextView messageText = (TextView) view.findViewById( R.id.textMessage );//.setText( mensaje );
        messageText.setText( mensaje );
        messageText.setGravity( Gravity.CENTER );

        ((Button) view.findViewById( R.id.BtnAction )).setText( msgButton );
        ((ImageView) view.findViewById( R.id.imageIcon )).setImageResource( R.drawable.ic_baseline_info_24 );


        final AlertDialog alertDialog = alertBuilder.create();


        view.findViewById( R.id.BtnAction ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (accion) {
                    case 1:
                        activity.finish();
                    case 2:
                        alertDialog.cancel();
                    default:
                        alertDialog.dismiss();

                }
            }
        } );
        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
        }
        alertDialog.show();
    }

    public void showSuccesDialog(String cabecera, String mensaje, String msgButton, Integer accion, Activity activity){
        AlertDialog.Builder builder= new AlertDialog.Builder( activity,R.style.AlertDialogTheme );
        View view = LayoutInflater.from( activity).inflate(
                R.layout.dialog_success,
                (ConstraintLayout)  activity.findViewById( R.id.layoutDialogContainer )
        );
        builder.setView( view );
        ((TextView) view.findViewById( R.id.textTitle)).setText(cabecera);
        ((TextView) view.findViewById( R.id.textMessage)).setText( mensaje );
        ((Button) view.findViewById( R.id.BtnAction)).setText( msgButton);
        ((ImageView) view.findViewById( R.id.imageIcon )).setImageResource( R.drawable.ic_baseline_check_24 );

        final AlertDialog alertDialog = builder.create();

        view.findViewById( R.id.BtnAction ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
        }
        alertDialog.show();
    }

    public void showWarningDialog(String cabecera, String mensaje, String msgButtonYes, String msgButtonNo, Integer accion, Activity activity){
        AlertDialog.Builder builder= new AlertDialog.Builder( activity,R.style.AlertDialogTheme );
        View view = LayoutInflater.from( activity ).inflate(
                R.layout.dialog_warning,
                (ConstraintLayout) activity.findViewById( R.id.layoutDialogContainer )
        );
        builder.setView( view );
        ((TextView) view.findViewById( R.id.textTitle)).setText( cabecera );
        TextView messageText = (TextView) view.findViewById( R.id.textMessage );//.setText( mensaje );
        messageText.setText( mensaje );
        messageText.setGravity( Gravity.CENTER );
        ((Button) view.findViewById( R.id.BtnYes)).setText( msgButtonYes );
        ((Button) view.findViewById( R.id.BtnNo)).setText( msgButtonNo );
        ((ImageView) view.findViewById( R.id.imageIcon )).setImageResource( R.drawable.ic_baseline_warning_24 );

        final AlertDialog alertDialog = builder.create();

        view.findViewById( R.id.BtnYes ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (accion) {
                    case 1:
                        alertDialog.dismiss();
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, Util.REQUEST_READ_PHONE_STATE);
                    case 2:
                        Util.logout( activity );
                        activity.finish();
                    default:


                }

            }
        });

        view.findViewById( R.id.BtnNo ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (accion) {
                    case 1:
                        activity.finish();
                    case 2:
                        alertDialog.cancel();
                    default:


                }
            }
        });

        if(alertDialog.getWindow() !=null){
            alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
        }
        alertDialog.show();
    }

}






