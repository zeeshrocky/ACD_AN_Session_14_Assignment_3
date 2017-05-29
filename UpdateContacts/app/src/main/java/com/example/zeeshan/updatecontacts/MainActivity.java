package com.example.zeeshan.updatecontacts;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText_name, editText_phone;
    Button button_add;
    ContentResolver contentResolver;
    String diag_contactName, diag_contactNumber;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;
    Button buttonViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = getContentResolver();
        button_add = (Button) findViewById(R.id.button_add);
        buttonViewAll = (Button) findViewById(R.id.button_viewAll);
        button_add.setOnClickListener(this);
        buttonViewAll.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_add:
                AddContact();
                Toast.makeText(MainActivity.this,"buttonADD Clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.button_viewAll:
                Intent intent = new Intent(MainActivity.this,ActivityTwo.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"buttonViewAll Clicked",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void AddContact() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialog.setView(inflater.inflate(R.layout.add_dialog, null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog diag = (Dialog) dialog;
                        editText_name = (EditText) diag.findViewById(R.id.editText_name);
                        editText_phone = (EditText) diag.findViewById(R.id.editText_phone);
                        diag_contactName = editText_name.getText().toString();
                        diag_contactNumber = editText_phone.getText().toString();

                        Log.e("addItem ", diag_contactName);

                        writeContact();
                        Toast.makeText(getApplicationContext(), "Contact added", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Log.e("addItem ", "cancelled");
                    }
                });
        alertDialog.show();
    }
    private void writeContact() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            insertContact(diag_contactName, diag_contactNumber);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                insertContact(diag_contactName, diag_contactNumber);
            } else {
                Toast.makeText(this, "Please grant permission to display the name", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void insertContact(String contactName,String contactNumber) {
        insertctact(contentResolver,contactName,contactNumber);
    }
    public static boolean insertctact(ContentResolver contactAdder, String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,firstName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}