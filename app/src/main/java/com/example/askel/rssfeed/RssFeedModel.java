package com.example.askel.rssfeed;

/**
 * Created by askel on 11/03/2018.
 */

//  This class has characteristics for a single item in RSS feed
public class RssFeedModel {
    public String title;
    public String description;
    public String link;

    public RssFeedModel(String tit, String link, String des){
        this.title = tit;
        this.description = des;
        this.link = link;
    }
}
