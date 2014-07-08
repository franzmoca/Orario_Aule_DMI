package com.dif.orarioauledmi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class QrScan extends Activity {
    public final static String QRCODE = "com.dif.orarioauledmi.qrcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
    }
     public void onActivityResult(int requestCode, int resultCode, Intent intent) {
           IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
           if (scanResult != null) {
                 // handle scan result
               Log.d("QrScan",""+scanResult);
               Intent qrcode = new Intent(QrScan.this,Orario.class);
               qrcode.putExtra(QRCODE,scanResult.getContents());
               QrScan.this.startActivity(qrcode);
               }
           // else continue with any other code you need in the method

         }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qr_scan, menu);
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


    public void leggiQR(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("SAVE_HISTORY", false);
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }
}
