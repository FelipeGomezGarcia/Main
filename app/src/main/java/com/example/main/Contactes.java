package com.example.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Contactes extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactes);
        Button crea = (Button) findViewById(R.id.bCrear2);
        crea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText asunte = (EditText) findViewById(R.id.etNom2);
        EditText text = (EditText) findViewById(R.id.etPhone2);

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, asunte.getText().toString())
                .putExtra(ContactsContract.Intents.Insert.PHONE, text.getText())
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        startActivity(intent);
    }
}