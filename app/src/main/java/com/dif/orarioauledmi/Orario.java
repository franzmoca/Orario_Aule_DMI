package com.dif.orarioauledmi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dif.orarioauledmi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.dif.orarioauledmi.Parser.getJSONFromHttpPost;

public class Orario extends Activity {
    TextView aula;
    int idlezione[] = {R.id.lezione1, R.id.lezione2, R.id.lezione3, R.id.lezione4, R.id.lezione5, R.id.lezione6, R.id.lezione7, R.id.lezione8, R.id.lezione9, R.id.lezione10, R.id.lezione11};
    int idora[] = {R.id.ora1, R.id.ora2, R.id.ora3, R.id.ora4, R.id.ora5, R.id.ora6, R.id.ora7, R.id.ora8, R.id.ora9, R.id.ora10, R.id.ora11};
    int idprof[] = {R.id.prof1, R.id.prof2, R.id.prof3, R.id.prof4, R.id.prof5, R.id.prof6, R.id.prof7, R.id.prof8, R.id.prof9, R.id.prof10, R.id.prof11};
    int idRelative[] = {R.id.primo,R.id.secondo,R.id.terzo,R.id.quarto,R.id.quinto,R.id.sesto,R.id.settimo,R.id.ottavo,R.id.nono,R.id.decimo,R.id.undicesimo};
    String orari[] = {"9-10","10-11","11-12","12-13","14-15","15-16","16-17","17-18"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Forza la portrait mode
        setContentView(R.layout.activity_orario);
        Calendar rightNow = Calendar.getInstance();
        int f = rightNow.get(Calendar.HOUR_OF_DAY) - 10;
        aula = (TextView) findViewById(R.id.aula);
        Intent intent = getIntent();
        String d =intent.getStringExtra(QrScan.QRCODE);

        JSONObject json = getJSONFromHttpPost(d);
        try {
            int a = 0;
            aula.setText(""+json.get("aula"));
            for(int i = f; i<8;i++){
                TextView orario =(TextView) findViewById(idora[a]);
                orario.setText(""+orari[i]);
                TextView lezione = (TextView) findViewById(idlezione[a]);
                lezione.setText(""+json.get(orari[i]));
                a++;
            }
            for(int b = a; b < 11; b++){
                RelativeLayout riga = (RelativeLayout) findViewById(idRelative[b]);
                riga.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
