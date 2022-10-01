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

public class FamilyActivity extends AppCompatActivity {
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
        al.add(new word("father", "epe",R.drawable.family_father, R.raw.family_father));
        al.add(new word("mother", "eta",R.drawable.family_mother, R.raw.family_mother));
        al.add(new word("son", "agnsi",R.drawable.family_son, R.raw.family_son));
        al.add(new word("daughter", "tune",R.drawable.family_daughter, R.raw.family_daughter));
        al.add(new word("older brother", "taachi",R.drawable.family_older_brother, R.raw.family_older_brother));
        al.add(new word("younger brother", "chilliti",R.drawable.family_younger_brother, R.raw.family_younger_brother));
        al.add(new word("older sister", "tete",R.drawable.family_older_sister, R.raw.family_older_sister));
        al.add(new word("younger sister", "kolitti",R.drawable.family_younger_sister, R.raw.family_younger_sister));
        al.add(new word("grandmother", "ama",R.drawable.family_grandmother, R.raw.family_grandmother));
        al.add(new word("grandfather", "paapa",R.drawable.family_grandfather, R.raw.family_grandfather));


        WordAdapter adapter = new WordAdapter(this, al,R.color.category_family);
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
                    // we have audio focus now

                //create and setup mediaplayer for audio resource associated with currentword
                mediaPlayer= MediaPlayer.create(FamilyActivity.this, currentword.getaudioResourceID());
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