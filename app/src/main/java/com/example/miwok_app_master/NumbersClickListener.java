package com.example.miwok_app_master;

import android.view.View;
import android.widget.Toast;

public class NumbersClickListener implements View.OnClickListener{

    @Override
    public void onClick(View view){
        Toast.makeText(view.getContext(), "open list of all numbers", Toast.LENGTH_SHORT).show();
    }
}
