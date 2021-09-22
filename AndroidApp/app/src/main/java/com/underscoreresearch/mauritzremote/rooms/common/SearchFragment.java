package com.underscoreresearch.mauritzremote.rooms.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.view.BackAwareEditText;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by henri on 1/29/2017.
 */

public class SearchFragment extends DeviceFragment {
    private BackAwareEditText inputFieldEdit;
    private FancyButton micButton;
    private SpeechRecognizer speechRecognizer;

    private void setupButtons(View view) {
        setupButton(view, R.id.btn_menu, R.string.cmd_Go_To_Home_In_Cable, "Home");
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_Cable);
        setupButton(view, R.id.btn_context, R.string.cmd_Search_In_Cable, "Search");
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_Cable);
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_Cable);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_Cable);
        setupButton(view, R.id.btn_info, R.string.cmd_Show_Info_In_Cable, "Info");
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_Cable);
        setupButton(view, R.id.btn_back, R.string.cmd_Go_Back_In_Cable, "Back");
    }

    public void setEditEnabled(boolean enabled) {
        View parent = getView();
        if (parent != null) {
            BackAwareEditText view = parent.findViewById(R.id.inputField);
            if (view != null) {
                view.setText("");
                view.setEnabled(enabled);
                if (enabled) {
                    view.requestFocus();
                    InputMethodManager imm = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                } else {
                    view.clearFocus();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.search, container, false);

        setupButtons(ret);

        micButton = ret.findViewById(R.id.btn_mic);
        micButton.setTag(new Object());

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                inputFieldEdit.setText("");
                inputFieldEdit.setHint("Understanding...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                resetSearch();
            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setIconResource(R.drawable.ic_mic_none);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String search = data.get(0);
                if (search != null && search.length() > 0) {
                    inputFieldEdit.setText(search);
                    RemoteService.sendSearch(getRoom(), inputFieldEdit.getText().toString());
                } else {
                    resetSearch();
                }

                (new Handler()).postDelayed(this::resetSearch,
                        2000);
            }

            private void resetSearch() {
                if (inputFieldEdit != null) {
                    inputFieldEdit.setText("");
                    inputFieldEdit.setHint(getString(R.string.enter_input));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    inputFieldEdit.setText("");
                    inputFieldEdit.setHint("Listening...");

                    micButton.setIconResource(R.drawable.ic_mic);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });

        return ret;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        inputFieldEdit = null;
        speechRecognizer.destroy();
    }

    @Override
    public void lostFocus() {
        if (inputFieldEdit != null) {
            inputFieldEdit.clearFocus();
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputFieldEdit = (BackAwareEditText) view.findViewById(R.id.inputField);

        inputFieldEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    RemoteService.sendInput(getRoom(), inputFieldEdit.getText().toString());
                    inputFieldEdit.setText("");
                    return true;
                }
                return false;
            }
        });
    }
}
