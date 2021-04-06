package com.example.skaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;

public class LoginActivity extends Activity {
    private EditText login_nazwa,login_haslo,login_ip;
    private TextView login_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login_btn(View view){
        login_nazwa=findViewById(R.id.login_nazwa);
        login_haslo=findViewById(R.id.login_haslo);
        login_ip=findViewById(R.id.login_ip);
        login_error=findViewById(R.id.login_error);
        if(login_haslo.getText().toString().equals("") || login_nazwa.getText().toString().equals("")|| login_ip.getText().toString().equals("")){
            login_error.setText("Uzupełnij wszystkie pola");
        }else{
            Connection poloczenie=null;
            KlasaConnection proba_poloczenia = new KlasaConnection(login_nazwa.getText().toString(),login_haslo.getText().toString(),login_ip.getText().toString());
            poloczenie=proba_poloczenia.connectionClass();
            if(poloczenie != null){
                Intent intent = new Intent(this,MenuActivity.class);
                intent.putExtra("URL",proba_poloczenia.getConnectionURL());
                intent.putExtra("NAZWA",login_nazwa.getText().toString());
                startActivity(intent);
            }else{
                login_error.setText("Błędne dane lub brak połączenia");
            }
        }
    }

    public void default_conf_btn(View view){
        login_nazwa=findViewById(R.id.login_nazwa);
        login_haslo=findViewById(R.id.login_haslo);
        login_ip=findViewById(R.id.login_ip);
        login_nazwa.setText("test1");
        login_haslo.setText("TESTzaq1@WSX");
        login_ip.setText("skaner.database.windows.net");
    }
}