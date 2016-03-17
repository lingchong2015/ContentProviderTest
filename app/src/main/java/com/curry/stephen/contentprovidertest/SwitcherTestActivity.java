package com.curry.stephen.contentprovidertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by LingChong on 2016/3/17 0017.
 */
public class SwitcherTestActivity extends AppCompatActivity {
    private TextSwitcher mTextSwitcher;
    private int mTextIndex = -1;

    private static final String[] mTexts = {
            "one",
            "two",
            "three"
    };

    private static final String TAG = SwitcherTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_switcher_test);

        findViewById(R.id.view_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewStub viewStub = (ViewStub) findViewById(R.id.view_stub);

//                View buttonClick = viewStub.inflate();
                // The following two line code can be instead by single line code above.
                viewStub.setVisibility(View.VISIBLE);
                View buttonClick = findViewById(R.id.button_click);

                buttonClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTextSwitcher.setText(mTexts[mTextIndex = (mTextIndex + 1) % mTexts.length]);
                    }
                });
            }
        });

        mTextSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        mTextSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mTextSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        // Must be called for add a view used to create two views between which the TextSwitcher will flip.
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(SwitcherTestActivity.this);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        });
    }
}
