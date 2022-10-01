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

public class ColorsActivity extends AppCompatActivity {
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

        ArrayList<word> al = new ArrayList<>();
        al.add(new word("red", " wetti",R.drawable.color_red, R.raw.color_red));
        al.add(new word("black", "kulluli",R.drawable.color_black, R.raw.color_black));
        al.add(new word("brown", "taakakki",R.drawable.color_brown, R.raw.color_brown));
        al.add(new word("gray", "topoppi",R.drawable.color_gray, R.raw.color_gray));
        al.add(new word("green", "chokkoki",R.drawable.color_green, R.raw.color_green));
        al.add(new word("white", "kelelli",R.drawable.color_white, R.raw.color_white));
        al.add(new word("dusty yellow", "topiise",R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        al.add(new word("mustard yellow", "chiwitaa",R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));




        WordAdapter adapter = new WordAdapter(this, al, R.color.category_colors);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //set a click listener to play audio when a item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the word object at the given position the user clicked on
                word currentword = al.get(position);

                //Release the mediaplayer if it currently exists because we are about to play a complete
                //different audio
                releaseMediaPlayer();

                int result = maudioManager.requestAudioFocus(afChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                //create and setup mediaplayer for audio resource associated with currentword
                mediaPlayer= MediaPlayer.create(ColorsActivity.this, currentword.getaudioResourceID());
                //start the audio file
                mediaPlayer.start();
            }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

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