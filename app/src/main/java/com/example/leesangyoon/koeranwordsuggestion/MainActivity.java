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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText editView;
    private TextView inputView;

    Button changeListView;
    Button changeOctupus;
    private int suggestionOption = 1;

    View suggetListLayout;
    List<TextView> suggestViewList;
    List<TextView> suggestItemList;
    int selectPosition;
    int selected;

    View octupusLayout;
    List<TextView> octupusItemList;

    private KeyboardView keyboardView;
    boolean isShifted;

    List<List<String>> suggestedList;

    char[] cho = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
    char[] jung = { 'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ' };
    char[] jong = { ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

    String ip = "143.248.53.191";
    int port = 5000;

    Socket socket;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        suggestedList = new ArrayList<List<String>>(
                Arrays.asList(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>())
        );

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
        isShifted = false;

        TextView shiftView = (TextView) findViewById(R.id.shift);
        TextView enterView = (TextView) findViewById(R.id.enter);
        TextView spaceView = (TextView) findViewById(R.id.space);
        TextView deleteView = (TextView) findViewById(R.id.delete);

        changeOctupus.setOnTouchListener(this);
        changeListView.setOnTouchListener(this);
        keyboardView.setOnTouchListener(this);
        shiftView.setOnTouchListener(this);
        enterView.setOnTouchListener(this);
        spaceView.setOnTouchListener(this);
        deleteView.setOnTouchListener(this);

        for (int i = 0; i < suggestViewList.size(); i++) {
            final TextView suggestView = suggestViewList.get(i);
            suggestView.setOnTouchListener(this);
        }

        try {
            socket = IO.socket("http://" + ip + ":" + port + "/mynamespace");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG, "connect");
            }

        }).on("response", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj;
                if (args.length == 1) {
                    obj = (JSONObject)args[0];
                    try {
                        setSuggestedList(obj.get("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    obj = (JSONObject)args[1];
                }
                Log.d(TAG, obj.toString());
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();
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
                        setSuggestionList();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (getIndexOf(cho, editView.getText().charAt(editView.getText().length()-1)) != -1){
                            editView.setText(suggestItemList.get(selectPosition * 3 + selected).getText());
                        } else {
                            editView.append(" " + suggestItemList.get(selectPosition * 3 + selected).getText());
                        }
                        editView.setSelection(editView.getText().length());
                        getSuggestion(String.valueOf(editView.getText()));
                        removeSuggestionList();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        selected = (int) (event.getY() / 158);
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
                                    //char[] jong = { ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
                                    //        'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
                                    //        'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };
                                    if (getIndexOf(cho, params[0].charAt(0)) != -1) {
                                        int tempIdx = getIndexOf(jong, params[0].charAt(0));
                                        int finalIdx = -1;
                                        boolean isChanged = false;
                                        switch (jongIndex){
                                            case 1: // ㄱ
                                                switch(tempIdx) {
                                                    case 1:
                                                        // ㄲ
                                                        isChanged = true;
                                                        finalIdx = 2;
                                                        break;
                                                    case 19:
                                                        // ㄱㅅ
                                                        isChanged = true;
                                                        finalIdx = 3;
                                                        break;
                                                }
                                                break;
                                            case 4: // ㄴ
                                                switch (tempIdx) {
                                                    case 22:
                                                        // ㄴㅈ
                                                        isChanged = true;
                                                        finalIdx = 5;
                                                        break;
                                                }
                                                break;
                                            case 8: // ㄹ
                                                switch (tempIdx) {
                                                    case 1:
                                                        // ㄹㄱ
                                                        isChanged = true;
                                                        finalIdx = 9;
                                                        break;
                                                    case 16:
                                                        // ㄹㅁ
                                                        isChanged = true;
                                                        finalIdx = 10;
                                                        break;
                                                    case 17:
                                                        // ㄹㅂ
                                                        isChanged = true;
                                                        finalIdx = 11;
                                                        break;
                                                    case 19:
                                                        // ㄹㅅ
                                                        isChanged = true;
                                                        finalIdx = 12;
                                                        break;
                                                    case 25:
                                                        // ㄹㅌ
                                                        isChanged = true;
                                                        finalIdx = 13;
                                                        break;
                                                    case 26:
                                                        // ㄹㅍ
                                                        isChanged = true;
                                                        finalIdx = 14;
                                                        break;
                                                    case 27:
                                                        // ㄹㅎ
                                                        isChanged = true;
                                                        finalIdx = 15;
                                                        break;
                                                }
                                            case 17: //ㅂ
                                                switch (tempIdx) {
                                                    case 19:
                                                        // ㅂㅅ
                                                        isChanged = true;
                                                        finalIdx = 18;
                                                        break;
                                                }
                                                break;
                                        }
                                        if (isChanged) {
                                            int combine = koreanCombine(choIndex, jungIndex, finalIdx);
                                            inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                            inputString += (char) combine;
                                        } else {
                                            inputString = editView.getText() + params[0];
                                        }
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
                                        jongIndex = getIndexOf(jung, params[0].charAt(0));
                                        if (jongIndex != -1) {
                                            if (jungIndex == 8) {
                                                if (jongIndex == 0 | jongIndex == 1 | jongIndex == 20) {
                                                    switch (jongIndex) {
                                                        case 0:
                                                            jungIndex += 1;
                                                            break;
                                                        case 1:
                                                            jungIndex += 2;
                                                            break;
                                                        case 20:
                                                            jungIndex += 3;
                                                            break;
                                                    }
                                                    int combine = koreanCombine(choIndex, jungIndex, 0);
                                                    inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                                    inputString += (char) combine;
                                                } else {
                                                    inputString = editView.getText() + params[0];
                                                }
                                            } else if (jungIndex == 13) {
                                                if (jongIndex == 4 | jongIndex == 5 | jongIndex == 20) {
                                                    switch (jongIndex) {
                                                        case 4:
                                                            jungIndex += 1;
                                                            break;
                                                        case 5:
                                                            jungIndex += 2;
                                                            break;
                                                        case 20:
                                                            jungIndex += 3;
                                                            break;
                                                    }
                                                    int combine = koreanCombine(choIndex, jungIndex, 0);
                                                    inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                                    inputString += (char) combine;
                                                } else {
                                                    inputString = editView.getText() + params[0];
                                                }
                                            } else if (jungIndex == 18) {
                                                if (jongIndex == 20) {
                                                    jungIndex += 1;
                                                    int combine = koreanCombine(choIndex, jungIndex, 0);
                                                    inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                                    inputString += (char) combine;
                                                } else {
                                                    inputString = editView.getText() + params[0];
                                                }
                                            } else {
                                                inputString = editView.getText() + params[0];
                                            }
                                        } else {
                                            inputString = editView.getText() + params[0];
                                        }
                                    }
                                }
                            } else {
                                int choIndex = getIndexOf(cho, targetChar);
                                if (choIndex != -1) {
                                    int jungIndex = getIndexOf(jung, params[0].charAt(0));
                                    int combine = koreanCombine(choIndex, jungIndex, 0);
                                    inputString = String.valueOf(editView.getText().subSequence(0, editView.getText().length() - 1));
                                    inputString += (char) combine;
                                } else {
                                    inputString = editView.getText() + params[0];
                                }
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
            case R.id.shift:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isShifted) {
                            isShifted = false;
                        } else {
                            isShifted = true;
                        }
                        keyboardView.setShifted(isShifted);
                        keyboardView.invalidate();
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
        JSONObject obj = new JSONObject();
        try {
            String type = "word";
            if (editView.getText().length() == 1 && getIndexOf(cho, editView.getText().charAt(0)) != -1) {
                type = "character";
            }
            obj.put("data", input);
            obj.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("request", obj);
    }

    public void setSuggestedList(Object object) {

        suggestedList = new ArrayList<List<String>>(
                Arrays.asList(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>())
        );
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            Iterator<String> jsonKeys = jsonObject.keys();
            int index = 0;
            while(jsonKeys.hasNext()) {
                String list = jsonObject.get(jsonKeys.next()).toString();
                String[] array = list.substring(1, list.length()-1).split(",");
                for (String item : array) {
                    suggestedList.get(index).add(item.substring(1, item.length()-1));
                }
                index++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, suggestedList.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < suggestViewList.size(); i++) {
                    TextView suggestView = suggestViewList.get(i);
                    if (suggestedList.get(i).size() > 1) {
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
