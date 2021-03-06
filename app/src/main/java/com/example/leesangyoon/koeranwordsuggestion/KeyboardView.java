package com.example.leesangyoon.koeranwordsuggestion;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class KeyboardView extends View {

    char[] keyboardCharList;

    char[] keyboardCharListBase = {
            'ㅂ', 'ㅈ', 'ㄷ', 'ㄱ', 'ㅅ', 'ㅛ', 'ㅕ', 'ㅑ', 'ㅐ', 'ㅔ',
            'ㅁ', 'ㄴ', 'ㅇ', 'ㄹ', 'ㅎ', 'ㅗ', 'ㅓ', 'ㅏ', 'ㅣ',
            'ㅋ', 'ㅌ', 'ㅊ', 'ㅍ', 'ㅠ', 'ㅜ', 'ㅡ'
    };

    char[] keyboardCharListShift = {
            'ㅃ', 'ㅉ', 'ㄸ', 'ㄲ', 'ㅆ', 'ㅛ', 'ㅕ', 'ㅑ', 'ㅒ', 'ㅖ',
            'ㅁ', 'ㄴ', 'ㅇ', 'ㄹ', 'ㅎ', 'ㅗ', 'ㅓ', 'ㅏ', 'ㅣ',
            'ㅋ', 'ㅌ', 'ㅊ', 'ㅍ', 'ㅠ', 'ㅜ', 'ㅡ'
    };

    char[][] keyboardChar;

    final char[][] keyboardCharBase = {
            {'ㅂ', 'ㅈ', 'ㄷ', 'ㄱ', 'ㅅ', 'ㅛ', 'ㅕ', 'ㅑ', 'ㅐ', 'ㅔ'},
            {'ㅁ', 'ㄴ', 'ㅇ', 'ㄹ', 'ㅎ', 'ㅗ', 'ㅓ', 'ㅏ', 'ㅣ'},
            {'ㅋ', 'ㅌ', 'ㅊ', 'ㅍ', 'ㅠ', 'ㅜ', 'ㅡ'}
    };

    final char[][] keyboardCharShift = {
            {'ㅃ', 'ㅉ', 'ㄸ', 'ㄲ', 'ㅆ', 'ㅛ', 'ㅕ', 'ㅑ', 'ㅒ', 'ㅖ'},
            {'ㅁ', 'ㄴ', 'ㅇ', 'ㄹ', 'ㅎ', 'ㅗ', 'ㅓ', 'ㅏ', 'ㅣ'},
            {'ㅋ', 'ㅌ', 'ㅊ', 'ㅍ', 'ㅠ', 'ㅜ', 'ㅡ'}
    };

    List<String> keyboardCharPos;
    double keyWidthRef;
    double keyHeightRef;

    Paint keyboardPaint;

    public KeyboardView(Context context) {
        super(context);
        init(null, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public String getKey(double x, double y) {
        int id = -1;

        for (int i = 0; i < keyboardCharPos.size(); i++) {
            String pos = keyboardCharPos.get(i);
            double posX = Double.parseDouble(pos.split("-")[0]);
            double posY = Double.parseDouble(pos.split("-")[1]);
            if ((Math.abs(posX - x) < keyWidthRef) && (Math.abs(posY - y) < keyHeightRef)) {
                id = i;
                break;
            }
        }

        if (id == -1) {
            return ".";
        }

        return String.valueOf(keyboardCharList[id]);
    }

    public String getKeyPos(String a) {
        return keyboardCharPos.get(new String(keyboardCharList).indexOf(a));
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.KeyboardView, defStyle, 0);

        a.recycle();

        keyboardPaint = new Paint();
        keyboardPaint.setAntiAlias(true);
        keyboardPaint.setTextSize(35);
        keyboardPaint.setTextAlign(Paint.Align.CENTER);
        keyboardPaint.setFakeBoldText(true);

        keyboardCharPos = new ArrayList<>();
        keyHeightRef = -1.0;
        keyWidthRef = -1.0;

        keyboardChar = keyboardCharBase;
        keyboardCharList = keyboardCharListBase;
    }

    public void setShifted(boolean isShifted) {
        if (isShifted) {
            keyboardChar = keyboardCharShift;
            keyboardCharList = keyboardCharListShift;
        } else {
            keyboardChar = keyboardCharBase;
            keyboardCharList = keyboardCharListBase;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double viewWidth = getWidth();
        double viewHeight = getHeight();

        double keyWidth = viewWidth / 10.5;
        double keyHeight = viewHeight / 3.5;

        if (keyWidthRef == -1.0) {
            keyWidthRef = keyWidth * 0.5;
        }
        if (keyHeightRef == -1.0) {
            keyHeightRef = keyHeight * 0.5;
        }

        double keyboardPaddingLeft = (viewWidth - (10 * keyWidth)) * 0.5;
        double keyboardPaddingTop = (viewHeight - (3 * keyHeight)) * 0.5;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                if (j < keyboardChar[i].length) {
                    String content = String.valueOf(keyboardChar[i][j]);

                    float leftPadding = (float) (keyboardPaddingLeft + j * keyWidth + (keyWidth * 0.5));
                    float topPadding = (float) (keyboardPaddingTop + i * keyHeight + (keyHeight * 0.5));
                    if (i == 1) {
                        leftPadding += keyWidth * 0.5;
                    } else if (i == 2) {
                        leftPadding += keyWidth * 1.5;
                    }

                    if (keyboardCharPos.size() != 26) {
                        keyboardCharPos.add(leftPadding + "-" + topPadding);
                    }

                    canvas.drawText(content,
                            leftPadding,
                            topPadding,
                            keyboardPaint);
                }
            }
        }

    }

}
