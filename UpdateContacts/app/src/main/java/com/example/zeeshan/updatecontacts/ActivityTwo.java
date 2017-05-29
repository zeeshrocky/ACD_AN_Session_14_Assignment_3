package com.example.zeeshan.updatecontacts;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityTwo extends AppCompatActivity {
    EditText ev_diag_contactNumber;


    String edit_contactName,edit_contactNumber;
    Adapter myAdapter;
    int currentposition;

    //ListView
    ListView lstNames;
    ArrayList<String> al_contactName,al_contactNumber,al_contactId;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_UPDATE_CONTACTS = 100;
    ContentResolver contentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        contentResolver = getContentResolver();

        al_contactName = new ArrayList<>();
        al_contactNumber = new ArrayList<>();
        al_contactId = new ArrayList<>();
        showContacts();
        myAdapter = new Adapter(getApplicationContext(),al_contactName,al_contactNumber);
        lstNames = (ListView) findViewById(R.id.lstNames);
        lstNames.setAdapter(myAdapter);
        registerForContextMenu(lstNames);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.setHeaderTitle("Select The Action");
        Log.e("SecondACTIVITY","ContextMenu is inflated");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        currentposition = info.position;
        Log.e("Menu Info Current Pos :", String.valueOf(currentposition));
        int selecteditemid = item.getItemId();

        switch (selecteditemid) {
            case R.id.subOptionUpdate:
                Toast.makeText(ActivityTwo.this, "Update Selected for id: " +selecteditemid, Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "Update Option Selected");
                updateItem();
                myAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public static void update(ContentResolver contactHelper, String number, String newNumber) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String contactId = String.valueOf(getContactID(contactHelper, number));
        //start

        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Contacts.Data.MIMETYPE + "='"  +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
        String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = { ContactsContract.PhoneLookup._ID };
        Cursor cursor = null;

        try {
            cursor = contactHelper.query(contactUri, projection, null, null,null);

            if (cursor != null && cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();

            }
        }
        return -1;
    }



    public void updateItem(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityTwo.this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertDialog.setTitle("Update "+al_contactName.get(currentposition)+" ?");
        alertDialog.setView(inflater.inflate(R.layout.dialog_update, null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog diag = (Dialog) dialog;
                        Log.e("Contact name is", al_contactName.get(currentposition));
                        ev_diag_contactNumber = (EditText) diag.findViewById(R.id.diag_update_contact_phone_number);
                        edit_contactNumber = ev_diag_contactNumber.getText().toString();
                        Log.e("updateItem ",edit_contactNumber);
                        editContacts();
                        refresh();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }



    private void refresh() {
        Intent intent = new Intent(ActivityTwo.this, ActivityTwo.class);
        ActivityTwo.this.startActivity(intent);
        finish();
    }

    private void displayAllContacts(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            al_contactName.add(name);
                            al_contactNumber.add(phoneNo);
                            al_contactId.add(id);
                            Log.e("DAC : ", name + ", " + phoneNo + ", " + id);
                        }
                    }
                }

            }
        }
    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        // Android version is lesser than 6.0 or the permission is already granted.
        displayAllContacts();


    }

    private void editContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_UPDATE_CONTACTS);
        }
        // Android version is lesser than 6.0 or the permission is already granted.
        Log.e("EC : ",al_contactNumber.get(currentposition)+", "+edit_contactName+", "+edit_contactNumber);
        updateContact(al_contactNumber.get(currentposition),edit_contactNumber);
    }
    private void updateContact(String number, String contactNUMBER) {
        update(contentResolver,number,contactNUMBER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Permission required to show contact name", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_UPDATE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                updateContact(al_contactNumber.get(currentposition), edit_contactNumber);
            } else {
                Toast.makeText(this, "Permission required to show contact name", Toast.LENGTH_SHORT).show();
            }
        }
    }

}