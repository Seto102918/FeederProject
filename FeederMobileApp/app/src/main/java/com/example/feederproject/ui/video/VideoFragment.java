package com.example.feederproject.ui.video;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.feederproject.ForegroundService;
import com.example.feederproject.LoginActivity;
import com.example.feederproject.databinding.FragmentVideoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longdo.mjpegviewer.MjpegView;

public class VideoFragment extends Fragment {

    private FragmentVideoBinding binding;
    private MjpegView viewer = null;
    private FirebaseDatabase database;
    private DatabaseReference RefBuzzer;
    private int a, state;
    private ImageButton imageButton;
    private Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler();



        binding = FragmentVideoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");
        RefBuzzer = database.getReference("Buzzer");

        viewer = binding.mjpegid;
        viewer.setAdjustHeight(true);
        viewer.setMode(MjpegView.MODE_BEST_FIT);
        viewer.setUrl("http://192.168.68.136:81/stream");
        viewer.setRecycleBitmap(true);

        Button start = binding.start;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Log.d("Video","Try Start Stream");
                    viewer.startStream();
                } catch (Exception e){
                    Toast.makeText(getActivity(),
                            "Failed to Start Stream",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button stop = binding.stop;
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{ viewer.stopStream(); }
                catch (Exception e){
                    Toast.makeText(getActivity(),
                            "Failed to Stop Stream",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButton = binding.imageButton;

        state = firebaseBuzzer();
        stateButton(state);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == 1){
                    RefBuzzer.setValue(0);
                    state = 0;
                    stateButton(state);
                } else if (state == 0){
                    RefBuzzer.setValue(1);
                    state = 1;
                    stateButton(state);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RefBuzzer.setValue(0);
                            state = 0;
                            stateButton(state);
                        }
                    }, 8000);
                }
            }
        });

        return root;
    }

    public int firebaseBuzzer() {
        RefBuzzer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                assert value != null;
                a = value.intValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return a;
    }

    public void stateButton(int state){
        if (state == 1){  imageButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,137,59))); }
        else if(state == 0){    imageButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(70,84,97))); }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        try{ viewer.stopStream(); }
        catch (Exception e){
            Toast.makeText(getActivity(),
                    "Failed to Stop Stream",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try{ viewer.startStream(); }
        catch (Exception e){
            Toast.makeText(getActivity(),
                    "Failed to Start Stream",
                    Toast.LENGTH_SHORT).show();
        }
    }
}