package com.example.feederproject.ui.water;

import static java.lang.Integer.parseInt;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.feederproject.R;
import com.example.feederproject.databinding.FragmentWaterBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaterFragment extends Fragment {
    private FragmentWaterBinding binding;
    private ImageView imageView;
    private TextView nilai;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWaterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        nilai = binding.nilai;
        imageView = binding.imgData;

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("waterlvl");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double data = snapshot.getValue(Double.class);
                int valueInt = data.intValue();
                nilai.setText(valueInt + " %");
                gantiBackground(valueInt);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void gantiBackground(int percent){
        if (percent == 0){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i0));
        } else if (percent <= 10 && percent != 0){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i10));
        }else if (percent > 10 && percent <= 30){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i20));
        }else if (percent > 30 && percent <= 50){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i40));
        }else if (percent > 50 && percent <= 70){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i60));
        }else if (percent > 70 && percent <= 90){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i80));
        }else if (percent > 90 && percent <= 100){
            imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.i100));
        }else {
            Log.d("TAG","NGACO");
        }
    }
}