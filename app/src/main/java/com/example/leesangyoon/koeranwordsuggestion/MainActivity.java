package com.example.leesangyoon.koeranwordsuggestion;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SpellCheckerSession mScs;

    private EditText editView;
    private TextView inputView;

    Button changeListView;
    Button changeOctupus;

    private int suggestionOption = 1;
    View suggetListLayout;
    View placeholder;
    int position;
    TextView suggestListItem1View;
    TextView suggestListItem2View;
    TextView suggestListItem3View;
    View octupusLayout;
    TextView octupusItem1View;
    TextView octupusItem2View;
    TextView octupusItem3View;

    private TextView suggest1View;
    private TextView suggest2View;
    private TextView suggest3View;

    private KeyboardView keyboardView;
    private TextView enterView;
    private TextView spaceView;
    private TextView deleteView;

    List<String> suggestedList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);

        editView = (EditText) findViewById(R.id.edit);

        inputView = (TextView) findViewById(R.id.input);

        changeOctupus = (Button) findViewById(R.id.change_octupus);
        changeListView = (Button) findViewById(R.id.change_list_view);

        suggest1View = (TextView) findViewById(R.id.suggest1);
        suggest2View = (TextView) findViewById(R.id.suggest2);
        suggest3View = (TextView) findViewById(R.id.suggest3);

        LayoutInflater vi1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        suggetListLayout = vi1.inflate(R.layout.suggest_list, null);

        LayoutInflater vi2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        octupusLayout = vi2.inflate(R.layout.octupus_layout, null);

        keyboardView = (KeyboardView) findViewById(R.id.keyboard);

        enterView = (TextView) findViewById(R.id.enter);
        spaceView = (TextView) findViewById(R.id.space);
        deleteView = (TextView) findViewById(R.id.delete);

        suggestedList = new ArrayList<String>();

        changeOctupus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (changeOctupus.getClass() == v.getClass()) {
                            if (suggetListLayout.getParent() != null) {
                                removeSuggestionList();
                            }
                            suggestionOption = 2;
                            suggest1View.setVisibility(View.GONE);
                            suggest2View.setVisibility(View.GONE);
                            suggest3View.setVisibility(View.GONE);
                        }
                        break;
                }
                return true;
            }
        });

        changeListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (changeListView.getClass() == v.getClass()) {
                            if (octupusLayout.getParent() != null) {
                                removeSuggestionList();
                            }
                            suggestionOption = 1;
                            suggest1View.setVisibility(View.VISIBLE);
                            suggest2View.setVisibility(View.VISIBLE);
                            suggest3View.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });

        keyboardView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (keyboardView.getClass() == v.getClass()) {
                            String[] params = getInputInfo(event);
                            editView.setText(editView.getText() + params[0]);
                            editView.setSelection(editView.getText().length());

                            if (mScs != null) {
                                if (isSentenceSpellCheckSupported()) {
                                    mScs.getSentenceSuggestions(new TextInfo[]{new TextInfo(String.valueOf(editView.getText()))}, 18);
                                }
                            }
                            switch (suggestionOption) {
                                case 2:
                                    if (octupusLayout.getParent() != null) {
                                        removeSuggestionList();
                                    }
                                    setSuggestionList();
                                    break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (keyboardView.getClass() == v.getClass()) {
                            switch (suggestionOption) {
                                case 2:
                                    //removeSuggestionList();
                                    break;
                            }
                        }
                        break;

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

        suggest1View.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (suggest1View.getClass() == v.getClass()) {
                        Log.d(TAG, "TOUCH");
                        setSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (suggest1View.getClass() == v.getClass()) {
                        removeSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    if (suggest1View.getClass() == v.getClass()) {
                        Log.d(TAG, "MOVE");
                    }
                }
                return true;
            }
        });

        suggest2View.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (suggest2View.getClass() == v.getClass()) {
                        Log.d(TAG, "TOUCH");
                        setSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (suggest2View.getClass() == v.getClass()) {
                        removeSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    if (suggest2View.getClass() == v.getClass()) {
                        Log.d(TAG, "MOVE");
                    }
                }
                return true;
            }
        });

        suggest3View.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (suggest3View.getClass() == v.getClass()) {
                        Log.d(TAG, "TOUCH");
                        setSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (suggest3View.getClass() == v.getClass()) {
                        removeSuggestionList();
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE){
                    if (suggest3View.getClass() == v.getClass()) {
                        Log.d(TAG, "MOVE");
                    }
                }
                return true;
            }
        });
    }

    public void setSuggestionList() {
        switch(suggestionOption) {
            case 1:
                addContentView(suggetListLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                placeholder = (View) findViewById(R.id.placeholder);
                suggestListItem1View = (TextView) findViewById(R.id.suggest_list_item1);
                suggestListItem2View = (TextView) findViewById(R.id.suggest_list_item2);
                suggestListItem3View = (TextView) findViewById(R.id.suggest_list_item3);

                if (suggestedList.size() > 2) {
                    suggestListItem1View.setText(suggestedList.get(0));
                    suggestListItem2View.setText(suggestedList.get(1));
                    suggestListItem3View.setText(suggestedList.get(2));
                } else if (suggestedList.size() == 2) {
                    suggestListItem1View.setText(suggestedList.get(0));
                    suggestListItem2View.setText(suggestedList.get(1));
                    suggestListItem3View.setText("");
                } else if (suggestedList.size() == 1) {
                    suggestListItem1View.setText(suggestedList.get(0));
                    suggestListItem2View.setText("");
                    suggestListItem3View.setText("");
                } else {
                    suggestListItem1View.setText("");
                    suggestListItem2View.setText("");
                    suggestListItem3View.setText("");
                }

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (float) position
                );
                placeholder.setLayoutParams(param);

                break;
            case 2:
                addContentView(octupusLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                octupusItem1View = (TextView) findViewById(R.id.octupus_item1);
                octupusItem2View = (TextView) findViewById(R.id.octupus_item2);
                octupusItem3View = (TextView) findViewById(R.id.octupus_item3);

                if (suggestedList.size() > 2) {
                    octupusItem1View.setText(suggestedList.get(0));
                    octupusItem2View.setText(suggestedList.get(1));
                    octupusItem3View.setText(suggestedList.get(2));
                } else if (suggestedList.size() == 2) {
                    octupusItem1View.setText(suggestedList.get(0));
                    octupusItem2View.setText(suggestedList.get(1));
                    octupusItem3View.setText("");
                } else if (suggestedList.size() == 1) {
                    octupusItem1View.setText(suggestedList.get(0));
                    octupusItem2View.setText("");
                    octupusItem3View.setText("");
                } else {
                    octupusItem1View.setText("");
                    octupusItem2View.setText("");
                    octupusItem3View.setText("");
                }

                Random generator = new Random();

                if (octupusItem1View.getText().length() > 0) {
                    String pos = String.valueOf(keyboardView.keyboardCharList[generator.nextInt(26)]);
                    int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[0]);
                    int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[1]);
                    octupusItem1View.setPadding(paddingLeft, paddingTop - 100, 0, 0);
                }

                if (octupusItem2View.getText().length() > 0) {
                    String pos = String.valueOf(keyboardView.keyboardCharList[generator.nextInt(26)]);
                    int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos("c").split("-")[0]);
                    int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos("c").split("-")[1]);
                    octupusItem2View.setPadding(paddingLeft, paddingTop - 100, 0, 0);
                }

                if (octupusItem3View.getText().length() > 1) {
                    String pos = String.valueOf(keyboardView.keyboardCharList[generator.nextInt(26)]);
                    int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[0]);
                    int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[1]);
                    octupusItem3View.setPadding(paddingLeft, paddingTop - 100, 0, 0);
                }
                break;
        }
    }

    public void removeSuggestionList() {
        switch(suggestionOption) {
            case 1:
                ((ViewGroup) suggetListLayout.getParent()).removeView(suggetListLayout);
                break;
            case 2:
                ((ViewGroup) octupusLayout.getParent()).removeView(octupusLayout);
                break;
        }
    }

    private boolean isSentenceSpellCheckSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    public void onResume() {
        super.onResume();
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);
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
        suggestedList.clear();
        suggestedList.addAll(result);
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
                TextView[] viewSet = {
                        suggest1View,
                        suggest2View,
                        suggest3View
                };

                position = new Random().nextInt(3);
                for(int i = 0; i < 3; i++) {
                    if (i == position) {
                        viewSet[i].setBackground(getResources().getDrawable(R.drawable.border_gray));
                    } else {
                        viewSet[i].setBackground(getResources().getDrawable(R.drawable.border_white));
                    }
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
