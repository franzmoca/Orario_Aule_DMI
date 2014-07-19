package com.dif.orarioauledmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Calendario extends Activity {
    public final static String QRCODE = "com.dif.orarioauledmi.qrcode";
    public final static String GIORNO = "com.dif.orarioauledmi.giorno";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Forza la portrait mode

        JSONArray aule=getJSONFromHttpPost("http://esameingsoft.altervista.org/php/android/echo_aule.php");


        List<String> aule_array = new ArrayList<String>();
        try {
            for(int i = 0; i<aule.length();i++){
                aule_array.add(aule.getJSONObject(i).getString("aula"));
            }
        }catch (NullPointerException a){

            AlertDialog.Builder builder = new AlertDialog.Builder(Calendario.this);

            // 2. Chain together various setter methods to set the dialog characteristics

                    builder.setMessage("Connessione a internet assente")
                            .setTitle("Errore!");

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
            dialog.show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_aula);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,aule_array);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_giorno);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.giorni, R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendario, menu);
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

    public void apriOrario(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_aula);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_giorno);
        String aula = ""+spinner.getSelectedItem().toString();
        String giorno = ""+spinner2.getSelectedItem().toString();
        Intent qrcode = new Intent(Calendario.this,Orario.class);
        qrcode.putExtra(QRCODE,aula);
        int d;
        switch (giorno){
            case "Lunedì":
                d=1;
                break;
            case "Martedì":
                d=2;
                break;
            case "Mercoledì":
                d=3;
                break;
            case "Giovedì":
                d=4;
                break;
            case "Venerdì":
                d=5;
                break;
            default:
                d=10;
                break;
        }
        qrcode.putExtra(GIORNO,""+d);

        Calendario.this.startActivity(qrcode);

    }


    public JSONArray getJSONFromHttpPost(String URL) {
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
               // if(resultString.contains("QrCode errato")){
                    //qrErrato(0);
                   // return null;
                //}
                // Transform the String into a JSONObject
                JSONArray jsonObjRecv = new JSONArray(resultString);
                // Raw DEBUG output of our received JSON object:
                System.out.println("<JSONARRAY>\n" + jsonObjRecv.toString() + "\n</JSONARRAY>");


                return jsonObjRecv;
            }
        }catch (UnknownHostException e){
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
}
