package com.example.feederproject.ui.home;

import static android.content.ContentValues.TAG;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.feederproject.CustomDialog;
import com.example.feederproject.CustomDialog2;
import com.example.feederproject.LoginActivity;
import com.example.feederproject.R;
import com.example.feederproject.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private FirebaseDatabase database;

    private Switch switch1, switch2, switch3;

    private String j1, j2, j3, m1, m2, m3;

    private RelativeLayout rout1, rout2, rout3;
    private TextView txtJAM1, txtJAM2, txtJAM3;
    private TextView txtMNT1, txtMNT2, txtMNT3, JumlahRefillText;
    private CardView pilihBerat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = FirebaseDatabase.getInstance("https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app");

        WebView webView = binding.webviewWeight;
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
        webView.loadUrl("https://tes2idklg.herokuapp.com/");

        WebView webView1 = binding.webviewWeight2;
        webView1.setWebViewClient(new WebViewClient());
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setDomStorageEnabled(true);
        webView1.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
        webView1.loadUrl("https://tes2idklg.herokuapp.com/yesterday");

        Log.d("Orientation", String.valueOf(getActivity().getResources().getConfiguration().orientation));

        TextView textView = binding.smallText;
        textView.setOnClickListener(view -> {

            Log.d("Orientation Bool", String.valueOf(getActivity().getResources().getConfiguration().orientation == 1));

            if (getActivity().getResources().getConfiguration().orientation == 1){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (getActivity().getResources().getConfiguration().orientation == 2){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

        });

        ImageButton button1 = binding.buttonLogout;
        button1.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        ///////////////////////////

        switch1 = binding.switch1;
        rout1 = binding.ReloutJadwal1;
        txtJAM1 = binding.txtJAM1;
        txtMNT1 = binding.txtMNT1;

        switch2= binding.switch2;
        rout2 = binding.ReloutJadwal2;
        txtJAM2 = binding.txtJAM2;
        txtMNT2 = binding.txtMNT2;

        switch3= binding.switch3;
        rout3 = binding.ReloutJadwal3;
        txtJAM3 = binding.txtJAM3;
        txtMNT3 = binding.txtMNT3;

        ///////////////////////////

        switchfirebase(1);
        waktufirebase(1);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchcheck(switch1, rout1, txtJAM1, txtMNT1);
                setswitch(switch1,1);
            }
        });
        rout1.setOnClickListener(view -> new CustomDialog(getActivity(),1).show());

        switchfirebase(2);
        waktufirebase(2);
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchcheck(switch2, rout2, txtJAM2, txtMNT2);
                setswitch(switch2,2);
            }
        });
        rout2.setOnClickListener(view -> new CustomDialog(getActivity(),2).show());

        switchfirebase(3);
        waktufirebase(3);
        switch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchcheck(switch3, rout3, txtJAM3, txtMNT3);
                setswitch(switch3,3);
            }
        });
        rout3.setOnClickListener(view -> new CustomDialog(getActivity(),3).show());

        //////////////////////////////////

        pilihBerat = binding.pilihBerat;
        pilihBerat.setOnClickListener(view -> new CustomDialog2(getActivity()).show());

        ///////////////////////////////////

        JumlahRefillText = binding.JumlahRefillText;
        DatabaseReference refRefill = database.getReference("Refill");

        refRefill.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                assert value != null;
                int brpGram = value.intValue();
                JumlahRefillText.setText(brpGram + "gr");
            }@Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        ////////////////////////////////////
        RelativeLayout ReloutWPR2 = binding.ReloutWPR2;
        TextView txtRefill2 = binding.txtRefill2;
        DatabaseReference refRefilltxt = database.getReference("RefillAlert");

        refRefilltxt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                assert value != null;
                int ambatukam = value.intValue();
                if (ambatukam == 1){
                    txtRefill2.setText("Food Dispenser is Empty");
                    ReloutWPR2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.red)));
                }else if (ambatukam == 0){
                    txtRefill2.setText("Food Dispenser is not Empty");
                }else {Log.e("ERROR", "APA NEH!");}
            }@Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void switchcheck(Switch swtch, RelativeLayout relativeLayout,TextView text, TextView text2){

        if (swtch.isChecked()){
            relativeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(115,156,162)));
            text.setTextColor(Color.rgb(236,243,244));
            text2.setTextColor(Color.rgb(236,243,244));

        }else{
            relativeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(38,47,56)));
            text.setTextColor(Color.rgb(38, 47, 56));
            text2.setTextColor(Color.rgb(38, 47, 56));

        }
    }

    private void waktufirebase(Integer jadwal){
        String pathJam = String.format("Jam/%s",jadwal);
        String pathMenit = String.format("Menit/%s",jadwal);

        DatabaseReference refjam = database.getReference(pathJam);
        DatabaseReference refmenit = database.getReference(pathMenit);

        refjam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                assert value != null;
                int jam = value.intValue();
                    switch(jadwal){
                        case 1:
                            j1 = ehe(jam);
                            txtJAM1.setText(j1);
                            break;
                        case 2:
                            j2 = ehe(jam);
                            txtJAM2.setText(j2);
                            break;
                        case 3:
                            j3 = ehe(jam);
                            txtJAM3.setText(j3);
                            break;
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        refmenit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                int menit = value.intValue();
                    switch(jadwal){
                        case 1:
                            m1 = anying(menit);
                            txtMNT1.setText(m1);
                            break;
                        case 2:
                            m2 = anying(menit);
                            txtMNT2.setText(m2);
                            break;
                        case 3:
                            m3 = anying(menit);
                            txtMNT3.setText(m3);
                            break;
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private String ehe(int entah){
        String P;
        if(entah < 10){
            P = "0" + entah + ":";
        }else{
            P = entah + ":";
        }
        return P;
    }

    private String anying(int entah){
        String P;
        if(entah < 10){
            P = "0" + entah;
        }else{
            P = entah + "";
        }
        return P;
    }

    private void switchfirebase(Integer jadwal){
        String pathState = String.format("State/%s",jadwal);
        DatabaseReference refState = database.getReference(pathState);
        refState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                Integer valueInt = value.intValue();
                Log.d("Value Dari Firebase Buat Jadwal", "Value is: " + value);

                switch (jadwal){
                    case 1:
                        switch1.setChecked(valueInt == 1);
                        switchcheck(switch1,rout1,txtJAM1, txtMNT1);
                        break;
                    case 2:
                        switch2.setChecked(valueInt == 1);
                        switchcheck(switch2,rout2,txtJAM2, txtMNT2);
                        break;
                    case 3:
                        switch3.setChecked(valueInt == 1);
                        switchcheck(switch3, rout3, txtJAM3, txtMNT3);
                        break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setswitch(Switch swtch, Integer jadwal){
        String pathState = String.format("State/%s",jadwal);
        DatabaseReference refState = database.getReference(pathState);

        if(swtch.isChecked()){refState.setValue(1);}
        else if (!swtch.isChecked()){refState.setValue(0);}
    }

}