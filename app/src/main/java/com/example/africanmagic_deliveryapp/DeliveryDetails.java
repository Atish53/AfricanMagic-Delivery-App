package com.example.africanmagic_deliveryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.StrictMode;
import android.telephony.SmsManager;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.exception.RestException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DeliveryDetails extends AppCompatActivity {
    private static final String TAG = "";
    public ConnectionClass connectionClass; //Connection Class Variable

    String SaleId= "";
    String Address = "";
    String DeliveryId = "";

    public static final String ACCOUNT_SID = "AC8c441e42c9f60515fbe2c1e9826b6663";
    public static final String AUTH_TOKEN = "88f70cb401107e2c3fb1036acac175b1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        connectionClass = new ConnectionClass(); // Connection Class Initialization

        final Intent intent = getIntent();
        final String selectedName = intent.getStringExtra(MainActivity.SelectedName);
        final String selectedLName = intent.getStringExtra(MainActivity.SelectedLName);
        String selectedAdd = intent.getStringExtra(MainActivity.SelectedAddress);
        final String selectedPhone = intent.getStringExtra(MainActivity.SelectedPhoneNo);
        final String selectedDeliveryId = intent.getStringExtra(MainActivity.SelectedDeliveryId);
        final String selectedSaleId = intent.getStringExtra(MainActivity.SelectedSaleId);

        SaleId = selectedSaleId;

        Address = selectedAdd;
        DeliveryId = selectedDeliveryId;

        TextView fNameView = (TextView) findViewById(R.id.fname);
        TextView deliveryID = (TextView) findViewById(R.id.delID);
        TextView addressView = (TextView) findViewById(R.id.address);
        fNameView.setText(selectedName + " " + selectedLName);
        deliveryID.setText("Delivery ID #" + selectedDeliveryId);
        addressView.setText(selectedAdd);

        Button call = (Button) findViewById(R.id.caller);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + selectedPhone));
                startActivity(intentCall);
            }
        });

        Button success = (Button) findViewById(R.id.successUpdate);
        success.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder successSQL = new AlertDialog.Builder(DeliveryDetails.this);
                successSQL.setMessage("Confirm Successful Delivery").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                //SQL Update Occurs Here............
                                try {
                                    Connection conn = connectionClass.CONN(); //Connection Object
                                    if (conn == null) {
                                        Toast.makeText(DeliveryDetails.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Change below query according to your own database.

                                        String query = "UPDATE dbo.Deliveries SET OrderStatus = 'Completed' WHERE DeliveryId =" + DeliveryId + ";";
                                        Statement stmt = conn.createStatement();
                                        stmt.executeQuery(query);
                                    }
                                } catch (Exception e) {
                                    //Error
                                    Toast.makeText(DeliveryDetails.this, "Successful Delivery Confirmed", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(DeliveryDetails.this, "Successful Delivery Confirmed", Toast.LENGTH_LONG).show();
                                sendSms("+27" + selectedPhone.substring(1,10) , "Dear " + selectedName + " . Your Order Number #" + selectedSaleId + " has been successfully delivered. Thank you for shopping with AfricanMagic." );
                                Intent refresh = new Intent(DeliveryDetails.this, MainActivity.class);
                                startActivity(refresh);//Start the same Activity
                                finish(); //finish Activity.
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DeliveryDetails.this, "Cancelled Delivery Update", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = successSQL.create();
                alertDialog.setTitle("Delivery Status");
                alertDialog.show();
            }
        });

        Button gmsTrack = (Button) findViewById(R.id.gpsRedirect); ///////////////Google Maps Attempt
        gmsTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });

        Button unsuccess = (Button) findViewById(R.id.unsuccessUpdate);
        unsuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder successSQL = new AlertDialog.Builder(DeliveryDetails.this);
                successSQL.setMessage("Confirm Unsuccessful Delivery").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //SQL Update Occurs Here............
                                try {
                                    Connection conn = connectionClass.CONN(); //Connection Object
                                    if (conn == null) {
                                        Toast.makeText(DeliveryDetails.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Change below query according to your own database.
                                        String query = "UPDATE dbo.Deliveries SET OrderStatus = 'Unsuccessful' WHERE DeliveryId =" + DeliveryId + ";";
                                        Statement stmt = conn.createStatement();
                                        stmt.executeQuery(query);
                                       }
                                } catch (Exception e) {
                                    //Error
                                    Toast.makeText(DeliveryDetails.this, "Failed to update status.", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(DeliveryDetails.this, "Unsuccessful Delivery Confirmed", Toast.LENGTH_LONG).show();
                                sendSms("+27" + selectedPhone.substring(1,10),"Dear " + selectedName + " . Your Order Number #" + selectedSaleId + " could not be delivered successfully. You will be notified of a re-delivery soon.");
                                Intent refresh = new Intent(DeliveryDetails.this, MainActivity.class);
                                startActivity(refresh);//Start the same Activity
                                finish(); //finish Activity.

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Toast.makeText(DeliveryDetails.this, "Cancelled Delivery Update", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = successSQL.create();
                alertDialog.setTitle("Delivery Status");
                alertDialog.show();
            }
        });


    }

    private void sendSms(String toPhoneNumber, String message){
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/SMS/Messages";
        String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("From", "+19382533870")
                .add("To", toPhoneNumber)
                .add("Body", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, "sendSms: "+ response.body().string());
        } catch (IOException e) { e.printStackTrace(); }

    }

    private void openMaps() {
                Uri navigationIntentUri = Uri.parse("google.navigation:q=" + Address);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

    }