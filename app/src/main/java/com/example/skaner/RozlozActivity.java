package com.example.skaner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class RozlozActivity extends AppCompatActivity {
    String URL,nazwa_urzytkownika,select,update;
    Connection poloczenie = null;
    EditText gniazdo_kod;
    ListView listView;
    Intent intent;
    KlasaConnection conn = new KlasaConnection();
    ArrayList<String> arrayList;
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozloz);
        intent=getIntent();
        arrayList=intent.getStringArrayListExtra("list");
        URL=intent.getStringExtra("url");
        nazwa_urzytkownika=intent.getStringExtra("nazwa");
    }

    @Override
    protected void onResume(){
        super.onResume();
        createList();
    }


    public void rozloz_scan(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setTorchEnabled(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                if(intentResult.getFormatName().equals("QR_CODE")){
                    gniazdo_kod=findViewById(R.id.rozloz_activity_gniazdo_kod);
                    gniazdo_kod.setText(intentResult.getContents());
                }else{
                    poloczenie=conn.connectionClass(URL);
                    if(poloczenie != null){
                        try{
                            select = "SELECT t_id FROM towary WHERE kod LIKE '"+intentResult.getContents()+"';";
                            Statement statement = poloczenie.createStatement();
                            ResultSet rs = statement.executeQuery(select);
                            if(rs.next()){
                                Intent intent_out = new Intent(this,RozlozActivityJestTowar.class);
                                intent_out.putExtra("URL",URL);
                                intent_out.putExtra("NAZWA",nazwa_urzytkownika);
                                intent_out.putExtra("KOD",intentResult.getContents());
                                intent_out.putExtra("LIST",arrayList);
                                startActivity(intent_out);

                            }else {
                                Intent intent_out = new Intent(this,RozlozActivityBrakTowaru.class);
                                intent_out.putExtra("URL",URL);
                                intent_out.putExtra("NAZWA",nazwa_urzytkownika);
                                intent_out.putExtra("KOD",intentResult.getContents());
                                intent_out.putExtra("LIST",arrayList);
                                startActivity(intent_out);
                            }
                        }catch (Exception e){
                            Toast.makeText(RozlozActivity.this,"SQLerr",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        brakPoloczenia.czy_poloczenie();
                    }
                }
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void createList(){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.list_layout,arrayList);
        listView=findViewById(R.id.rozloz_activity_listview);
        listView.setAdapter(arrayAdapter);
        onClickListView();
    }

    private void onClickListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
                LayoutInflater li = LayoutInflater.from(RozlozActivity.this);
                View promptView = li.inflate(R.layout.activity_rozloz_remove,null);
                AlertDialog.Builder alert = new AlertDialog.Builder(RozlozActivity.this);
                alert.setView(promptView);
                alert.setCancelable(false)
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrayList.remove(i);
                                createList();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Anujul", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert.create();
                alert.show();
            }
        });
    }

    public void rozloz_activity_cancel_btn(View view){
        LayoutInflater li = LayoutInflater.from(RozlozActivity.this);
        View promptView = li.inflate(R.layout.activity_rozloz_cancel,null);
        AlertDialog.Builder alert = new AlertDialog.Builder(RozlozActivity.this);
        alert.setView(promptView);
        alert.setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.clear();
                        gniazdo_kod=findViewById(R.id.rozloz_activity_gniazdo_kod);
                        gniazdo_kod.setText("");
                        createList();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert.create();
        alert.show();
    }

    public void rozloz_activity_confirm_btn(View view){
        gniazdo_kod=findViewById(R.id.rozloz_activity_gniazdo_kod);
        gniazdo_kod.setBackgroundColor(Color.WHITE);
        listView.setBackgroundColor(Color.WHITE);
        if(gniazdo_kod_validate()){
            if(arrayList.size()!=0){
                LayoutInflater li = LayoutInflater.from(this);
                View promptView = li.inflate(R.layout.activity_rozloz_confirm, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setView(promptView);
                alert.setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String g,t_id,ilo,u_id;
                                g=gniazdo_kod.getText().toString();
                                u_id=get_u_id();
                                Boolean pop=true;
                                Statement statement,stat;
                                ResultSet rs;
                                for(String i:arrayList){
                                    t_id=get_t_id(i.substring(i.indexOf("\t")+1,i.indexOf("\t|\t\t")));
                                    ilo=i.substring(i.indexOf("\t\t|\t")+4,(Integer)i.length());
                                    update = "SELECT ilosc FROM pozycja WHERE t_id="+t_id+" AND gniazdo LIKE '"+g+"';";
                                    try{
                                        stat = poloczenie.createStatement();
                                        rs = stat.executeQuery(update);
                                        if(rs.next()){
                                            ilo=String.valueOf(Integer.valueOf(rs.getString(1))+Integer.valueOf(ilo));
                                            update = "UPDATE pozycja SET ilosc ="+ilo+",u_id="+u_id+" WHERE t_id="+t_id+" AND gniazdo LIKE '"+g+"';";
                                        }else{
                                            update = "INSERT INTO pozycja(gniazdo,t_id,ilosc,u_id) VALUES('"+g+"',"+t_id+","+ilo+","+u_id+");";
                                        }
                                        statement = poloczenie.createStatement();
                                        statement.executeUpdate(update);

                                    }catch (Exception e){
                                        AlertDialog alertDialog = new AlertDialog.Builder(RozlozActivity.this).create();
                                        alertDialog.setTitle("BŁĄD");
                                        alertDialog.setMessage("Błąd podczas tworzenia pozycji dla"+i+"\n"+e.getMessage());
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                        pop=false;
                                    }
                                }
                                if(pop){
                                    Toast.makeText(RozlozActivity.this,"Poprawnie dodano rekordy",Toast.LENGTH_LONG).show();
                                }
                                arrayList.clear();
                                gniazdo_kod.setText("");
                                createList();
                                gniazdo_kod.setBackgroundColor(Color.WHITE);
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert.create();
                alert.show();
            }else{
                listView.setBackgroundColor(Color.RED);
                Toast.makeText(this,"LISTA NIE MOŻE BYĆ PUSTA",Toast.LENGTH_LONG).show();
            }
        }else{
            gniazdo_kod.setBackgroundColor(Color.RED);
            Toast.makeText(this,"BŁĄD KODU GNIAZDA\nNIE ZNALEZIONO KODU GNIAZDA",Toast.LENGTH_LONG).show();
        }
    }


    private Boolean gniazdo_kod_validate(){
        poloczenie=conn.connectionClass(URL);
        String select_gniazdo_val;
        if(poloczenie != null){
            select_gniazdo_val= "SELECT g_id FROM gniazdo WHERE gniazdo_kod LIKE '"+gniazdo_kod.getText().toString()+"';";
            try{
                Statement statement = poloczenie.createStatement();
                ResultSet rs = statement.executeQuery(select_gniazdo_val);
                while (rs.next()){
                    return true;
                }
                return false;
            }catch (Exception e){
                Toast.makeText(this,"Błąd serwera",Toast.LENGTH_LONG).show();
                return false;
            }

        }else{
            brakPoloczenia.czy_poloczenie();
            return false;
        }
    }

    private String get_t_id(String t_id_kod){
        String get_t_id_select = "SELECT t_id FROM towary WHERE kod LIKE '"+t_id_kod+"';";
        try{
            Statement statement = poloczenie.createStatement();
            ResultSet rs = statement.executeQuery(get_t_id_select);
            while (rs.next()){
                return rs.getString(1);
            }
        }catch (Exception e){
            return "";
        }
        return "";
    }

    private String get_u_id(){
        String get_u_id_select = "SELECT u_id FROM users WHERE nazwa LIKE '"+nazwa_urzytkownika+"';";
        poloczenie=conn.connectionClass(URL);
        try{
            Statement statement = poloczenie.createStatement();
            ResultSet rs = statement.executeQuery(get_u_id_select);
            while (rs.next()){
                return rs.getString(1);
            }
        }catch (Exception e){
            gniazdo_kod.setText(e.getMessage());
            return "";
        }
        return "";
    }
}