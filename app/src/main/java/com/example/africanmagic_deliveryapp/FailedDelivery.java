package com.example.africanmagic_deliveryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.example.africanmagic_deliveryapp.MainActivity.SelectedSaleId;

public class FailedDelivery extends AppCompatActivity {
        public static final String SelectedNameF = "com.example.africanmagic.deliveryapp.SelectedName";
        public static final String SelectedLNameF = "com.example.africanmagic.deliveryapp.SelectedLName";
        public static final String SelectedAddressF = "com.example.africanmagic.deliveryapp.SelectedAddress";
        public static final String SelectedPhoneNoF = "com.example.africanmagic.deliveryapp.SelectedPhoneNo";
        public static final String SelectedDeliveryIdF = "com.example.africanmagic.deliveryapp.SelectedLongitude";
        public static final String SelectedSaleIdF = "com.example.africanmagic.deliveryapp.SelectedLongitude";

        private ArrayList<ClassListDeliveries> itemArrayList;  //List items Array
        private com.example.africanmagic_deliveryapp.FailedDelivery.MyAppAdapter myAppAdapter; //Array Adapter
        private ListView listView; // Listview
        private boolean success = false; // boolean
        public ConnectionClass connectionClass; //Connection Class Variable

        public String firstName ="";
        public String lastName ="";
        public String AddressG ="";
        public String phoneNumber ="";
        public String deliveryId ="";
        public String saleId="";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_failed_delivery);

            listView = (ListView) findViewById(R.id.listView); //Listview Declaration
            connectionClass = new ConnectionClass(); // Connection Class Initialization
            itemArrayList = new ArrayList<ClassListDeliveries>(); // Arraylist Initialization

            listView.setAdapter(myAppAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String fname = myAppAdapter.deliveriesList.get(i).getfName();
                    firstName = fname;
                    String lname = myAppAdapter.deliveriesList.get(i).getlName();
                    lastName = lname;
                    String address = myAppAdapter.deliveriesList.get(i).getAddress();
                    AddressG = address;
                    String phone = myAppAdapter.deliveriesList.get(i).getPhoneNo();
                    phoneNumber = phone;
                    Integer delIDGet = myAppAdapter.deliveriesList.get(i).getDelId();
                    deliveryId = (""+ delIDGet);
                    Integer saleIDGet = myAppAdapter.deliveriesList.get(i).getSaleId();
                    saleId = (""+ saleIDGet);
                    AlertDialog.Builder reDeliver = new AlertDialog.Builder(FailedDelivery.this);
                    reDeliver.setMessage("Attempt Redelivery").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Toast.makeText(com.example.africanmagic_deliveryapp.FailedDelivery.this, "Deliver to " + firstName + " " + lastName, Toast.LENGTH_LONG).show();
                                    openDetails();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(FailedDelivery.this, "Cancelled Redelivery Attempt.", Toast.LENGTH_LONG).show();
                                }
                            });
                    AlertDialog alertDialog = reDeliver.create();
                    alertDialog.setTitle("Redeliver");
                    alertDialog.show();
                }
            });


            Button pend = (Button) findViewById(R.id.pendFromFail);
            pend.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FailedDelivery.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }));

            // Calling Async Task
            com.example.africanmagic_deliveryapp.FailedDelivery.SyncData orderData = new com.example.africanmagic_deliveryapp.FailedDelivery.SyncData();
            orderData.execute("");
        }


        public void openDetails(){
            Intent intent = new Intent(this, DeliveryDetails.class);
            intent.putExtra(MainActivity.SelectedName, firstName);
            intent.putExtra(MainActivity.SelectedLName, lastName);
            intent.putExtra(MainActivity.SelectedAddress, AddressG);
            intent.putExtra(MainActivity.SelectedSaleId, saleId);
            intent.putExtra(MainActivity.SelectedPhoneNo, phoneNumber);
            intent.putExtra(MainActivity.SelectedDeliveryId, deliveryId);
            startActivity(intent);
        }

        // Async Task has three overrided methods,
        private class SyncData extends AsyncTask<String, String, String>
        {
            String msg = "Check Credentials!";
            ProgressDialog progress;

            @Override
            protected void onPreExecute() //Starts the progress dialog
            {
                progress = ProgressDialog.show(FailedDelivery.this, "Synchronising",
                        "Failed Deliveries Loading! Please Wait...", true);
            }

            @Override
            protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
            {
                try
                {
                    Connection conn = connectionClass.CONN(); //Connection Object
                    if (conn == null)
                    {

                        success = false;
                    }
                    else {
                        // Change below query according to your own database.
                        String query = "SELECT dbo.Sales.SaleDate, dbo.Sales.FirstName, dbo.Sales.LastName, dbo.Deliveries.DeliveryId, dbo.Sales.PhoneNumber, dbo.Sales.Address, dbo.Deliveries.OrderStatus, dbo.Sales.SaleId FROM dbo.Sales INNER JOIN dbo.Deliveries ON dbo.Sales.SaleId = dbo.Deliveries.SaleId WHERE OrderStatus = 'Unsuccessful';";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                        {
                            while (rs.next())
                            {
                                try {
                                    itemArrayList.add(new ClassListDeliveries(rs.getString("FirstName"),rs.getString("LastName"),rs.getDate("SaleDate"),rs.getInt("DeliveryId"),rs.getString("PhoneNumber"),rs.getString("Address"),rs.getString("OrderStatus"),rs.getInt("SaleId")));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            msg = "Fetched Deliveries From Server.";
                            success = true;
                        } else {
                            msg = "No Data found!";
                            success = false;
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Writer writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    msg = writer.toString();
                    success = false;
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
            {
                progress.dismiss();
                Toast.makeText(com.example.africanmagic_deliveryapp.FailedDelivery.this, msg + "", Toast.LENGTH_LONG).show();
                if (success == false)
                {
                }
                else {
                    try {
                        myAppAdapter = new com.example.africanmagic_deliveryapp.FailedDelivery.MyAppAdapter(itemArrayList, com.example.africanmagic_deliveryapp.FailedDelivery.this);
                        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        listView.setAdapter((ListAdapter) myAppAdapter);
                    } catch (Exception ex)
                    {

                    }

                }
            }
        }

        public class MyAppAdapter extends BaseAdapter //has a class viewholder which holds
        {
            public class ViewHolder
            {
                TextView textName;

            }

            public List<ClassListDeliveries> deliveriesList;

            public Context context;
            ArrayList<ClassListDeliveries> arraylist;

            private MyAppAdapter(ArrayList<ClassListDeliveries> apps, com.example.africanmagic_deliveryapp.FailedDelivery context)
            {
                this.deliveriesList = apps;
                this.context = context;
                arraylist = new ArrayList<ClassListDeliveries>();
                arraylist.addAll(deliveriesList);
            }

            @Override
            public int getCount() {
                return deliveriesList.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
            {

                View rowView = convertView;
                com.example.africanmagic_deliveryapp.FailedDelivery.MyAppAdapter.ViewHolder viewHolder= null;
                if (rowView == null)
                {
                    LayoutInflater inflater = getLayoutInflater();
                    rowView = inflater.inflate(R.layout.list_content, parent, false);
                    viewHolder = new com.example.africanmagic_deliveryapp.FailedDelivery.MyAppAdapter.ViewHolder();
                    viewHolder.textName = (TextView) rowView.findViewById(R.id.textName);
                    rowView.setTag(viewHolder);
                }
                else
                {
                    viewHolder = (com.example.africanmagic_deliveryapp.FailedDelivery.MyAppAdapter.ViewHolder) convertView.getTag();
                }
                // here setting up names and images
                viewHolder.textName.setText("Delivery ID #: " + deliveriesList.get(position).getDelId() + ", For " + deliveriesList.get(position).getfName() + " " + deliveriesList.get(position).getlName() + ". Placed On " + deliveriesList.get(position).getSaleDate());


                return rowView;
            }
        }

    }
