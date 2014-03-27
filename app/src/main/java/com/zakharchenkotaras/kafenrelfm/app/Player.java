package com.zakharchenkotaras.kafenrelfm.app;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class Player extends MediaPlayer{
    public String currentTrack;
    public Player(){
        super();
        currentTrack = "";
        setOnPreparedListener(new MyOnPrepared());
    }

    public Player(String track_url){
        super();
        setOnPreparedListener(new MyOnPrepared());
        setOnCompletionListener(new MyOnStop());
        playTrack(track_url);
    }

    public void playTrack(String track_url){
        try {
            setDataSource(track_url);
            Log.i("Player", "Ready to sync!");
            prepareAsync();
            this.currentTrack = track_url;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class MyOnPrepared implements OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i("Player", "Going to play now!");
            mp.start();
        }
    }

    private class MyOnStop implements OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.stop();
            mp.reset();
            currentTrack = "";
        }
    }
}
