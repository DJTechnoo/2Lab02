package com.example.askel.rssfeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static com.example.askel.rssfeed.Constants.*;

public class PrefActivity extends AppCompatActivity {
    private EditText editLink;
    private Button commit;
    private Spinner amountSpinner;
    private Spinner timerSpinner;
    private String link;
    private int amount;
    private int timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        editLink = (EditText) findViewById(R.id.pref_link);
        commit = (Button) findViewById(R.id.button_commit);


        //  Create Spinner for amount of items to display
        amountSpinner = (Spinner) findViewById(R.id.amountSpinner);
        ArrayAdapter<String> arAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,
                getResources().getStringArray(R.array.amount_spinner));
        arAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amountSpinner.setAdapter(arAdapter);

        //  Create Spinner for frequency of updates to be set
        timerSpinner = (Spinner) findViewById(R.id.timerSpinner);
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_expandable_list_item_1,
                            getResources().getStringArray(R.array.timer_spinner));
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timerSpinner.setAdapter(freqAdapter);

        //  finally listen to both spinners
        timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (i) {
                    case 0: timer = FR_MINUTE; break;
                    case 1: timer = FR_TEN_MINUTES; break;
                    case 2: timer = FR_DAY; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }


        });

        //  Finally listen to both spinners
        amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (i) {
                    case 0:
                        amount = 10;
                        break;
                    case 1:
                        amount = 20;
                        break;
                    case 2:
                        amount = 50;
                        break;
                    case 3: amount = 100;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }


        });


        //  Receive the current link
        Intent intent = getIntent();
        link = intent.getStringExtra("CURRENT_LINK");
        editLink.setText(link);

    }

    public void commitChanges(View view) {
        String text = editLink.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("NEW_LINK", text);
        intent.putExtra("NEW_AMOUNT", amount);
        intent.putExtra("NEW_FREQUENCY", timer);
        setResult(RESULT_OK, intent);
        finish();

    }
}
