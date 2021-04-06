package com.example.skaner;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class BrakPoloczenia {
    Context context;

    public BrakPoloczenia(Context context) {
        this.context = context;
    }

    public void czy_poloczenie() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.activity_menu_prompt_connerr, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(promptView);
        alert.setCancelable(false)
                .setPositiveButton("Spróbuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Wyloguj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        alert.create();
        alert.show();
    }
}
