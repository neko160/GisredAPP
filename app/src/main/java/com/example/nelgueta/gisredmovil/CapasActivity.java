package com.example.nelgueta.gisredmovil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import java.util.ArrayList;

/**
 * Created by nelgueta on 29/02/2016.
 */
public class CapasActivity extends ActionBarActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dialog dialog;
        final String[] items = {" SSEE", "Salida Alimentador", " JSON", " C#", " Objective-C"};
        final ArrayList itemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Languages you know : ");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {
                        if (isSelected) {
                            itemsSelected.add(selectedItemId);
                        } else if (itemsSelected.contains(selectedItemId)) {
                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                        }
                    }
                })
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Your logic when OK button is clicked
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
}
