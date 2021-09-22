package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;

public class TvFragment extends DeviceFragment {
    protected void setupButtons(View view) {
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_TV);
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_TV);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_TV);
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_TV);

        setupButton(view, R.id.btn_tv_last, R.string.cmd_Go_To_Last_Channel_In_TV);
        setupButton(view, R.id.btn_tv_channel_down, R.string.cmd_Show_Previous_Channel_In_TV);
        setupButton(view, R.id.btn_tv_channel_up, R.string.cmd_Show_Next_Channel_In_TV);

        setupButton(view, R.id.btn_d1, R.string.cmd_Press_One_In_TV);
        setupButton(view, R.id.btn_d2, R.string.cmd_Press_Two_In_TV);
        setupButton(view, R.id.btn_d3, R.string.cmd_Press_Three_In_TV);
        setupButton(view, R.id.btn_d4, R.string.cmd_Press_Four_In_TV);
        setupButton(view, R.id.btn_d5, R.string.cmd_Press_Five_In_TV);
        setupButton(view, R.id.btn_d6, R.string.cmd_Press_Six_In_TV);
        setupButton(view, R.id.btn_d7, R.string.cmd_Press_Seven_In_TV);
        setupButton(view, R.id.btn_d8, R.string.cmd_Press_Eight_In_TV);
        setupButton(view, R.id.btn_d9, R.string.cmd_Press_Nine_In_TV);
        setupButton(view, R.id.btn_d0, R.string.cmd_Press_Zero_In_TV);

        setupButton(view, R.id.btn_skip_back, R.string.cmd_Step_Back_In_Media_Center);
        setupButton(view, R.id.btn_skip_forward, R.string.cmd_Step_Forward_In_Media_Center);
        setupButton(view, R.id.btn_mute, R.string.cmd_Mute);

        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Media_Center);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Media_Center);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Media_Center);
        setupButton(view, R.id.btn_skip, R.string.cmd_Go_To_Next_In_Media_Center);
    }

    protected void setupPanasonicButtons(View view) {
        setupButton(view, R.id.btn_menu, R.string.cmd_Show_Menu_In_TV, "Menu");
        setupButton(view, R.id.btn_context, R.string.cmd_Cancel_In_TV, "Cancel");
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_TV);
        setupButton(view, R.id.btn_info, R.string.cmd_Show_Viera_In_TV, "Viera");
        setupButton(view, R.id.btn_back, R.string.cmd_Return_In_TV, "Return");

        setupButton(view, R.id.btn_tv_volume_up, R.string.cmd_Increase_Volume_In_TV);
        setupButton(view, R.id.btn_tv_volume_down, R.string.cmd_Decrease_Volume_In_TV);
        setupButton(view, R.id.btn_tv_mute, R.string.cmd_Mute_In_TV);

        setupButton(view, R.id.btn_hash, R.string.cmd_Press_Star_In_TV);
        setupButton(view, R.id.btn_star, R.string.cmd_Press_Hash_In_TV);

        setupButton(view, R.id.btn_b1, R.string.cmd_Press_Red_In_TV, "").setBackgroundColor(getResources().getColor(R.color.red));
        setupButton(view, R.id.btn_b2, R.string.cmd_Press_Green_In_TV, "").setBackgroundColor(getResources().getColor(R.color.green));
        setupButton(view, R.id.btn_b3, R.string.cmd_Press_Blue_In_TV, "").setBackgroundColor(getResources().getColor(R.color.blue));
        setupButton(view, R.id.btn_b4, R.string.cmd_Press_Yellow_In_TV, "").setBackgroundColor(getResources().getColor(R.color.yellow));
        setupButton(view, R.id.btn_b5, R.string.cmd_Show_Viera_Link_In_TV, "Link");
        setupButton(view, R.id.btn_b6, R.string.cmd_Show_Viera_Tools_In_TV, "Tools");
        setupButton(view, R.id.btn_b7, R.string.cmd_Show_Info_In_TV, "Info");
        setupButton(view, R.id.btn_b8, R.string.cmd_Show_Option_In_TV, "Option");
        setupButton(view, R.id.btn_b9, R.string.cmd_Change_Input_In_TV, "Input");
        setupButton(view, R.id.btn_b10, R.string.cmd_Show_Favorite_In_TV, "Fav");
        setupButton(view, R.id.btn_b11, R.string.cmd_Show_Format_In_TV, "Format");
        setupButton(view, R.id.btn_b12, R.string.cmd_Show_Three_D_In_TV, "3D");
    }

    public static class Livingroom extends TvFragment {
        public Livingroom() {
            setRoom("Livingroom");
        }

        @Override
        protected void setupButtons(View view) {
            super.setupButtons(view);
            setupPanasonicButtons(view);
        }
    }
    public static class Office extends TvFragment {
        public Office() {
            setRoom("Office");
        }

        @Override
        protected void setupButtons(View view) {
            super.setupButtons(view);
            setupPanasonicButtons(view);
        }
    }
    public static class Zone2 extends TvFragment {
        public Zone2() {
            setRoom("Zone2");
        }

        @Override
        protected void setupButtons(View view) {
            super.setupButtons(view);

            view.findViewById(R.id.lbl_tv_volume).setVisibility(View.INVISIBLE);

            setupButton(view, R.id.btn_menu, R.string.cmd_Show_Guide_In_TV, "Guide");
            setupButton(view, R.id.btn_context, R.string.cmd_Exit_In_TV, "Exit");
            setupButton(view, R.id.btn_select, R.string.cmd_Select_In_TV, "Menu\nOK");
            setupButton(view, R.id.btn_info, R.string.cmd_Show_Info_In_TV);

            setupButton(view, R.id.btn_hash, R.string.cmd_Preess_Dash_In_TV, "-");

            setupButton(view, R.id.btn_b1, R.string.cmd_Change_Input_In_TV, "Input");
            setupButton(view, R.id.btn_b2, R.string.cmd_Show_Closed_Caption_In_TV, "CC");
            setupButton(view, R.id.btn_b3, R.string.cmd_Show_Format_In_TV, "Format");
            setupButton(view, R.id.btn_b4, R.string.cmd_Sleep_In_Tv, "Sleep");
            setupButton(view, R.id.btn_b5, R.string.cmd_View_TV_In_TV, "TV");
            setupButton(view, R.id.btn_b6, R.string.cmd_View__A_V_In_TV, "A/V");
            setupButton(view, R.id.btn_b7, R.string.cmd_View_Component_In_TV, "Component");
            setupButton(view, R.id.btn_b8, R.string.cmd_View_HDMI_In_TV, "HDMI");
        }
    }

    public TvFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.tv_fragment, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
