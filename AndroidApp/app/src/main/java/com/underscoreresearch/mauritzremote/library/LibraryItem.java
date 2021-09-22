package com.underscoreresearch.mauritzremote.library;

import lombok.Data;

/**
 * Created by henri on 1/29/2017.
 */

@Data
public class LibraryItem
{
    public String itemId;
    public String artist;
    public String album;
    public String title;
    public int duration;
    public int played;
    public int voted;
    public int trackNumber;
    public int rating;
    public double toplist;
    public String coverUrl;
}
