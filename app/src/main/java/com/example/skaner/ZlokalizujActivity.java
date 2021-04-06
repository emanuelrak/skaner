package com.example.skaner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class
ZlokalizujActivity extends AppCompatActivity {
    String URL, nazwa_urzytkownika,select;
    Connection poloczenie = null;
    EditText kod;
    ListView listView;
    KlasaConnection conn = new KlasaConnection();
    ArrayList<String> arrayList=new ArrayList<>();
    BrakPoloczenia brakPoloczenia = new BrakPoloczenia(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zlokalizuj);
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        nazwa_urzytkownika = intent.getStringExtra("NAZWA");
    }

    @Override
    protected void onResume() {
        super.onResume();
        createList();
    }

    public void zlokalizuj_search_btn(View view){
        createList();
    }

    private void createList(){
        try{
            poloczenie = conn.connectionClass(URL);
            kod=findViewById(R.id.zlokalizuj_kod);
            if(poloczenie !=null){
                select="SELECT t.nazwa,g.gniazdo_kod,p.ilosc FROM towary t JOIN pozycja p ON p.t_id=t.t_id JOIN gniazdo g ON p.gniazdo=g.gniazdo_kod WHERE t.kod LIKE '"+kod.getText().toString()+"' ORDER BY p.ilosc DESC";
                Statement statement = poloczenie.createStatement();
                ResultSet rs = statement.executeQuery(select);
                arrayList.clear();
                while (rs.next()){
                    arrayList.add("\t"+rs.getString(1)+"\t|\t\t"+rs.getString(2)+"\t\t|\t"+String.valueOf(rs.getInt(3)));
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.list_layout,arrayList);
                listView=findViewById(R.id.zlokalizuj_listview);
                listView.setAdapter(arrayAdapter);
                onClickListView();
            }else{
                brakPoloczenia.czy_poloczenie();
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void onClickListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
                String arr_ilosc,arr_gniazdo;
                arr_ilosc=arrayList.get(i);
                Integer indexOf;
                indexOf=arr_ilosc.indexOf("\t\t|\t");
                arr_ilosc=arr_ilosc.substring(indexOf+4,(Integer)arr_ilosc.length());
                arr_gniazdo=arrayList.get(i);
                indexOf=arr_gniazdo.indexOf("\t|\t\t");
                arr_gniazdo=arr_gniazdo.substring(indexOf+4,arr_gniazdo.indexOf("\t\t|\t"));

                Intent intent = new Intent(ZlokalizujActivity.this,ZlokalizujActivityCrud.class);
                intent.putExtra("gniazdo",arr_gniazdo);
                intent.putExtra("ilosc",arr_ilosc);
                intent.putExtra("kod",kod.getText().toString());
                intent.putExtra("nazwa_urzytkownika",nazwa_urzytkownika);
                intent.putExtra("url",URL);
                startActivity(intent);
            }
        });
    }

    public void zlokalizuj_skaner_btn(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                kod=findViewById(R.id.zlokalizuj_kod);
                kod.setText(intentResult.getContents());
                createList();
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}