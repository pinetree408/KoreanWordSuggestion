package com.example.leesangyoon.koeranwordsuggestion;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    List<TextView> suggestViewList;

    private KeyboardView keyboardView;
    private TextView enterView;
    private TextView spaceView;
    private TextView deleteView;

    List<String> suggestedList;


    char[] cho = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
    char[] jung = { 'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ' };
    char[] jong = { ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editView = (EditText) findViewById(R.id.edit);
        inputView = (TextView) findViewById(R.id.input);

        changeOctupus = (Button) findViewById(R.id.change_octupus);
        changeListView = (Button) findViewById(R.id.change_list_view);

        suggestViewList = new ArrayList<TextView>();
        suggestViewList.add((TextView) findViewById(R.id.suggest1));
        suggestViewList.add((TextView) findViewById(R.id.suggest2));
        suggestViewList.add((TextView) findViewById(R.id.suggest3));

        LayoutInflater vi1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        suggetListLayout = vi1.inflate(R.layout.suggest_list, null);

        LayoutInflater vi2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        octupusLayout = vi2.inflate(R.layout.octupus_layout, null);

        keyboardView = (KeyboardView) findViewById(R.id.keyboard);
        enterView = (TextView) findViewById(R.id.enter);
        spaceView = (TextView) findViewById(R.id.space);
        deleteView = (TextView) findViewById(R.id.delete);

        suggestedList = new ArrayList<String>();

        changeOctupus.setOnTouchListener(this);
        changeListView.setOnTouchListener(this);
        keyboardView.setOnTouchListener(this);
        enterView.setOnTouchListener(this);
        spaceView.setOnTouchListener(this);
        deleteView.setOnTouchListener(this);

        for (int i = 0; i < suggestViewList.size(); i++) {
            final TextView suggestView = suggestViewList.get(i);
            final int index = i;
            suggestView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (suggestView.getClass() == v.getClass()) {
                            setSuggestionList();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (suggestView.getClass() == v.getClass()) {
                            removeSuggestionList();
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE){
                        if (suggestView.getClass() == v.getClass()) {
                            Log.d(TAG, "MOVE");
                        }
                    }
                    return true;
                }
            });

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.change_list_view:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (octupusLayout.getParent() != null) {
                            removeSuggestionList();
                        }
                        suggestionOption = 1;
                        for (TextView suggestView : suggestViewList) {
                            suggestView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                break;
            case R.id.change_octupus:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (suggetListLayout.getParent() != null) {
                            removeSuggestionList();
                        }
                        suggestionOption = 2;
                        for (TextView suggestView : suggestViewList) {
                            suggestView.setVisibility(View.GONE);
                        }
                        break;
                }
                break;
            case R.id.keyboard:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        String[] params = getInputInfo(event);
                        String inputString = "";
                        if (editView.getText().length() > 0) {
                            char targetChar = editView.getText().charAt(editView.getText().length()-1);
                            int charCode = (int) targetChar;
                            if (charCode >= 0xAC00) {
                                int choIndex = ((((charCode - 0xAC00) - (charCode - 0xAC00) % 28)) / 28) / 21;
                                int jungIndex = ((((charCode - 0xAC00) - (charCode - 0xAC00) % 28)) / 28) % 21;
                                int jongIndex = (charCode - 0xAC00) % 28;

                                if (jongIndex > 0) {
                                    if (getIndexOf(cho, params[0].charAt(0)) != -1) {
                                        inputString = editView.getText() + params[0];
                                    } else {
                                        int preCombine = 0xAC00 + 28 * 21 * choIndex + 28 * jungIndex;
                                        int combine = 0xAC00 + 28 * 21 * getIndexOf(cho, jong[jongIndex]) + 28 * getIndexOf(jung, params[0].charAt(0));
                                        inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                        inputString += (char) preCombine;
                                        inputString += (char) combine;

                                    }
                                } else {
                                    jongIndex = getIndexOf(jong, params[0].charAt(0));
                                    if (jongIndex != -1) {
                                        int combine = 0xAC00 + 28 * 21 * choIndex + 28 * jungIndex + jongIndex;
                                        inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                        inputString += (char) combine;
                                    } else {
                                        inputString = editView.getText() + params[0];
                                    }
                                }
                            } else {
                                int choIndex = getIndexOf(cho, targetChar);
                                int jungIndex = getIndexOf(jung, params[0].charAt(0));
                                int combine = 0xAC00 + 28 * 21 * choIndex + 28 * jungIndex;
                                inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                inputString += (char) combine;
                            }
                        } else {
                            inputString = editView.getText() + params[0];
                        }

                        editView.setText(inputString);
                        editView.setSelection(editView.getText().length());
                        getSuggestion(String.valueOf(editView.getText()));

                        switch (suggestionOption) {
                            case 2:
                                if (octupusLayout.getParent() != null) {
                                    removeSuggestionList();
                                }
                                setSuggestionList();
                                break;
                        }
                        break;
                }
                break;
            case R.id.space:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        editView.setText(editView.getText() + " ");
                        editView.setSelection(editView.getText().length());
                        getSuggestion(String.valueOf(editView.getText()));
                        break;
                }
                break;
            case R.id.delete:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (editView.getText().length() > 0) {
                            editView.setText(editView.getText().subSequence(0, editView.getText().length() - 1));
                            editView.setSelection(editView.getText().length());
                            if (editView.getText().length() > 0) {
                                getSuggestion(String.valueOf(editView.getText()));
                            } else {
                                for (TextView suggestView : suggestViewList) {
                                    suggestView.setText("");
                                }
                            }
                        } else {
                            for (TextView suggestView : suggestViewList) {
                                suggestView.setText("");
                            }
                        }
                        break;
                }
                break;
            case R.id.enter:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputView.setText(inputView.getText() + String.valueOf(editView.getText()));
                        editView.setText("");
                        for (TextView suggestView : suggestViewList) {
                            suggestView.setText("");
                        }
                        break;
                }
                break;
        }
        return true;
    }

    public void getSuggestion(String input) {
        final List<String> result = new ArrayList<String>();
        for (String item : Source.dictionary) {
            if (item.indexOf(input) != -1) {
                result.add(item);
            }
        }
        if (result.size() == 2) {
            result.add("");
        } else if (result.size() == 1) {
            result.add("");
            result.add("");
        } else if (result.size() == 0) {
            result.add("");
            result.add("");
            result.add("");
        }
        suggestedList.clear();
        suggestedList.addAll(result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < suggestViewList.size(); i++) {
                    TextView suggestView = suggestViewList.get(i);
                    suggestView.setText(result.get(i));
                }
                position = new Random().nextInt(suggestViewList.size());
                for(int i = 0; i < suggestViewList.size(); i++) {
                    if (i == position) {
                        suggestViewList.get(i).setBackground(getResources().getDrawable(R.drawable.border_gray));
                    } else {
                        suggestViewList.get(i).setBackground(getResources().getDrawable(R.drawable.border_white));
                    }
                }
            }
        });
    }

    public int getIndexOf(char[] sourceList, char target) {
        int ret = -1;
        for (int index = 0; index < sourceList.length; index++) {
            if (target == sourceList[index]) {
                ret = index;
                break;
            }
        }
        return ret;
    }

    public void setSuggestionList() {
        switch(suggestionOption) {
            case 1:
                addContentView(suggetListLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                placeholder = (View) findViewById(R.id.placeholder);
                suggestListItem1View = (TextView) findViewById(R.id.suggest_list_item1);
                suggestListItem2View = (TextView) findViewById(R.id.suggest_list_item2);
                suggestListItem3View = (TextView) findViewById(R.id.suggest_list_item3);

                suggestListItem1View.setText(suggestedList.get(0));
                suggestListItem2View.setText(suggestedList.get(1));
                suggestListItem3View.setText(suggestedList.get(2));

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

                octupusItem1View.setText(suggestedList.get(0));
                octupusItem2View.setText(suggestedList.get(1));
                octupusItem3View.setText(suggestedList.get(2));

                Random generator = new Random();

                if (octupusItem1View.getText().length() > 0) {
                    String pos = String.valueOf(keyboardView.keyboardCharList[generator.nextInt(26)]);
                    int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[0]);
                    int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[1]);
                    octupusItem1View.setPadding(paddingLeft, paddingTop - 100, 0, 0);
                }

                if (octupusItem2View.getText().length() > 0) {
                    String pos = String.valueOf(keyboardView.keyboardCharList[generator.nextInt(26)]);
                    int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[0]);
                    int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[1]);
                    octupusItem2View.setPadding(paddingLeft, paddingTop - 100, 0, 0);
                }

                if (octupusItem3View.getText().length() > 0) {
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
