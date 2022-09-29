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

public class CustomDialog extends Dialog {

    private EditText editText;
    private Button save;
    private Button back;

    private RadioGroup radioGroup;
    private RadioButton radio1;
    private RadioButton radio2;
    private RadioButton radio3;
    private RadioButton radio4;

    private int numberradio;

    private final FirebaseDatabase database;
    private final DatabaseReference Refjam;
    private final DatabaseReference Refmenit;

    public CustomDialog(Context context,Integer jadwal) {
        super(context, jadwal);
        setCancelable(false);
        database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");
        String pathJam = String.format("Jam/%s",jadwal);
        String pathMenit = String.format("Menit/%s",jadwal);

        Log.i(TAG,pathJam);
        Log.i(TAG, pathMenit);

        Refjam = database.getReference(pathJam);
        Refmenit = database.getReference(pathMenit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_custom_dialog);
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
                if (radio1.isChecked()){ numberradio = 0;}
                else if (radio2.isChecked()){ numberradio = 15;}
                else if (radio3.isChecked()){ numberradio = 30;}
                else if (radio4.isChecked()){ numberradio = 45;}
                else{ Toast.makeText(getContext(),"Error Radio Group", Toast.LENGTH_SHORT).show();}
            }
        });

        save = findViewById(R.id.button_save);
        save.setOnClickListener(view -> {
            String inputjam = editText.getText().toString();
            if (inputjam.isEmpty()){
                Toast.makeText(getContext(),"Please Input Jam",Toast.LENGTH_SHORT).show();
                return;
            }

            Double jam = Double.parseDouble(inputjam);

            if (jam > 23){
                Toast.makeText(getContext(),"Jam Harus diatara 0 dan 23",Toast.LENGTH_SHORT).show();
                return;
            }

            Refmenit.setValue(numberradio);
            Refjam.setValue(jam);
            Toast.makeText(getContext(),"New Schedule Saved",Toast.LENGTH_SHORT).show();
            dismiss();
        });

        back = findViewById(R.id.button_back);
        back.setOnClickListener(view -> dismiss());
    }

}