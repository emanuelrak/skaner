package com.example.skaner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class WydajActivity extends AppCompatActivity {
    String URL,nazwa_urzytkownika,select,update;
    Connection poloczenie = null;
    EditText kod;
    ListView listView_lokalizacja,listView_kosz;
    Intent intent;
    KlasaConnection conn = new KlasaConnection();
    ArrayList<String> arrayList_lokalizacja = new ArrayList<>();
    ArrayList<String> arrayList_kosz = new ArrayList<>();
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wydaj);
        intent=getIntent();
        arrayList_kosz=intent.getStringArrayListExtra("list");
        URL=intent.getStringExtra("url");
        nazwa_urzytkownika=intent.getStringExtra("nazwa");
    }
    @Override
    protected void onResume(){
        super.onResume();
        createList_kosz();
    }

    private void createList_zlokalizuj(){
        try{
            poloczenie = conn.connectionClass(URL);
            kod=findViewById(R.id.activity_wydaj_kod);
            if(poloczenie != null){
                select="SELECT t.nazwa,g.gniazdo_kod,p.ilosc FROM towary t JOIN pozycja p ON p.t_id=t.t_id JOIN gniazdo g ON p.gniazdo=g.gniazdo_kod WHERE t.kod LIKE '"+kod.getText().toString()+"' ORDER BY p.ilosc DESC";
                Statement statement = poloczenie.createStatement();
                ResultSet rs = statement.executeQuery(select);
                arrayList_lokalizacja.clear();
                while (rs.next()){
                    arrayList_lokalizacja.add("\t"+rs.getString(1)+"\t|\t\t"+rs.getString(2)+"\t\t|\t"+String.valueOf(rs.getInt(3)));
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.list_layout,arrayList_lokalizacja);
                listView_lokalizacja=findViewById(R.id.activity_wydaj_listview_lokalizacja);
                listView_lokalizacja.setAdapter(arrayAdapter);
                onClickListView_lokalizacja();
            }else{
                brakPoloczenia.czy_poloczenie();
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void onClickListView_lokalizacja(){
        listView_lokalizacja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String arr_ilosc,arr_gniazdo;
                arr_ilosc=arrayList_lokalizacja.get(i);
                Integer indexOf;
                indexOf=arr_ilosc.indexOf("\t\t|\t");
                arr_ilosc=arr_ilosc.substring(indexOf+4,(Integer)arr_ilosc.length());
                arr_gniazdo=arrayList_lokalizacja.get(i);
                indexOf=arr_gniazdo.indexOf("\t|\t\t");
                arr_gniazdo=arr_gniazdo.substring(indexOf+4,arr_gniazdo.indexOf("\t\t|\t"));

                Intent intent_out = new Intent(WydajActivity.this,WydajActivityLokalizacja.class);
                intent_out.putExtra("gniazdo",arr_gniazdo);
                intent_out.putExtra("ilosc",arr_ilosc);
                intent_out.putExtra("kod",kod.getText().toString());
                intent_out.putExtra("list",arrayList_kosz);
                intent_out.putExtra("nazwa_urzytkownika",nazwa_urzytkownika);
                intent_out.putExtra("url",URL);
                startActivity(intent_out);
            }
        });
    }

    private void createList_kosz(){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.list_layout,arrayList_kosz);
        listView_kosz=findViewById(R.id.activity_wydaj_listview_kosz);
        listView_kosz.setAdapter(arrayAdapter);
        onClickListView_kosz();
    }
    private void onClickListView_kosz(){
        listView_kosz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(WydajActivity.this);
                alert.setTitle("Wybór działania");
                alert.setMessage("Oznacz jako wydane lub usuń pozycje");
                alert.setCancelable(false)
                        .setPositiveButton("Wydane", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String row=arrayList_kosz.get(i);
                                Integer indexOf = row.indexOf("-WYDANE-\t");
                                if(indexOf == -1){
                                    arrayList_kosz.set(i,"-WYDANE-\t"+row);
                                    createList_kosz();
                                }else{
                                    String substring = row.substring(indexOf+9,(Integer)row.length());
                                    arrayList_kosz.set(i,substring);
                                    createList_kosz();
                                }
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrayList_kosz.remove(i);
                                createList_kosz();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
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

    public void wydaj_scan(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null && intentResult.getContents() != null && !intentResult.getFormatName().equals("QR_CODE")){
            kod=findViewById(R.id.activity_wydaj_kod);
            kod.setText(intentResult.getContents());
            createList_zlokalizuj();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public void wydaj_kod_search_btn(View view){
        createList_zlokalizuj();
    }

    public void wydaj_cancel_btn(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(WydajActivity.this);
        alert.setTitle("Anulowanie");
        alert.setMessage("Czy na pewno chcesz wyczyścić listę wydania?");
        alert.setCancelable(false);
        alert.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                arrayList_kosz.clear();
                createList_kosz();
                dialog.cancel();
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

    public void wydaj_confirm_btn(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(WydajActivity.this);
        alert.setTitle("Zatwierdzenie wydania");
        alert.setMessage("Czy na pewno chcesz sfinalizować wydawanie?");
        alert.setCancelable(false);
        alert.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                poloczenie = conn.connectionClass(URL);
                if(poloczenie != null){
                    try{
                        String t_id=null,kod_temp,gniazdo_temp,ilosc_temp,ilosc_select=null;
                        for(String i:arrayList_kosz){
                            if(i.contains("-WYDANE-\t")){
                                kod_temp=i.substring(10,i.indexOf("\t|\t\t"));
                                gniazdo_temp=i.substring(i.indexOf("\t|\t\t")+4,i.indexOf("\t\t|\t"));
                                ilosc_temp=i.substring(i.indexOf("\t\t|\t")+4,(Integer)i.length());
                            }else{
                                kod_temp=i.substring(1,i.indexOf("\t|\t\t"));
                                gniazdo_temp=i.substring(i.indexOf("\t|\t\t")+4,i.indexOf("\t\t|\t"));
                                ilosc_temp=i.substring(i.indexOf("\t\t|\t")+4,(Integer)i.length());
                            }
                            select = "SELECT p.ilosc, t.t_id FROM pozycja p JOIN towary t ON p.t_id=t.t_id WHERE t.kod LIKE '"+kod_temp+"' AND p.gniazdo LIKE '"+gniazdo_temp+"';";
                            Statement stat_select = poloczenie.createStatement();
                            ResultSet rs = stat_select.executeQuery(select);
                            if(rs.next()){
                                ilosc_select=rs.getString(1);
                                t_id=rs.getString(2);
                                ilosc_temp=String.valueOf(Integer.valueOf(ilosc_select)-Integer.valueOf(ilosc_temp));
                                if(ilosc_temp.equals("0")){
                                    update="DELETE FROM pozycja WHERE t_id="+t_id+" AND gniazdo LIKE '"+gniazdo_temp+"';";
                                }else{
                                    update="UPDATE pozycja SET ilosc ="+ilosc_temp+" WHERE t_id="+t_id+" AND gniazdo LIKE '"+gniazdo_temp+"';";
                                }
                                Statement stat_update = poloczenie.createStatement();
                                stat_update.executeUpdate(update);
                            }
                        }
                        Toast.makeText(WydajActivity.this,"Poprawnie zaktualizowano",Toast.LENGTH_LONG).show();
                        arrayList_kosz.clear();
                        createList_kosz();
                    }catch (Exception e){
                        Toast.makeText(WydajActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }else{
                    dialog.cancel();
                    brakPoloczenia.czy_poloczenie();
                }
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
}