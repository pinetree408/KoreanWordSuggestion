package com.example.leesangyoon.koeranwordsuggestion;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SpellCheckerSession mScs;

    private EditText editView;
    private TextView inputView;

    private int suggestionOption = 1;
    private TextView suggest1View;
    private TextView suggest2View;
    private TextView suggest3View;

    private KeyboardView keyboardView;
    private TextView enterView;
    private TextView spaceView;
    private TextView deleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);

        editView = (EditText) findViewById(R.id.edit);

        inputView = (TextView) findViewById(R.id.input);

        suggest1View = (TextView) findViewById(R.id.suggest1);
        suggest2View = (TextView) findViewById(R.id.suggest2);
        suggest3View = (TextView) findViewById(R.id.suggest3);

        keyboardView = (KeyboardView) findViewById(R.id.keyboard);

        enterView = (TextView) findViewById(R.id.enter);
        spaceView = (TextView) findViewById(R.id.space);
        deleteView = (TextView) findViewById(R.id.delete);

        keyboardView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (keyboardView.getClass() == v.getClass()) {
                        String[] params = getInputInfo(event);
                        editView.setText(editView.getText() + params[0]);
                        editView.setSelection(editView.getText().length());

                        if (mScs != null) {
                            if (isSentenceSpellCheckSupported()) {
                                mScs.getSentenceSuggestions(new TextInfo[]{new TextInfo(String.valueOf(editView.getText()))}, 18);
                            }
                        }

                    }
                }
                return true;
            }
        });

        enterView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (enterView.getClass() == v.getClass()) {
                        inputView.setText(inputView.getText() + String.valueOf(editView.getText()));
                        editView.setText("");
                        suggest1View.setText("");
                        suggest2View.setText("");
                        suggest3View.setText("");
                    }
                }
                return true;
            }
        });

        spaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (spaceView.getClass() == v.getClass()) {
                        editView.setText(editView.getText() + " ");
                        editView.setSelection(editView.getText().length());

                        if (mScs != null) {
                            if (isSentenceSpellCheckSupported()) {
                                mScs.getSentenceSuggestions(new TextInfo[]{new TextInfo(String.valueOf(editView.getText()))}, 18);
                            }
                        }
                    }
                }
                return true;
            }
        });

        deleteView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (deleteView.getClass() == v.getClass()) {
                        if (editView.getText().length() > 0) {
                            editView.setText(editView.getText().subSequence(0, editView.getText().length() - 1));
                            editView.setSelection(editView.getText().length());

                            if (editView.getText().length() > 0) {
                                if (mScs != null) {
                                    if (isSentenceSpellCheckSupported()) {
                                        mScs.getSentenceSuggestions(new TextInfo[]{new TextInfo(String.valueOf(editView.getText()))}, 18);
                                    }
                                }
                            } else {
                                suggest1View.setText("");
                                suggest2View.setText("");
                                suggest3View.setText("");
                            }
                        } else {
                            suggest1View.setText("");
                            suggest2View.setText("");
                            suggest3View.setText("");
                        }
                    }
                }
                return true;
            }
        });




    }

    private boolean isSentenceSpellCheckSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScs != null) {
            mScs.close();
        }
    }

    private void dumpSuggestionsInfoInternal(
            final List<String> result, final SuggestionsInfo si, final int length, final int offset) {
        final int len = si.getSuggestionsCount();
        for (int j = 0; j < len; ++j) {
            result.add(si.getSuggestionAt(j));
        }
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfos) {

    }

    @Override
    public void onGetSentenceSuggestions(final SentenceSuggestionsInfo[] arg0) {
        if (!isSentenceSpellCheckSupported()) {
            return;
        }
        final List<String> result = new ArrayList<String>();

        for (int i = 0; i < arg0.length; ++i) {
            final SentenceSuggestionsInfo ssi = arg0[i];
            for (int j = 0; j < ssi.getSuggestionsCount(); ++j) {
                dumpSuggestionsInfoInternal(
                        result, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
            }
        }
        Log.d(TAG, result.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result.size() > 2) {
                    suggest1View.setText(result.get(0));
                    suggest2View.setText(result.get(1));
                    suggest3View.setText(result.get(2));
                } else if (result.size() == 2) {
                    suggest1View.setText(result.get(0));
                    suggest2View.setText(result.get(1));
                    suggest3View.setText("");
                } else if (result.size() == 1) {
                    suggest1View.setText(result.get(0));
                    suggest2View.setText("");
                    suggest3View.setText("");
                } else {
                    suggest1View.setText("");
                    suggest2View.setText("");
                    suggest3View.setText("");
                }
            }
        });
    }

    public String[] getInputInfo(MotionEvent event) {
        double tempX = (double) event.getAxisValue(MotionEvent.AXIS_X);
        double tempY = (double) event.getAxisValue(MotionEvent.AXIS_Y);

        String input = keyboardView.getKey(tempX, tempY);

        String[] params = {
                String.valueOf(input),
                String.valueOf(tempX),
                String.valueOf(tempY)
        };
        return params;
    }
}
