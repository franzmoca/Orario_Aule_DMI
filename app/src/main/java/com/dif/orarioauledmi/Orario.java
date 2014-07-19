package com.dif.orarioauledmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Calendar;


public class Orario extends Activity {
    TextView aula;
    int idlezione[] = {R.id.lezione1, R.id.lezione2, R.id.lezione3, R.id.lezione4, R.id.lezione5, R.id.lezione6, R.id.lezione7, R.id.lezione8, R.id.lezione9, R.id.lezione10, R.id.lezione11};
    int idora[] = {R.id.ora1, R.id.ora2, R.id.ora3, R.id.ora4, R.id.ora5, R.id.ora6, R.id.ora7, R.id.ora8, R.id.ora9, R.id.ora10, R.id.ora11};
    //int idprof[] = {R.id.prof1, R.id.prof2, R.id.prof3, R.id.prof4, R.id.prof5, R.id.prof6, R.id.prof7, R.id.prof8, R.id.prof9, R.id.prof10, R.id.prof11};
    int idRelative[] = {R.id.primo,R.id.secondo,R.id.terzo,R.id.quarto,R.id.quinto,R.id.sesto,R.id.settimo,R.id.ottavo,R.id.nono,R.id.decimo,R.id.undicesimo};
    String orari[] = {"9-10","10-11","11-12","12-13","14-15","15-16","16-17","17-18"};
    public final static String GIORNO = "com.dif.orarioauledmi.giorno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Forza la portrait mode
        setContentView(R.layout.activity_orario);
        Calendar rightNow = Calendar.getInstance();
        int iora = rightNow.get(Calendar.HOUR_OF_DAY);
        Log.d("ioraprima: ",""+iora);

        //Orari
        if(iora > 8 && iora < 19) {
            if(iora>13){
                iora-=10;
            }else {
                iora -= 9;
            }
        }else{
            iora=0;
        }
        Log.d("ioradopo: ",""+iora);
        aula = (TextView) findViewById(R.id.aula);
        Intent intent = getIntent();
        String query = intent.getStringExtra(QrScan.QRCODE);
        String giorno = intent.getStringExtra(GIORNO);

        String d ="http://esameingsoft.altervista.org/php/android/echo_json.php?q="+ query.replaceAll(" ","%20")+"&d="+giorno;
        Log.d("d",""+d);

        JSONObject json = getJSONFromHttpPost(d);
        try {
            String nomeaula= ""+json.get("aula");
            int a = 0;
            aula.setText(nomeaula);
            RelativeLayout sfondo= (RelativeLayout)findViewById(R.id.sfondo);
            setViewBackgroundWithoutResettingPadding(sfondo,trueAula(nomeaula));
            for(int i = iora; i<8;i++){
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
        }catch (Exception e){
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
    public  void  qrErrato(int errore){
        //0 : Qr letto non valido
        //1: Connessione assente
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(Orario.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        switch (errore){
            case 0:
                builder.setMessage("Il QR letto non è corrispondente ad un aula")
                        .setTitle("Errore!");
                break;
            case 1:
                builder.setMessage("Connessione a internet assente")
                        .setTitle("Errore!");

        }


        builder.setPositiveButton("Riprova",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }
        );
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        aula.setText("Aula sconosciuta");
        for(int b = 0; b < 11; b++){
            RelativeLayout riga = (RelativeLayout) findViewById(idRelative[b]);
            riga.setVisibility(View.INVISIBLE);
        }
        dialog.show();
    }

    public JSONObject getJSONFromHttpPost(String URL) {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            // Create a new HttpClient and Post Header
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            String resultString = null;


            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPost);
            System.out.println("HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");
            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();

                // convert content stream to a String
                resultString = convertStreamToString(instream);
                instream.close();
                System.out.println("result String : " + resultString);
                //resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"
                if(resultString.contains("QrCode errato")){
                 qrErrato(0);
                 return null;
                }
                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                System.out.println("<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");


                return jsonObjRecv;
            }
        }catch (UnknownHostException e){
            qrErrato(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line ="";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void setViewBackgroundWithoutResettingPadding(final View v, final int backgroundResId) {
        final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
        final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
        v.setBackgroundResource(backgroundResId);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int trueAula(String aula){
        if(aula.contains("A0")){
            return R.drawable.a0;
        }
        if(aula.contains("A2")){
            return R.drawable.a2;
        }
        if(aula.contains("A3")){
            return R.drawable.ingresso;
        }
        if(aula.contains("B1")){
            return R.drawable.ingresso;
        }
        if(aula.contains("B3")){
            return R.drawable.ingresso;
        }
        if(aula.contains("C2")){
            return R.drawable.ingresso;
        }
        if(aula.contains("I1")){
            return R.drawable.ingresso;
        }
        if(aula.contains("I2")){
            return R.drawable.ingresso;
        }
        if(aula.contains("lab GIALLA")){
            return R.drawable.ingresso;
        }
        if(aula.contains("lab VERDE")){
            return R.drawable.ingresso;
        }
        return R.drawable.ingresso;
    }

    public void indietro(View view){
        finish();
    }
}
