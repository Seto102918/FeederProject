package com.example.feederproject;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomDialog2 extends Dialog {

    private EditText editText;
    private Button save;
    private Button back;

    private RadioGroup radioGroup;
    private RadioButton radio1;
    private RadioButton radio2;
    private RadioButton radio3;
    private RadioButton radio4;

    private int numberradio;

    private FirebaseDatabase database;
    private DatabaseReference Refrefill;

    public CustomDialog2(Context context) {
        super(context);
        setCancelable(false);
        database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");
        String pathMenit = String.format("Refill");
        Log.i(TAG, pathMenit);
        Refrefill = database.getReference(pathMenit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_custom_dialog2);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        editText = findViewById(R.id.edit_Jam);

        radioGroup = findViewById(R.id.radio_group);
        radio1 = findViewById(R.id.radioButton1);
        radio2 = findViewById(R.id.radioButton2);
        radio3 = findViewById(R.id.radioButton3);
        radio4 = findViewById(R.id.radioButton4);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio1.isChecked()){ numberradio = 50;}
                else if (radio2.isChecked()){ numberradio = 100;}
                else if (radio3.isChecked()){ numberradio = 200;}
                else if (radio4.isChecked()){ numberradio = 300;}
                else{ Toast.makeText(getContext(),"Error Radio Group", Toast.LENGTH_SHORT).show();}
            }
        });

        save = findViewById(R.id.button_save);
        save.setOnClickListener(view -> {
            if (numberradio == 0){ numberradio = 50;}
            Refrefill.setValue(numberradio);
            Toast.makeText(getContext(),"Jumlah Refill Berhasil Diubah",Toast.LENGTH_SHORT).show();
            dismiss();
        });

        back = findViewById(R.id.button_back);
        back.setOnClickListener(view -> dismiss());


    }

}