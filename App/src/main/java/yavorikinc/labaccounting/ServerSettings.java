package yavorikinc.labaccounting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServerSettings extends AppCompatActivity {

    private EditText ipAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        ipAdress = (EditText) findViewById(R.id.ip_adress);

        initClicked();
    }

    @Override
    public void onBackPressed(){

        Toast.makeText(this, "Exc: " + "Натисніть кнопку 'Зберегти'", Toast.LENGTH_LONG).show();
        /*try{
            if("".equals(ipAdress.getText().toString())) {
                throw new Throwable("Введіть адресу.");
                //Toast.makeText(this,"Введіть адресу.", Toast.LENGTH_SHORT).show();
            }

            super.onBackPressed();
            Intent i = new Intent(Intent.ACTION_MAIN);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            finish();

        } catch(Throwable t) {
            Toast.makeText(this, "Exc: " + t.toString(), Toast.LENGTH_LONG).show();
        }*/
    }

    public void initClicked() {

        Button save = (Button) findViewById(R.id.save_ip_adress);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //readFromFile(v);

                if(saveIpToFile(v)){

                    //onPause();
                    //finish();
                    //setText();
                    //System.exit(0);

                    //finish();
                    //overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                }
                //readFromFile(v);
            }
        });
    }

    public void setText(){

        TextView v = (TextView) findViewById(R.id.hello);

        SharedPreferences myPref = getSharedPreferences("myPrefs-ip", MODE_PRIVATE);
        String str = myPref.getString("ip", "127.0.0.1");

        v.setText(str);

        //Toast.makeText(this,,).show();
    }

    public boolean saveIpToFile(View v){

        ipAdress = (EditText) findViewById(R.id.ip_adress);

        TextView vw = (TextView) findViewById(R.id.hello);

        try{
            if("".equals(ipAdress.getText().toString())) {
                throw new Throwable("Введіть адресу.");
                //Toast.makeText(this,"Введіть адресу.", Toast.LENGTH_SHORT).show();
            }

            SharedPreferences myPrefs = getSharedPreferences("myPrefs-ip", Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();

            prefsEditor.putString("ip", ipAdress.getText().toString());

            prefsEditor.commit();

            Toast.makeText(this,"Адреса збережена у файл.", Toast.LENGTH_LONG).show();

            //redirect to main
            Intent i = new Intent(ServerSettings.this, MainActivity.class);
            startActivity(i);

            overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
            finish();
            overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);

            return true;
        } catch(Throwable t) {
            Toast.makeText(this, "Exc: " + t.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
