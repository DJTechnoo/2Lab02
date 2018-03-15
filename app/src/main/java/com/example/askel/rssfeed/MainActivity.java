package com.example.askel.rssfeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.askel.rssfeed.Constants.*;

public class MainActivity extends AppCompatActivity {

    private Button button_pref;
    private List<RssFeedModel> itemList;
    private String startPage;
    private ListView listView;
    private List<String> titles;
    private List<String> links;
    private Handler handler;

    private int amount;
    private int frequency;
    private String currentLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView     = (ListView) this.findViewById(R.id.list_view);
        button_pref  = (Button) findViewById(R.id.button_pref);
        startPage    = "https://www.usnews.com/rss/education";
        currentLink  = startPage;
        amount       = 100;
        handler      = new Handler();
        frequency    = FR_TEN_MINUTES; // 10 min by default

        handler.post(loop);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemLink;
                itemLink = links.get(i);
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("ITEM_LINK", itemLink);
                startActivity(intent);
            }
        });
    }




    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
                                                                               IOException{

        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(inputStream, "UTF_8");

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description);
                        items.add(item);

                        Log.d("MainActivity", "Parsed" + title);

                    }


                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }

    }

    public void toPreferences(View view) {
        String oldLink = currentLink;
        Intent toPref = new Intent(this, PrefActivity.class);
        toPref.putExtra("CURRENT_LINK", oldLink);
        startActivityForResult(toPref, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String newLink;


        // get settings from preferences activity and update feed
        if ((requestCode == 1)&& (resultCode == RESULT_OK)) {
            newLink   = data.getStringExtra("NEW_LINK");
            amount    = data.getIntExtra("NEW_AMOUNT", 0);
            frequency = data.getIntExtra("NEW_FREQUENCY", 2000 );
            currentLink = newLink;
            new FetchFeedTask().execute((Void) null);

        }
    }

    // run feed update every frequency-time
    public Runnable loop = new Runnable()
    {
        @Override
        public void run()
        {
            new FetchFeedTask().execute((Void) null);
            handler.postDelayed(this, frequency);
        }
    };



    //  Responsible for fetching all items parsed from xml
    //  and set the feed list according to the preferred amount
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
        private String link;

        @Override
        protected void onPreExecute(){
            link = currentLink;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d("getlink =", link);
                URL url = new URL(link);

                InputStream inputStream = url.openConnection().getInputStream();
                itemList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
               Log.e("ohh", "Error", e);
            } catch (XmlPullParserException e) {
              Log.e("ohh", "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
            super.onPostExecute(success);

                // {loop through the top n amount for display in new list}
                if (amount >= itemList.size()){
                    amount = itemList.size();
                }


                titles = new ArrayList<>();
                links = new ArrayList<>();
                for(int i = 0; i < amount; i++){

                    titles.add(itemList.get(i).title);
                    links.add(itemList.get(i).link);
                }
                ArrayAdapter<String> adap = new ArrayAdapter<String>(
                        MainActivity.this, android.R.layout.simple_list_item_1, titles);

                listView.setAdapter(adap);
            }
        }
    }
}
