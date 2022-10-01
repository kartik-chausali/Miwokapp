package com.example.miwok_app_master;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<word> {

    private int mColorResourceID;
    public WordAdapter(Activity context, ArrayList<word> Words, int colorResourceID){
        //here we initialze the ArrayAdapter's internal storage for the context and the list
        //because this a custom adapter for two text views and an image view , the adapter is not going to use second argument
        //so here we used 0
        super(context, 0, Words);
        mColorResourceID = colorResourceID;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //check if existing view is being reused , otherwise inflate a view

        View listitemView = convertView;
        if(listitemView == null){
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_item,parent,false);
        }

        //we get word object located at this position
        word currentWord = getItem(position);

        //find the textview in list_item_xml layout with id
        //the listitemView variable is currently referencing the root linear layout for the list item layout
        // so if we call the find view by id method on listitemView then we can find miwok and default text view 
        TextView textView = listitemView.findViewById(R.id.miwok_text_view);
        textView.setText(currentWord.getMiwokTranslation());

        TextView textView1 = listitemView.findViewById(R.id.default_text_view);
        textView1.setText(currentWord.getDefaultTranslation());

        ImageView imageView = listitemView.findViewById(R.id.image);

        if(currentWord.hasImage()){
            imageView.setImageResource(currentWord.getImageResourceID());
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }

        //set theme color for list item
        View textContainer = listitemView.findViewById(R.id.text_Container);
        //find the color that resource id maps to
        int color = ContextCompat.getColor(getContext(), mColorResourceID);
        //set the background color of text container view
        textContainer.setBackgroundColor(color);

        return listitemView;
    }
}
