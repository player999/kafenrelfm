package com.zakharchenkotaras.kafenrelfm.app;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MusicListAdapter extends ArrayAdapter<CompositionItem> {

    Context context;
    int layoutResourceId;
    ArrayList<CompositionItem> data;

    public MusicListAdapter(Context context, int layoutResourceId, ArrayList<CompositionItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MusicHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MusicHolder();
            holder.txtArtist = (TextView) row.findViewById(R.id.txtArtist);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.playButton = (ImageView) row.findViewById(R.id.playButton);

            row.setTag(holder);
        } else {
            holder = (MusicHolder) row.getTag();
        }

        CompositionItem composition = data.get(position);
        holder.txtArtist.setText(composition.artist);
        holder.txtTitle.setText(composition.composition);
        holder.playButton.setImageResource(composition.icon);
        if (data.get(position).address.equals(MainActivity.mp.currentTrack)){
            holder.playButton.setImageResource(R.drawable.stop);
        } else {
            holder.playButton.setImageResource(R.drawable.play);
        }
        holder.playButton.setOnClickListener(new playClickListener(composition.address));
        return row;
    }
    static class MusicHolder
    {
        TextView txtTitle;
        TextView txtArtist;
        ImageView playButton;
    }


    private class playClickListener implements View.OnClickListener {
        String play_url;
        public playClickListener(){
            super();
        }
        public playClickListener(String url){
            super();
            play_url = url;
        }
        @Override
        public void onClick(View v) {
            ImageView playButton = (ImageView) v;
            Log.i("Play click listener", "Current track: "+ MainActivity.mp.currentTrack);
            Log.i("Play click listener", "Item track: " + this.play_url);
            if (MainActivity.mp.isPlaying() && MainActivity.mp.currentTrack.equals(play_url)){
                MainActivity.mp.stop();
                MainActivity.mp.reset();
                for (int i = 0; i < data.size(); i++) {
                    CompositionItem item = data.get(i);
                    item.icon = R.drawable.play;
                }
                MainActivity.mp.currentTrack = "";

            } else {
                if (MainActivity.mp.isPlaying()) MainActivity.mp.reset();
                for (int i = 0; i < data.size(); i++) {
                    CompositionItem item = data.get(i);
                    if (play_url.equals(item.address)) {
                        item.icon = R.drawable.stop;
                    } else {
                        item.icon = R.drawable.play;
                    }
                }

                MainActivity.mp.playTrack(play_url);
            }
            ListView muslist = (ListView)((ViewGroup) v.getParent()).getParent();
            muslist.setAdapter(MusicListAdapter.this);
        }
    }
}

