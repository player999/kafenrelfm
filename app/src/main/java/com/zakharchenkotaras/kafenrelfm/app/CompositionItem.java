package com.zakharchenkotaras.kafenrelfm.app;
/**
 * Created by player999 on 25.03.14.
 */

public class CompositionItem {
    String artist;
    String composition;
    String address;
    int icon;
    public CompositionItem(String art, String comp, String addr) {
        super();
        artist = art;
        composition = comp;
        address = addr;
        icon = R.drawable.play;
    }
    public CompositionItem() {
        super();
        artist = "Ivan Petrov";
        composition = "A song";
        address = "http://google.com";
        icon = R.drawable.play;
    }
}
