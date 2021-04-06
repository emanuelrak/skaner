package com.example.skaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ZlokalizujActivityCrud extends AppCompatActivity{
    String kod,gniazdo,ilosc,URL,nazwa_urzytkownika;
    Connection poloczenie;
    KlasaConnection conn = new KlasaConnection();
    TextView zlokalizuj_prompt_kod_towaru,zlokalizuj_prompt_gniazdo;
    TextView zlokalizuj_prompt_ilosc;
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zlokalizuj_crud);
        Intent intent = getIntent();
        kod=intent.getStringExtra("kod");
        gniazdo=intent.getStringExtra("gniazdo");
        ilosc=intent.getStringExtra("ilosc");
        URL=intent.getStringExtra("url");
        nazwa_urzytkownika=intent.getStringExtra("nazwa_urzytkownika");

        zlokalizuj_prompt_gniazdo=findViewById(R.id.zlokalizuj_prompt_gniazdo);
        zlokalizuj_prompt_gniazdo.setText("Gniazdo: "+gniazdo);

        zlokalizuj_prompt_ilosc=findViewById(R.id.zlokalizuj_prompt_ilosc);
        zlokalizuj_prompt_ilosc.setText(ilosc);

        zlokalizuj_prompt_kod_towaru=findViewById(R.id.zlokalizuj_prompt_kod_towaru);
        zlokalizuj_prompt_kod_towaru.setText("Kod towaru: "+kod);
    }

    public void zlokalizuj_crud_confirm_btn(View view){
        try{
            poloczenie=conn.connectionClass(URL);
            ilosc=zlokalizuj_prompt_ilosc.getText().toString();
            String select1,select2,update;
            if(poloczenie != null){
                select1="SELECT t_id FROM towary WHERE kod LIKE '"+kod+"';";
                select2="SELECT u_id FROM users WHERE nazwa LIKE '"+nazwa_urzytkownika+"';";
                Statement statement1 = poloczenie.createStatement();
                Statement statement2 = poloczenie.createStatement();
                Statement statement3 = poloczenie.createStatement();
                ResultSet rs1 = statement1.executeQuery(select1);
                ResultSet rs2 = statement2.executeQuery(select2);
                while (rs1.next()){
                    select1=rs1.getString(1);
                }
                while (rs2.next()){
                    select2=rs2.getString(1);
                }
                if(ilosc.equals("0")){
                    update = "DELETE FROM pozycja WHERE t_id="+select1+" AND gniazdo LIKE '"+gniazdo+"';";
                    statement3.executeUpdate(update);
                }else{
                    update = "UPDATE pozycja SET ilosc="+ilosc+", u_id="+select2+" WHERE t_id="+select1+" AND gniazdo LIKE '"+gniazdo+"';";
                    statement3.executeUpdate(update);
                }
            Toast.makeText(this,"Zaktualizowano pozycje",Toast.LENGTH_LONG).show();

            finish();

            }else{
                brakPoloczenia.czy_poloczenie();
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void zlokalizuj_crud_cancel_btn(View view){
        Toast.makeText(this,"Anulowano",Toast.LENGTH_LONG).show();
        finish();
    }

    public void zlokalizuj_crud_plus_btn(View view){
        zlokalizuj_prompt_ilosc=findViewById(R.id.zlokalizuj_prompt_ilosc);
        Integer i=Integer.valueOf(zlokalizuj_prompt_ilosc.getText().toString())+1;
        zlokalizuj_prompt_ilosc.setText(String.valueOf(i));
    }

    public void zlokalizuj_crud_minus_btn(View view){
        zlokalizuj_prompt_ilosc=findViewById(R.id.zlokalizuj_prompt_ilosc);
        Integer i=Integer.valueOf(zlokalizuj_prompt_ilosc.getText().toString());
        if(i>=1){
            i--;
            zlokalizuj_prompt_ilosc.setText(String.valueOf(i));
        }
    }
}