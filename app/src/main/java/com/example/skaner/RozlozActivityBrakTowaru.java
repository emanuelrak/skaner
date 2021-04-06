package com.example.skaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

public class RozlozActivityBrakTowaru extends AppCompatActivity {
    String URL,nazwa_urzytkownika,update,kod,row;
    Connection poloczenie;
    EditText et_ilosc,et_nazwa;
    TextView tv_kod;
    ArrayList arrayList;
    KlasaConnection conn = new KlasaConnection();
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozloz_brak_towaru);
        Intent intent = getIntent();
        arrayList=intent.getStringArrayListExtra("LIST");
        URL=intent.getStringExtra("URL");
        nazwa_urzytkownika=intent.getStringExtra("NAZWA");
        kod=intent.getStringExtra("KOD");
        tv_kod=findViewById(R.id.rozloz_braktowaru_kod);
        tv_kod.setText(kod);
    }

    public void rozloz_braktowaru_confirm_btn(View view){
        poloczenie=conn.connectionClass(URL);
        if(poloczenie != null){
            et_nazwa=findViewById(R.id.rozloz_braktowaru_nazwa);
            et_ilosc=findViewById(R.id.rozloz_braktowaru_ilosc);
            if(et_nazwa.getText().toString().equals("") || et_ilosc.getText().toString().equals("0")){
                et_nazwa.setHint("POLE NAZWA NIE MOŻE POZOSTAĆ PUSTE");
                Toast.makeText(this,"POLE NAZWA NIE MOŻE BYĆ PUSTE\nILOŚĆ MUSI BYĆ WIĘKSZA OD 0",Toast.LENGTH_LONG).show();
            }else{
                update = "INSERT INTO towary(nazwa,kod) VALUES('"+et_nazwa.getText()+"','"+tv_kod.getText().toString()+"');";
                try{
                    Statement statement = poloczenie.createStatement();
                    statement.executeUpdate(update);
                    Toast.makeText(this,"Prawidłowo dodano nowy towar",Toast.LENGTH_LONG).show();
                    row="\t"+kod+"\t|\t\t"+et_nazwa.getText().toString()+"\t\t|\t"+et_ilosc.getText().toString();
                    arrayList.add(row);
                    finish();
                    Intent intent = new Intent(this,RozlozActivity.class);
                    intent.putExtra("url",URL);
                    intent.putExtra("nazwa",nazwa_urzytkownika);
                    intent.putExtra("list",arrayList);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(this,"Błąd\n"+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }else{
            brakPoloczenia.czy_poloczenie();
        }
    }

    public void rozloz_braktowaru_cancel_btn(View view){
        finish();
    }

    public void rozloz_braktowaru_plus_btn(View view){
        et_ilosc=findViewById(R.id.rozloz_braktowaru_ilosc);
        Integer i=Integer.valueOf(et_ilosc.getText().toString())+1;
        et_ilosc.setText(String.valueOf(i));
    }

    public void rozloz_braktowaru_minus_btn(View view){
        et_ilosc=findViewById(R.id.rozloz_braktowaru_ilosc);
        Integer i=Integer.valueOf(et_ilosc.getText().toString());
        if(i>1){
            i--;
            et_ilosc.setText(String.valueOf(i));
        }
    }
}