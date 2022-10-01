package com.example.miwok_app_master;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {
   private MediaPlayer mediaPlayer;

   //This listener gets triggered once MediaPlayer has completed playing the audio file
   private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp){
        releaseMediaPlayer();
    }
    };

   private AudioManager maudioManager;

   AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
       @Override
       public void onAudioFocusChange(int focusChange) {     //focus change is new audio focus state
           if(focusChange == AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
               // the AUDIO_LOSS_TRANSIENT case means that we've lost audio focus for short amount of time
               // the AUDIO_LOSS_TRANSIENT_CAN_DUCK means that our app is allowed to continue playing sound at lower volume .
               // we can handle both cases the same way because our app is playing short sound files .

               // pause playback and resent player to start of the file , that way we play the word from the begining when we resume playback
               mediaPlayer.pause();
               mediaPlayer.seekTo(0);

           }else if(focusChange == AUDIOFOCUS_GAIN){
               //the AUDIOFOCUS_GAIN case means we have regained focus and can Resume playback
               mediaPlayer.start();
           }else if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
              //the AUDIOFOCUS_LOSS case means that we've lost audio focus and stop playback and clean up resources
               releaseMediaPlayer();
           }
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);

    maudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

       final ArrayList<word> Words = new ArrayList<word>();
        Words.add(new word("one", "latti", R.drawable.number_one, R.raw.number_one));
        Words.add(new word("two", "ottiko",R.drawable.number_two, R.raw.number_two));
        Words.add(new word("three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        Words.add(new word("four", "oyyisa",R.drawable.number_four, R.raw.number_four));
        Words.add(new word("five", "massokka", R.drawable.number_five, R.raw.number_five));
        Words.add(new word("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        Words.add(new word("seven", "tenekaku", R.drawable.number_seven, R.raw.number_seven));
        Words.add(new word("eight", "kawinta",R.drawable.number_eight, R.raw.number_eight));
        Words.add(new word("nine", "wo'e",R.drawable.number_nine, R.raw.number_nine));
        Words.add(new word("ten", "na'accha",R.drawable.number_ten, R.raw.number_ten));


//        LinearLayout rootView = findViewById(R.id.rootView);
//        int i =0;
//        while(i<words.size()){
//            TextView text = new TextView(this);
//            text.setText(words.get(i));
//            rootView.addView(text);
//            i++;
//        }

  //create an arrayAdapter whose data source is list of strings. the adapter knows how to crate layout for each item in list, using
  // the simple_list_item1.xml in android framework, this list item layout contains single @textview , which  adapter will set to display
        //the words
        WordAdapter itemAdapter = new WordAdapter(this, Words, R.color.category_numbers);

        //find listView object in view heirchachy of the activity .
        ListView listView = findViewById(R.id.listView);

  //make the listView use arrayAdapter , so that listView will display list of items for each word in list of words ,
  // do this by calling setAdapter method on listView object , and passes in 1 argument , which is ArrayAdapter with variable name itemAdapter
        listView.setAdapter(itemAdapter);

        //set a click listener to play audio when a item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //Get the word object at the given position the user clicked on
                word currentword = Words.get(position);

                //Release the mediaplayer if it currently exists because we are about to play a complete
                //different audio
                releaseMediaPlayer();

                //Request audio focus for playback
                int result = maudioManager.requestAudioFocus(afChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // we have audio focus now 



                //create and setup mediaplayer for audio resource associated with currentword
                mediaPlayer= MediaPlayer.create(NumbersActivity.this, currentword.getaudioResourceID());
                //start the audio file
                mediaPlayer.start();

                //setup a listener on mediaplayer so that we can stop and release the media player once
                //sounds has finished playing
                mediaPlayer.setOnCompletionListener(mCompletionListener);
            }
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            //Regardless of whether we are granted audio focus, abandon it . This also unregisters audioFocusChangeListener
            // so we don't get anymore callbacks
            maudioManager.abandonAudioFocus(afChangeListener);
        }
    }
}