package com.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bAlarm = (Button) findViewById(R.id.alarm);
        Button bNotes = (Button) findViewById(R.id.bNotes);
        Button bCamara = (Button) findViewById(R.id.bCamara);
        bAlarm.setOnClickListener(this);
        bNotes.setOnClickListener(this);
        bCamara.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        if (v == findViewById(R.id.alarm)) {
            intent = new Intent (this, Alarma.class);
        } else if (v == findViewById(R.id.bNotes)) {
            intent = new Intent (this, Contactes.class);
        } else if (v == findViewById(R.id.bCamara)){
            intent = new Intent (this,camara.class);
        }else {
            intent = null;
        }
        startActivity(intent);
    }
}