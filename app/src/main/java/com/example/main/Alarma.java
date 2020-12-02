package com.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Alarma extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        Button establecer = (Button) findViewById(R.id.bEstablecer);
        establecer.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        EditText nom = (EditText) findViewById(R.id.alarmNom);
        EditText reloj = (EditText) findViewById(R.id.hora);
        EditText minuto = (EditText) findViewById(R.id.minut);
        Intent intent;

        intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, nom.getText().toString())
                .putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(reloj.getText().toString()))
                .putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minuto.getText().toString()));
        startActivity(intent);

    }
}