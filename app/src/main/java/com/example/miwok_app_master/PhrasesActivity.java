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

public class PhrasesActivity extends AppCompatActivity {
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

        ArrayList<word> prhasesList = new ArrayList<>();
        prhasesList.add(new word("where are you going ", "mintu wiksus", R.raw.phrase_where_are_you_going));
        prhasesList.add(new word("what is your name", "tinna oyasse'na", R.raw.phrase_what_is_your_name));
        prhasesList.add(new word("my name is ", "oyaaset...", R.raw.phrase_my_name_is));
        prhasesList.add(new word("How are you felling", "michekses?", R.raw.phrase_how_are_you_feeling));
        prhasesList.add(new word("I'm felling good ", "kuchi achit", R.raw.phrase_im_feeling_good));
        prhasesList.add(new word("Are you coming ?", "eenes'aa", R.raw.phrase_are_you_coming));
        prhasesList.add(new word("Yes I'm coming" , "hee'eneem", R.raw.phrase_yes_im_coming));
        prhasesList.add(new word("I'm coming", "eenem", R.raw.phrase_im_coming));
        prhasesList.add(new word("Let's go ", "yoowutis", R.raw.phrase_lets_go));
        prhasesList.add(new word("Come here ", "anni'hem",R.raw.phrase_come_here));


        WordAdapter adapter = new WordAdapter(this, prhasesList, R.color.category_phrases);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        //set a click listener to play audio when a item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the word object at the given position the user clicked on
                word currentword = prhasesList.get(position);

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
                mediaPlayer= MediaPlayer.create(PhrasesActivity.this, currentword.getaudioResourceID());
                //start the audio file
                mediaPlayer.start();

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