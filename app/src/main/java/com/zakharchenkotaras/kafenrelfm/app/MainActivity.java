package com.zakharchenkotaras.kafenrelfm.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import android.net.Uri;


public class MainActivity extends ActionBarActivity {
    public static Player mp = new Player();
    public static int page_no = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new wallSyncTask().execute(page_no);
        TextView postText = (TextView) findViewById(R.id.postText);
        postText.setText("");
        Button tuda = (Button)findViewById(R.id.btnPervious);
        tuda.setOnClickListener(new OnListListener(false));
        Button suda = (Button)findViewById(R.id.btnNext);
        suda.setOnClickListener(new OnListListener(true));
    }

    public void showMessage(String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Kafenrel FM");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            showMessage("Спасибо Насте за вдохновение.\nTaras Zakharchenko, 2014");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void fillPostPage(JSONObject jobject){
        TextView postText = (TextView) findViewById(R.id.postText);
        try {
            postText.setText(Html.fromHtml(jobject.getJSONArray("response").getJSONObject(1).getString("text")));
            String attachment_type = jobject.getJSONArray("response").getJSONObject(1).getJSONObject("attachment").getString("type");
            if (attachment_type.equals("photo")) {
                new fillImageTask().execute(jobject.getJSONArray("response").getJSONObject(1).getJSONObject("attachment").getJSONObject("photo").getString("src_big"));
            }

            if (jobject.getJSONArray("response").getJSONObject(1).has("attachments")){
                ArrayList<CompositionItem> music_data = new ArrayList<CompositionItem>();
                JSONArray attachments = jobject.getJSONArray("response").getJSONObject(1).getJSONArray("attachments");
                for (int i = 0; i < attachments.length(); i++){
                    JSONObject attachment = attachments.getJSONObject(i);
                    if (attachment.getString("type").equals("audio")){
                        JSONObject track = attachment.getJSONObject("audio");
                        String artist = track.getString("artist");
                        String title = track.getString("title");
                        String address = track.getString("url");
                        music_data.add(new CompositionItem(artist, title, address));
                    }
                }

                MusicListAdapter adapter = new MusicListAdapter(this,
                        R.layout.music_list_item, music_data);
                ListView musicList = (ListView)findViewById(R.id.musicList);
                musicList.setAdapter(adapter);
                setListViewHeightBasedOnChildren(musicList);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private class fillImageTask extends AsyncTask<String, Integer, Bitmap>{
        protected Bitmap doInBackground(String ... params) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(new URL(params[0]).openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView postImage = (ImageView) findViewById(R.id.postImage);
            postImage.setImageBitmap(result);
        }
    }


    public class wallSyncTask extends AsyncTask<Integer, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Integer ... params) {
            JSONObject jobject = null;
            int offset = params[0];
            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                String groupURI = "https://api.vkontakte.ru/method/wall.get?owner_id=-52558955&count=" + Integer.toString(1) + "&offset=" + Integer.toString(offset);
                request.setURI(new URI(groupURI));
                response = client.execute(request);
            } catch (URISyntaxException e) {
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
            catch (Exception e) { e.printStackTrace(); }

            String res_line;
            res_line = sb.toString();
            try {
                jobject = new JSONObject(res_line);
            } catch (JSONException e) {
                jobject = null;
            }
            return jobject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            fillPostPage(result);
        }
    }

    public class OnListListener implements View.OnClickListener{
        Boolean right;
        public OnListListener(Boolean in){
            super();
            this.right = in;
        }
        @Override
        public void onClick(View v) {
            Button b = (Button)v;
            if (right) {
                MainActivity.page_no++;
            } else {
                if (MainActivity.page_no > 0){
                    MainActivity.page_no--;
                } else {
                    MainActivity.page_no = 0;
                }
            }
            //mp.stop();
            //mp.reset();
            new wallSyncTask().execute(MainActivity.page_no);
        }
    }
}
