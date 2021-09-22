package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.rooms.common.CableSearchTopFragment;
import com.underscoreresearch.mauritzremote.rooms.common.GameFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class OfficeFragment extends MainFragment {

    public OfficeFragment() {
        setMainTitle("Office");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Office(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableSearchTopFragment.Office(), "Roku", R.drawable.ic_cable_white);
        addPage(new GameFragment.Office(), "Games", R.drawable.ic_game_white);
        addPage(new TvFragment.Office(), "TV", R.drawable.ic_tv_white);
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
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Game_On));
                    break;
                case 3:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_TV_On));
                    break;
            }
        }
    }
}

