package com.example.bra.emergentcall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.appindexing.Thing;

/*import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing; */

public class EmergentCall extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private final int PICK_CONTACT = 1; //birden çok temas adresi seçimi için listeleştirilebilir.
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergent_call);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Location myCurrentLocation = new Location("dummy");
        myCurrentLocation.setLongitude(0.0);
        myCurrentLocation.setLatitude(0.0);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            /*String msg = " Current Location: " +
                                    Double.toString(location.getLatitude()) + ",    " +
                                    Double.toString(location.getLongitude());
                            System.out.println(msg);
                            ((TextView)findViewById(R.id.content_emergent_call)).setText(msg);*/

                            //Set my current location
                            myCurrentLocation.setLatitude(location.getLatitude());
                            myCurrentLocation.setLongitude(location.getLongitude());
                        }
                    }
                });


        //SmsManager.getDefault().sendTextMessage(phoneNumbers.get(0), null, smsBody, null,null);
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumbers.get(0)));
        //intent.putExtra("sms_body", smsBody);
        //startActivity(intent);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(0, "+905339677890");

        String smsBody = "Ben Büşra. Acil durum mesajıdır. Yardımına ihtiyacım var. Konum bilgim şöyle: http://maps.google.com/?q=" + myCurrentLocation.getLatitude() + "," + myCurrentLocation.getLongitude();


        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumbers.get(0)));


        //sms otomatik atılacak, benim göndermemi beklemeyecek. Konum bilgisi gelmiyor. Başarılı bir şekilde alınması sağlanacak.
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("sms:" + phoneNumbers.get(0)));
        smsIntent.putExtra("sms_body", smsBody);
        //startActivity(smsIntent);

        if (ActivityCompat.checkSelfPermission(EmergentCall.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        System.out.println("It will call " + phoneNumbers.get(0));
        //startActivity(callIntent);
        System.out.println("It must have called!");


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
      //  client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergent_call, menu);
        return true;
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
        try {
            outputStream = new FileOutputStream(new File("contacts"),true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ;

        if (reqCode == PICK_CONTACT) {
            if (resultCode == ActionBarActivity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData,null,null,null,null);


                while(c.moveToNext()){
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("EmergentCall Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }*/
}
