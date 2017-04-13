package com.example.leesangyoon.koeranwordsuggestion;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText editView;
    private TextView inputView;

    Button changeListView;
    Button changeOctupus;
    private int suggestionOption = 1;

    View suggetListLayout;
    int selectPosition;
    int[] position;
    int selected;
    List<TextView> suggestViewList;

    View octupusLayout;
    List<TextView> octupusItemList;

    List<TextView> suggestItemList;

    private KeyboardView keyboardView;

    List<List<String>> suggestedList;

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

        suggestedList = new ArrayList<List<String>>(
                Arrays.asList(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>())
        );
        position = new int[] {
                0, 0, 0
        };

        editView = (EditText) findViewById(R.id.edit);
        inputView = (TextView) findViewById(R.id.input);

        changeOctupus = (Button) findViewById(R.id.change_octupus);
        changeListView = (Button) findViewById(R.id.change_list_view);

        suggestViewList = new ArrayList<>();
        suggestViewList.add((TextView) findViewById(R.id.suggest1));
        suggestViewList.add((TextView) findViewById(R.id.suggest2));
        suggestViewList.add((TextView) findViewById(R.id.suggest3));

        suggestItemList = new ArrayList<>();
        octupusItemList = new ArrayList<>();

        LayoutInflater vi1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int suggestListId = R.layout.suggest_list;
        suggetListLayout = vi1.inflate(suggestListId, null);

        LayoutInflater vi2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int octupusLayoutId = R.layout.octupus_layout;
        octupusLayout = vi2.inflate(octupusLayoutId, null);

        keyboardView = (KeyboardView) findViewById(R.id.keyboard);
        TextView enterView = (TextView) findViewById(R.id.enter);
        TextView spaceView = (TextView) findViewById(R.id.space);
        TextView deleteView = (TextView) findViewById(R.id.delete);

        changeOctupus.setOnTouchListener(this);
        changeListView.setOnTouchListener(this);
        keyboardView.setOnTouchListener(this);
        enterView.setOnTouchListener(this);
        spaceView.setOnTouchListener(this);
        deleteView.setOnTouchListener(this);

        for (int i = 0; i < suggestViewList.size(); i++) {
            final TextView suggestView = suggestViewList.get(i);
            suggestView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.suggest1:
            case R.id.suggest2:
            case R.id.suggest3:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()) {
                            case R.id.suggest1:
                                selectPosition = 0;
                                break;
                            case R.id.suggest2:
                                selectPosition = 1;
                                break;
                            case R.id.suggest3:
                                selectPosition = 2;
                                break;
                        }
                        if (position[selectPosition] == 1) {
                            setSuggestionList();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (position[selectPosition] == 1) {
                            editView.setText(suggestItemList.get(selectPosition * 3 + selected).getText());
                            editView.setSelection(editView.getText().length());
                            getSuggestion(String.valueOf(editView.getText()));
                            removeSuggestionList();
                        } else {
                            editView.setText(suggestViewList.get(selectPosition).getText());
                            editView.setSelection(editView.getText().length());
                            getSuggestion(String.valueOf(editView.getText()));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (position[selectPosition] == 1) {
                            selected = (int) (event.getY() / 158);
                        }
                        break;
                }
                break;
            case R.id.octupus_item1:
            case R.id.octupus_item2:
            case R.id.octupus_item3:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int index = 0;
                        switch (v.getId()) {
                            case R.id.octupus_item1:
                                index = 0;
                                break;
                            case R.id.octupus_item2:
                                index = 1;
                                break;
                            case R.id.octupus_item3:
                                index = 2;
                                break;
                        }
                        editView.setText(octupusItemList.get(index).getText());
                        editView.setSelection(editView.getText().length());
                        getSuggestion(String.valueOf(editView.getText()));
                        if (octupusLayout.getParent() != null) {
                            removeSuggestionList();
                        }
                        setSuggestionList();
                        break;
                }
                break;
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
                        String inputString;
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
                                        int preCombine = koreanCombine(choIndex, jungIndex, 0);
                                        int combine = koreanCombine(getIndexOf(cho, jong[jongIndex]), getIndexOf(jung, params[0].charAt(0)), 0);
                                        inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                        inputString += (char) preCombine;
                                        inputString += (char) combine;

                                    }
                                } else {
                                    jongIndex = getIndexOf(jong, params[0].charAt(0));
                                    if (jongIndex != -1) {
                                        int combine = koreanCombine(choIndex, jungIndex, jongIndex);
                                        inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                        inputString += (char) combine;
                                    } else {
                                        inputString = editView.getText() + params[0];
                                    }
                                }
                            } else {
                                int choIndex = getIndexOf(cho, targetChar);
                                int jungIndex = getIndexOf(jung, params[0].charAt(0));
                                int combine = koreanCombine(choIndex, jungIndex, 0);
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
                        editView.append(" ");
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
                                switch (suggestionOption) {
                                    case 2:
                                        if (octupusLayout.getParent() != null) {
                                            removeSuggestionList();
                                        }
                                        setSuggestionList();
                                        break;
                                }
                            } else {
                                switch (suggestionOption) {
                                    case 1:
                                        for (TextView suggestView : suggestViewList) {
                                            suggestView.setText("");
                                            suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
                                        }
                                        break;
                                    case 2:
                                        for (int i = 0; i < octupusItemList.size(); i++) {
                                            octupusItemList.get(i).setText("");
                                            octupusItemList.get(i).setVisibility(View.GONE);
                                        }
                                        break;
                                }
                            }
                        } else {
                            switch (suggestionOption) {
                                case 1:
                                    for (TextView suggestView : suggestViewList) {
                                        suggestView.setText("");
                                        suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
                                    }
                                    break;
                                case 2:
                                    for (int i = 0; i < octupusItemList.size(); i++) {
                                        octupusItemList.get(i).setText("");
                                        octupusItemList.get(i).setVisibility(View.GONE);
                                    }
                                    break;
                            }
                        }
                        break;
                }
                break;
            case R.id.enter:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputView.append(String.valueOf(editView.getText()));
                        editView.setText("");
                        for (TextView suggestView : suggestViewList) {
                            suggestView.setText("");
                            suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
                        }
                        break;
                }
                break;
        }
        return true;
    }

    public int koreanCombine(int choIndex, int jungIndex, int jongIndex) {
        return 0xAC00 + 28 * 21 * choIndex + 28 * jungIndex + jongIndex;
    }

    public void getSuggestion(String input) {

        position = new int[] {
                0, 0, 0
        };
        suggestedList = new ArrayList<List<String>>(
                Arrays.asList(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>())
        );
        int total = 0;
        for (String item : Source.dictionary) {
            if (item.indexOf(input) == 0) {
                point:
                {
                    if (total == 9) {
                        break;
                    }
                    for (int row = 0; row < 3; row++) {
                        if (suggestedList.get(row).size() == 0) {
                            suggestedList.get(row).add(item);
                            total++;
                            break point;
                        } else {
                            if (suggestedList.get(row).size() < 3 && suggestedList.get(row).get(0).charAt(0) == item.charAt(0)) {
                                if (!suggestedList.get(row).contains(item)) {
                                    suggestedList.get(row).add(item);
                                    total++;
                                }
                                break point;
                            }
                        }
                    }
                }
            }
        }

        Log.d(TAG, suggestedList.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < suggestViewList.size(); i++) {
                    TextView suggestView = suggestViewList.get(i);
                    if (suggestedList.get(i).size() > 1) {
                        position[i] = 1;
                        suggestView.setText(suggestedList.get(i).get(0));
                        suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_gray, null));
                    } else if (suggestedList.get(i).size() == 1) {
                        suggestView.setText(suggestedList.get(i).get(0));
                        suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
                    } else {
                        suggestView.setText("");
                        suggestView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
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
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item01));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item02));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item03));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item11));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item12));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item13));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item21));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item22));
                suggestItemList.add((TextView) findViewById(R.id.suggest_list_item23));

                for (int i = 0; i < suggestItemList.size(); i++) {
                    TextView suggestItemView = suggestItemList.get(i);
                    int col = i / 3;
                    int row = i % 3;
                    if (col == selectPosition) {
                        if (row == 0) {
                            suggestItemView.setText(suggestedList.get(col).get(row));
                            suggestItemView.setVisibility(View.VISIBLE);
                        } else {
                            if (suggestedList.get(col).size() > row) {
                                suggestItemView.setText(suggestedList.get(col).get(row));
                                suggestItemView.setVisibility(View.VISIBLE);
                            } else {
                                suggestItemView.setText("");
                                suggestItemView.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        suggestItemView.setVisibility(View.GONE);
                    }
                }
                break;
            case 2:
                addContentView(octupusLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                octupusItemList.add((TextView) findViewById(R.id.octupus_item1));
                octupusItemList.add((TextView) findViewById(R.id.octupus_item2));
                octupusItemList.add((TextView) findViewById(R.id.octupus_item3));
                for (int i = 0; i < octupusItemList.size(); i++) {
                    if (suggestedList.get(i).size() > 0) {
                        octupusItemList.get(i).setOnTouchListener(this);
                        octupusItemList.get(i).setText(suggestedList.get(i).get(0));
                        octupusItemList.get(i).setVisibility(View.VISIBLE);
                    } else {
                        octupusItemList.get(i).setText("");
                        octupusItemList.get(i).setVisibility(View.GONE);
                    }
                }

                List<String> prePos = new ArrayList<>();
                for (int i = 0; i < octupusItemList.size(); i++) {
                    if (octupusItemList.get(i).getText().length() > editView.getText().length()) {
                        int charCode = (int) octupusItemList.get(i).getText().charAt(editView.getText().length());
                        int choIndex = ((((charCode - 0xAC00) - (charCode - 0xAC00) % 28)) / 28) / 21;
                        String pos = String.valueOf(cho[choIndex]);
                        int paddingLeft = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[0]);
                        int paddingTop = (int) Double.parseDouble(keyboardView.getKeyPos(pos).split("-")[1]);
                        if (prePos.contains(pos)) {
                            int count = Collections.frequency(prePos, pos);
                            octupusItemList.get(i).setPadding(paddingLeft, paddingTop - (100 + 40*count), 0, 0);
                        } else {
                            octupusItemList.get(i).setPadding(paddingLeft, paddingTop - 100, 0, 0);
                        }
                        prePos.add(pos);
                    } else {
                        octupusItemList.get(i).setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    public void removeSuggestionList() {
        switch(suggestionOption) {
            case 1:
                suggestItemList.clear();
                ((ViewGroup) suggetListLayout.getParent()).removeView(suggetListLayout);
                break;
            case 2:
                octupusItemList.clear();
                ((ViewGroup) octupusLayout.getParent()).removeView(octupusLayout);
                break;
        }
    }

    public String[] getInputInfo(MotionEvent event) {
        double tempX = (double) event.getAxisValue(MotionEvent.AXIS_X);
        double tempY = (double) event.getAxisValue(MotionEvent.AXIS_Y);
        String input = keyboardView.getKey(tempX, tempY);

        return new String[] {
                String.valueOf(input),
                String.valueOf(tempX),
                String.valueOf(tempY)
        };
    }

}
