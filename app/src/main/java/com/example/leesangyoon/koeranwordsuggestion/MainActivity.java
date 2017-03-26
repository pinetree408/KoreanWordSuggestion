package com.example.leesangyoon.koeranwordsuggestion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editView;
    private TextView inputView;
    private KeyboardView keyboardView;
    private TextView enterView;
    private TextView spaceView;
    private TextView deleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editView = (EditText) findViewById(R.id.edit);

        inputView = (TextView) findViewById(R.id.input);

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
                        editView.setText(editView.getText().subSequence(0, editView.getText().length() - 1));
                        editView.setSelection(editView.getText().length());
                    }
                }
                return true;
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
