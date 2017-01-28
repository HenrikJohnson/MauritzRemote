package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.rooms.common.CableFragment;
import com.underscoreresearch.mauritzremote.rooms.common.DeviceFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class LivingroomFragment extends MainFragment {

    public LivingroomFragment() {
        setMainTitle("Livingroom");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Livingroom(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableFragment.Livingroom(), "Cable", R.drawable.ic_cable_white);
        addPage(new TvFragment.Livingroom(), "TV", R.drawable.ic_tv_white);
        addPage(new DeviceFragment(), "Audio", R.drawable.ic_audio_white);
    }
}

