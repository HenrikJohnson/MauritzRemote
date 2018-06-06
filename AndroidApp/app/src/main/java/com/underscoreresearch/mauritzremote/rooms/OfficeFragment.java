package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.rooms.common.CableFragment;
import com.underscoreresearch.mauritzremote.rooms.common.DeviceFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;
import com.underscoreresearch.mauritzremote.rooms.common.XBoxFragment;

public class OfficeFragment extends MainFragment {

    public OfficeFragment() {
        setMainTitle("Office");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Office(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new TvFragment.Office(), "TV", R.drawable.ic_tv_white);
        addPage(new XBoxFragment.Office(), "XBox", R.drawable.ic_xbox_white);
        addPage(new MediaCenterFragment.Office(), "PS3", R.drawable.ic_playstation_white);
        addPage(new MediaCenterFragment.Office(), "WII", R.drawable.ic_wii_white);
        addPage(new DeviceFragment(), "Audio", R.drawable.ic_audio_white);
    }

    @Override
    public void selectPage(int page, boolean switched) {
        super.selectPage(page, switched);

        switch(page) {
            case 0:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Media_Center_On));
                break;
            case 1:
                if (!switched) {
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_TV_On));
                }
                break;
            case 2:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_XBox_One_On));
                break;
            case 3:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Play_Station_On));
                break;
            case 4:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Wii_On));
                break;
        }
    }
}

