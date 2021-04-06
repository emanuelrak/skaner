package com.example.skaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WydajActivityLokalizacja extends AppCompatActivity {
    TextView wydaj_lokalizacja_ilosc,wydaj_lokalizacja_kod_towaru,wydaj_lokalizacja_gniazdo;
    String kod,gniazdo,ilosc,row,nazwa_urzytkownika,url;
    Integer ilosc_max;
    ArrayList<String> arrayList;
    Button confirm,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wydaj_lokalizacja);
        Intent intent = getIntent();
        kod=intent.getStringExtra("kod");
        gniazdo=intent.getStringExtra("gniazdo");
        ilosc=intent.getStringExtra("ilosc");
        ilosc_max=Integer.valueOf(ilosc);
        arrayList=intent.getStringArrayListExtra("list");
        nazwa_urzytkownika=intent.getStringExtra("nazwa_urzytkownika");
        url=intent.getStringExtra("url");

        wydaj_lokalizacja_kod_towaru=findViewById(R.id.wydaj_lokalizacja_kod_towaru);
        wydaj_lokalizacja_kod_towaru.setText(kod);

        wydaj_lokalizacja_gniazdo=findViewById(R.id.wydaj_lokalizacja_gniazdo);
        wydaj_lokalizacja_gniazdo.setText(gniazdo);

        wydaj_lokalizacja_ilosc=findViewById(R.id.wydaj_lokalizacja_ilosc);
        wydaj_lokalizacja_ilosc.setText(ilosc);

        cancel=findViewById(R.id.wydaj_lokalizacja_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wydaj_lokalizacja_cancel_btn();
            }
        });

        confirm=findViewById(R.id.wydaj_lokalizacja_confirm_btn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wydaj_lokalizacja_confirm_btn();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
            String temp,temp_i;
            temp="\t"+kod+"\t|\t\t"+gniazdo+"\t\t|\t";
            for(String i:arrayList){
                if(i.contains("-WYDANE-\t")){
                    temp_i=i.substring(9,i.indexOf("\t\t|\t")+4);
                }else{
                    temp_i=i.substring(0,i.indexOf("\t\t|\t")+4);
                }
                if(temp.equals(temp_i)){
                    ilosc_max=ilosc_max-Integer.valueOf(i.substring(i.indexOf("\t\t|\t")+4,(Integer)i.length()));
                }
            }
            wydaj_lokalizacja_ilosc.setText(String.valueOf(ilosc_max));
            if(ilosc_max <= 0){
                wydaj_lokalizacja_ilosc.setText("0");
                Toast.makeText(this,"Wszystkie sztuki z danego gnizada są już w liście do wydania!",Toast.LENGTH_LONG).show();
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wydaj_lokalizacja_cancel_btn();
                    }
                });
            }
    }


    public void wydaj_lokalizacja_confirm_btn(){
        row="\t"+kod+"\t|\t\t"+gniazdo+"\t\t|\t"+wydaj_lokalizacja_ilosc.getText().toString();
        arrayList.add(row);
        finish();
        Intent intent = new Intent(this,WydajActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("nazwa",nazwa_urzytkownika);
        intent.putExtra("list",arrayList);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void wydaj_lokalizacja_cancel_btn(){
        Toast.makeText(this,"Anulowano",Toast.LENGTH_LONG).show();
        finish();
    }

    public void wydaj_lokalizacja_plus_btn(View view){
        wydaj_lokalizacja_ilosc=findViewById(R.id.wydaj_lokalizacja_ilosc);
        Integer i=Integer.valueOf(wydaj_lokalizacja_ilosc.getText().toString());
        if(i<ilosc_max) {
            i++;
            wydaj_lokalizacja_ilosc.setText(String.valueOf(i));
        }
    }

    public void wydaj_lokalizacja_minus_btn(View view){
        wydaj_lokalizacja_ilosc=findViewById(R.id.wydaj_lokalizacja_ilosc);
        Integer i=Integer.valueOf(wydaj_lokalizacja_ilosc.getText().toString());
        if(i>1){
            i--;
            wydaj_lokalizacja_ilosc.setText(String.valueOf(i));
        }
    }
}