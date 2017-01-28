package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.rooms.common.CableFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class BedroomFragment extends MainFragment {

    public BedroomFragment() {
        setMainTitle("Bedroom");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Bedroom(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableFragment.Bedroom(), "Cable", R.drawable.ic_cable_white);
        addPage(new TvFragment.Bedroom(), "TV", R.drawable.ic_tv_white);
    }
}

