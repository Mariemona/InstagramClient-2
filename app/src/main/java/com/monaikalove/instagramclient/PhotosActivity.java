package com.monaikalove.instagramclient;


import android.support.v7.app.ActionBarActivity;


//import android.preference.PreferenceActivity;
import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.*;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.monaikalove.instagramclient.R;

import java.util.ArrayList;

public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "84e4bfcb8c5d402eb8d2f50df8a071c1";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        //SEND OUT API REQUEST to POPULAR PHOTOS
        photos = new ArrayList<>();

        // 1. create the adapter
        aPhotos = new InstagramPhotosAdapter(this, photos);

        //2. find the list view from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        //3. set the adapter binding it to the Listview
        lvPhotos.setAdapter(aPhotos);

        //fetch the popular photos
        fetchPopularPhotos();


    }

    // Trigger API  request
    public void fetchPopularPhotos(){
       /* CLIENT ID	5b034b5aa97a4a068c201f9f84abf71d
       -Popular: https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN
        -Response       */

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        //Create the network client
        AsyncHttpClient client = new AsyncHttpClient();
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT ).show();
        // trigger the GET request
        client.get(url, null,  new JsonHttpResponseHandler() {
            //on Success (worked, 200)


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Interate each of the photo items and ...
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data"); //array of posts
                    //iterate array of posts
                    for (int i = 0; i < photosJSON.length(); i++){
                        //get the json object at the ...
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        //Decode the attributes of the json...
                        InstagramPhoto photo = new InstagramPhoto();
                        //author name
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        //caption
                        photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        // photo.type = photoJSON.getJSONObject("type").getString("text");
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        //height
                        photo.imageHeigth = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        //likes
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        //add decoded object to the...
                        photos.add(photo);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // callback
                aPhotos.notifyDataSetChanged();
            }

            //on failure (fail)

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // DO SOMETHING
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
