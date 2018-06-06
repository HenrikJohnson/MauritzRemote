package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.underscoreresearch.mauritzremote.R;

public class MediaCenterControlFragment extends DeviceFragment {
    private void setupButtons(View view) {
        setupButton(view, R.id.btn_b3, R.string.cmd_Go_To_DVD_Menu_In_Media_Center, "Menu");
        setupButton(view, R.id.btn_b4, R.string.cmd_Show_Video_Menu_In_Media_Center, "Video");
        setupButton(view, R.id.btn_b5, R.string.cmd_Play_Next_Audio_In_Media_Center, "Audio");
        setupButton(view, R.id.btn_b6, R.string.cmd_Show_Audio_Menu_In_Media_Center, "Audio");
        setupButton(view, R.id.btn_b7, R.string.cmd_Show_Next_Subtitle_In_Media_Center, "Subtitle");

        setupButton(view, R.id.btn_menu, R.string.cmd_Show_Menu_In_Media_Center, "Menu");
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_Media_Center);
        setupButton(view, R.id.btn_context, R.string.cmd_Show_Context_Menu_In_Media_Center, "Context");
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_Media_Center);
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_Media_Center);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_Media_Center);
        setupButton(view, R.id.btn_info, R.string.cmd_Show_Info_In_Media_Center, "Info");
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_Media_Center);
        setupButton(view, R.id.btn_back, R.string.cmd_Go_Back_In_Media_Center, "Back");

        setupButton(view, R.id.btn_home, R.string.cmd_Go_To_Home_In_Media_Center);
        setupButton(view, R.id.btn_play_music, R.string.cmd_Play_Music_In_Media_Center);
        setupButton(view, R.id.btn_play_movie, R.string.cmd_Play_Movies_In_Media_Center);
        setupButton(view, R.id.btn_play_tv, R.string.cmd_Play_TV_In_Media_Center);

        setupButton(view, R.id.btn_skip_back, R.string.cmd_Step_Back_In_Media_Center);
        setupButton(view, R.id.btn_skip_forward, R.string.cmd_Step_Forward_In_Media_Center);
        setupButton(view, R.id.btn_mute, R.string.cmd_Mute);

        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Media_Center);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Media_Center);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Media_Center);
        setupButton(view, R.id.btn_skip, R.string.cmd_Go_To_Next_In_Media_Center);

        setupButton(view, R.id.btn_page_up, R.string.cmd_Page_Up_In_Media_Center);
        ((TextView)view.findViewById(R.id.lbl_page)).setText("Page");
        setupButton(view, R.id.btn_page_down, R.string.cmd_Page_Down_In_Media_Center);
    }

    public MediaCenterControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.media_center_control, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
