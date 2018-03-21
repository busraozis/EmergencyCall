package com.example.bra.emergentcall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class EmergentCall extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    protected LocationManager locationManager;

    private GoogleApiClient client;
    private final int PICK_CONTACT = 1; //birden çok temas adresi seçimi için listeleştirilebilir.

    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifyEmergency();
        setContentView(R.layout.activity_emergent_call);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergent_call, menu);
        return true;
    }


    public void notifyEmergency(){

        GPSTracker tracker = new GPSTracker(this);
        double longitude = tracker.getLongitude();
        double latitude = tracker.getLatitude();

        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(0, "+905339677890");

        //String smsBody = "Ben Büşra. Acil durum mesajıdır. Yardımına ihtiyacım var. Konum bilgim şöyle: http://maps.google.com/?q=" + latitude + "," + longitude;
        String smsBody = "Konumum:  http://maps.google.com/?q=" + latitude + "," + longitude;

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumbers.get(0)));


        //sms otomatik atılacak, benim göndermemi beklemeyecek.
        sendSMS(phoneNumbers.get(0), smsBody);

        /*Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("sms:" + phoneNumbers.get(0)));
        smsIntent.putExtra("sms_body", smsBody);
        startActivity(smsIntent);*/

        if (ActivityCompat.checkSelfPermission(EmergentCall.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        System.out.println("It will call " + phoneNumbers.get(0));
        //startActivity(callIntent);
        System.out.println("It must have called!");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callContacts(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        FileOutputStream outputStream = null;
        String path = getFilesDir().getAbsolutePath();
        try {
            outputStream = new FileOutputStream(new File(path+"/contacts"),true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        if (reqCode == PICK_CONTACT) {
            if (resultCode == ActionBarActivity.RESULT_OK) {
                Uri contactData = data.getData();
                //String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                     //   ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor c = getContentResolver().query(contactData,null,null,null,null);


                while(c.moveToNext()){
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup.NORMALIZED_NUMBER));
                    phone += phone+"\n";
                    Toast.makeText(this, "Seçtiğiniz kişi: "+ name, Toast.LENGTH_LONG).show();
                    try {
                        outputStream.write(phone.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void listContacts(View v) throws FileNotFoundException {
        String path = getFilesDir().getAbsolutePath();
        File file = new File(path+"/contacts");
        Scanner scan = new Scanner(file);

        while(scan.hasNextLine()){
            Toast.makeText(this, scan.nextLine(), Toast.LENGTH_LONG).show();
        }
    }
}
