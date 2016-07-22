/*
 * CS 193A, Santi Gallego
 * This program demonstrates REST APIs by fetching some data from two simple APIs,
 * the Internet Chuck Norris Database (ICNDb) and The Cat API.
 * We use the Ion library for downloading JSON/XML data from URLs, and
 * we use the Picasso library for fetching images from URLs.
 */

package cs193a.stanford.edu.resttest;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.JSONArrayBody;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;

import stanford.androidlib.*;
import stanford.androidlib.xml.XML;

public class RestActivity extends SimpleActivity {

    private static final String SUCCESS = "success";
    private int width, height;

    // auto-generated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }


    public void chuckNorrisClick(View view) {
        Ion.with(this)
                .load("http://api.icndb.com/jokes/random")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processChuckNorrisData(result);
                    }
                });
    }


    public void catClick(View view) {
        Ion.with(this)
                .load("http://thecatapi.com/api/images/get?results_per_page=4&format=xml")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processCatData(result);
                    }
                });
    }

    /*
     * The JSON data uses the following format:
     *
     *  {
     *   "type": "success",
     *   "value": {
     *              "id": 496,
     *              "joke": "Chuck Norris went out of an infinite loop.",
     *              "categories": ["nerdy"]
     *            }
     *  }
     */

    private void processChuckNorrisData(String data) {
        try {
            JSONObject json = new JSONObject(data);
            if(json.getString("type").equals(SUCCESS)) {
                JSONObject value = json.getJSONObject("value");
                String joke = value.getString("joke");
                JSONArray categories = value.getJSONArray("categories");

                TextView output = (TextView) findViewById(R.id.output);
                output.setText(joke);
            } else {
                Log.e("JSON", "Json failed");
            }
        } catch (JSONException e) {
            Log.wtf("json", e);
        }
    }




    /*
     * The JSON data will be in the following format:
     *
     * {
     *   "response": {
     *     "data": {
     *         "images": {
     *             "image": [
     *                {"url":"http:\/\/24.media.tumblr.com\/tumblr_luw2y2MCum1qbdrypo1_500.jpg","id":"d40","source_url":"http:\/\/thecatapi.com\/?id=d40"},
     *                {"url":"http:\/\/25.media.tumblr.com\/tumblr_m4rwp4gkQ11r6jd7fo1_400.jpg","id":"e85","source_url":"http:\/\/thecatapi.com\/?id=e85"},
     *  ...
     * }
     */

    private void processCatData(String data) {
        try {
            JSONObject json = XML.toJSONObject(data);
            JSONArray jsonArray = json
                    .getJSONObject("response")
                    .getJSONObject("data")
                    .getJSONObject("images")
                    .getJSONArray("image");

            GridLayout gridLayout = (GridLayout) findViewById(R.id.grid);
            gridLayout.removeAllViews();

            for(int i = 0; i < jsonArray.length(); i++) {
                String url = jsonArray.getJSONObject(i).getString("url");
                Log.v("URL", url);

                ImageView imageView = new ImageView(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                imageView.setLayoutParams(params);

                int padding = (int) (getResources().getDimension(R.dimen.activity_horizontal_margin) / getResources().getDisplayMetrics().density);

                Log.d("DIMENS", "" + padding);

                int picSize = (width - padding * 2) / 2 - 20;

                Picasso.with(this)
                        .load(url)
                        .resize(picSize, picSize)
                        .into(imageView);
                gridLayout.addView(imageView);
            }
        } catch(JSONException e) {
            Log.wtf("JSON", "Json failed");
        }
    }

}