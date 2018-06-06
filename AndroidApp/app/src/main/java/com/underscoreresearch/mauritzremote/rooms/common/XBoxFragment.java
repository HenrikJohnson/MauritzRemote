package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class XBoxFragment extends DeviceFragment {
    protected void setupButtons(View view) {
        setupButton(view, R.id.btn_menu, R.string.cmd_Show_Menu_In_Xbox, "Menu");
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_Xbox);
        setupButton(view, R.id.btn_context, R.string.cmd_Exit_In_Xbox, "Cancel");
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_Xbox);
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_Xbox);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_Xbox);
        setupButton(view, R.id.btn_info, R.string.cmd_Press_XBox_In_Xbox, "XBox");
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_Xbox);
        setupButton(view, R.id.btn_back, R.string.cmd_Press_Enter_In_Xbox, "Enter");

        setupButton(view, R.id.btn_tv_volume_up, R.string.cmd_Increase_Volume_In_TV);
        setupButton(view, R.id.btn_tv_volume_down, R.string.cmd_Decrease_Volume_In_TV);
        setupButton(view, R.id.btn_tv_mute, R.string.cmd_Mute_In_TV);
        setupButton(view, R.id.btn_tv_channel_down, R.string.cmd_Show_Previous_Channel_In_XBox);
        setupButton(view, R.id.btn_tv_channel_up, R.string.cmd_Show_Next_Channel_In_XBox);

        setupButton(view, R.id.btn_d1, R.string.cmd_Press_One_In_XBox);
        setupButton(view, R.id.btn_d2, R.string.cmd_Press_Two_In_XBox);
        setupButton(view, R.id.btn_d3, R.string.cmd_Press_Three_In_XBox);
        setupButton(view, R.id.btn_d4, R.string.cmd_Press_Four_In_XBox);
        setupButton(view, R.id.btn_d5, R.string.cmd_Press_Five_In_XBox);
        setupButton(view, R.id.btn_d6, R.string.cmd_Press_Six_In_XBox);
        setupButton(view, R.id.btn_d7, R.string.cmd_Press_Seven_In_XBox);
        setupButton(view, R.id.btn_d8, R.string.cmd_Press_Eight_In_XBox);
        setupButton(view, R.id.btn_d9, R.string.cmd_Press_Nine_In_XBox);
        setupButton(view, R.id.btn_d0, R.string.cmd_Press_Zero_In_XBox);
        setupButton(view, R.id.btn_hash, R.string.cmd_Show_Split_View_In_Xbox, "Split").setTextSize(getResources().getDimensionPixelSize(R.dimen.smallTextSize));
        setupButton(view, R.id.btn_star, R.string.cmd_Press_View_In_Xbox, "View").setTextSize(getResources().getDimensionPixelSize(R.dimen.smallTextSize));

        setupButton(view, R.id.btn_b1, R.string.cmd_Press_X_In_Xbox, "X").setBackgroundColor(getResources().getColor(R.color.red));
        setupButton(view, R.id.btn_b2, R.string.cmd_Press_Y_In_Xbox, "Y").setBackgroundColor(getResources().getColor(R.color.green));
        setupButton(view, R.id.btn_b3, R.string.cmd_Press_A_In_XBox, "A").setBackgroundColor(getResources().getColor(R.color.blue));
        setupButton(view, R.id.btn_b4, R.string.cmd_Press_B_In_XBox, "B").setBackgroundColor(getResources().getColor(R.color.yellow));
        setupButton(view, R.id.btn_b5, R.string.cmd_Step_Back_In_Xbox);
        setupButton(view, R.id.btn_b6, R.string.cmd_Rewind_In_Xbox);
        setupButton(view, R.id.btn_b7, R.string.cmd_Fast_Forward_In_Xbox);
        setupButton(view, R.id.btn_b8, R.string.cmd_Step_Forward_In_Xbox);
        setupButton(view, R.id.btn_b9, R.string.cmd_Press_Eject_In_Xbox);
        setupButton(view, R.id.btn_b10, R.string.cmd_Pause_In_Xbox);
        setupButton(view, R.id.btn_b11, R.string.cmd_Stop_In_Xbox);
        setupButton(view, R.id.btn_b12, R.string.cmd_Play_In_Xbox);

        setupButton(view, R.id.btn_skip_back, R.string.cmd_Step_Back_In_Media_Center);
        setupButton(view, R.id.btn_skip_forward, R.string.cmd_Step_Forward_In_Media_Center);
        setupButton(view, R.id.btn_mute, R.string.cmd_Mute);

        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Media_Center);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Media_Center);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Media_Center);
        setupButton(view, R.id.btn_skip, R.string.cmd_Go_To_Next_In_Media_Center);
    }

    public static class Office extends XBoxFragment {
        public Office() {
            setRoom("Office");
        }
    }

    public XBoxFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.xbox_fragment, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
