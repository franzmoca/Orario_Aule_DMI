package com.dif.orarioauledmi;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JSONArray aule = getJSONFromHttpPost("http://esameingsoft.altervista.org/php/android/echo_aule.php");
        List<String> aule_array = new ArrayList<String>();
        try {
            for(int i = 0; i<aule.length();i++){
                aule_array.add(aule.getJSONObject(i).getString("aula"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setContentView(R.layout.activity_calendario);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Forza la portrait mode
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
            //qrErrato(1);
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
