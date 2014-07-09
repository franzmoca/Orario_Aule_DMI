package com.dif.orarioauledmi;

import android.os.StrictMode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by franz on 09/07/14.
 */
public class Parser {

    public static JSONObject getJSONFromHttpPost(String URL) {
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
                System.out.println("result String : " + resultString);
                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                System.out.println("<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");


                return jsonObjRecv;
            }
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
