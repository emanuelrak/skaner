package com.example.skaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.sql.Connection;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    String URL, nazwa_urzytkownika;
    KlasaConnection conn = new KlasaConnection();
    Connection poloczenie = null;
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        poloczenie = conn.connectionClass(URL);
        nazwa_urzytkownika = intent.getStringExtra("NAZWA");
    }


    public void rozloz_btn(View view) {
        poloczenie = conn.connectionClass(URL);
        if(poloczenie != null){
            Intent intent = new Intent(this,RozlozActivity.class);
            intent.putExtra("url",URL);
            intent.putExtra("nazwa",nazwa_urzytkownika);
            ArrayList<String> arrayList = new ArrayList<>();
            intent.putExtra("list",arrayList);
            startActivity(intent);
        }else{
            brakPoloczenia.czy_poloczenie();
        }
    }

    public void wydaj_btn(View view) {
        poloczenie = conn.connectionClass(URL);
        if(poloczenie != null){
            Intent intent = new Intent(this,WydajActivity.class);
            intent.putExtra("url",URL);
            intent.putExtra("nazwa",nazwa_urzytkownika);
            ArrayList<String> arrayList = new ArrayList<>();
            intent.putExtra("list",arrayList);
            startActivity(intent);
        }else{
            brakPoloczenia.czy_poloczenie();
        }
    }

    public void zlokalizuj(View view) {
        poloczenie = conn.connectionClass(URL);
        if(poloczenie != null){
            Intent intent = new Intent(this,ZlokalizujActivity.class);
            intent.putExtra("URL",URL);
            intent.putExtra("NAZWA",nazwa_urzytkownika);
            startActivity(intent);
        }else{
            brakPoloczenia.czy_poloczenie();
        }
    }
}
