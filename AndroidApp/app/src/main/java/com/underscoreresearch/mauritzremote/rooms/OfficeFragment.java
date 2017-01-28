package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.rooms.common.CableFragment;
import com.underscoreresearch.mauritzremote.rooms.common.DeviceFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class OfficeFragment extends MainFragment {

    public OfficeFragment() {
        setMainTitle("Office");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Livingroom(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableFragment.Office(), "Cable", R.drawable.ic_cable_white);
        addPage(new TvFragment.Office(), "TV", R.drawable.ic_tv_white);
        addPage(new DeviceFragment(), "XBox", R.drawable.ic_xbox_white);
        addPage(new DeviceFragment(), "PS3", R.drawable.ic_playstation_white);
        addPage(new DeviceFragment(), "WII", R.drawable.ic_wii_white);
        addPage(new DeviceFragment(), "Audio", R.drawable.ic_audio_white);
    }
}

