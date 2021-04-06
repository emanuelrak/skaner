package com.example.skaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class RozlozActivityJestTowar extends AppCompatActivity {
    String URL,nazwa_urzytkownika,kod,row,select;
    Connection poloczenie;
    EditText et_ilosc;
    TextView tv_kod, tv_nazwa;
    ArrayList arrayList;
    KlasaConnection conn = new KlasaConnection();
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozloz_jest_towar);
        Intent intent = getIntent();
        arrayList=intent.getStringArrayListExtra("LIST");
        URL=intent.getStringExtra("URL");
        nazwa_urzytkownika=intent.getStringExtra("NAZWA");
        kod=intent.getStringExtra("KOD");
        tv_kod=findViewById(R.id.rozloz_jest_towar_kod);
        tv_kod.setText(kod);
        tv_nazwa=findViewById(R.id.rozloz_jest_towar_nazwa);
        try{
            select="SELECT nazwa FROM towary WHERE kod LIKE '"+kod+"';";
            poloczenie=conn.connectionClass(URL);
            Statement statement = poloczenie.createStatement();
            ResultSet rs = statement.executeQuery(select);
            while (rs.next()){
                tv_nazwa.setText(rs.getString(1));
            }
        }catch (Exception e){
            Toast.makeText(this,"Błąd serwera",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void rozloz_jest_towar_confirm_btn(View view){
        poloczenie = conn.connectionClass(URL);
        et_ilosc=findViewById(R.id.rozloz_jest_towar_ilosc);
        if(poloczenie != null){
            row="\t"+kod+"\t|\t\t"+tv_nazwa.getText().toString()+"\t\t|\t"+et_ilosc.getText().toString();
            arrayList.add(row);
            finish();
            Intent intent = new Intent(this,RozlozActivity.class);
            intent.putExtra("url",URL);
            intent.putExtra("nazwa",nazwa_urzytkownika);
            intent.putExtra("list",arrayList);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            brakPoloczenia.czy_poloczenie();
        }
    }



    public void rozloz_jest_towar_cancel_btn(View view){
        finish();
    }

    public void rozloz_jest_towar_plus_btn(View view){
        et_ilosc=findViewById(R.id.rozloz_jest_towar_ilosc);
        Integer i=Integer.valueOf(et_ilosc.getText().toString())+1;
        et_ilosc.setText(String.valueOf(i));
    }

    public void rozloz_jest_towar_minus_btn(View view){
        et_ilosc=findViewById(R.id.rozloz_jest_towar_ilosc);
        Integer i=Integer.valueOf(et_ilosc.getText().toString());
        if(i>1){
            i--;
            et_ilosc.setText(String.valueOf(i));
        }
    }
}