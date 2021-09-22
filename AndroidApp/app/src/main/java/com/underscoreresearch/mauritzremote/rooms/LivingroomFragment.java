package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.rooms.common.CableSearchTopFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class LivingroomFragment extends MainFragment {

    public LivingroomFragment() {
        setMainTitle("Livingroom");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Livingroom(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableSearchTopFragment.Livingroom(), "Cable", R.drawable.ic_cable_white);
        addPage(new CableSearchTopFragment.Livingroom(), "Netflix", R.drawable.ic_netflix_white);
        addPage(new CableSearchTopFragment.Livingroom(), "Amazon", R.drawable.ic_amazon_white);
        addPage(new CableSearchTopFragment.Livingroom(), "Disney", R.drawable.ic_disney_white);
        addPage(new TvFragment.Livingroom(), "TV", R.drawable.ic_tv_white);
        //addPage(new DeviceFragment(), "Audio", R.drawable.ic_audio_white);
    }

    @Override
    public void selectPage(int page, boolean switched) {
        super.selectPage(page, switched);

        if (!switched) {
            switch (page) {
                case 0:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Media_Center_On));
                    break;
                case 1:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Cable_On));
                    break;
                case 2:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Netflix_On));
                    break;
                case 3:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Amazon_On));
                    break;
                case 4:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Disney_On));
                    break;
                case 5:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_TV_On));
                    break;
            }
        }
    }
}

