package com.github.chengang.tickview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.chengang.library.TickView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    TickView tickView;
    TickView tickViewAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tickView = (TickView) findViewById(R.id.tick_view);
        tickViewAccent = (TickView) findViewById(R.id.tick_view_accent);
        tickView.setOnCheckedChangeListener(new TickView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TickView tickView, boolean isCheck) {
                Toast.makeText(MainActivity.this, isCheck + "", Toast.LENGTH_SHORT).show();
            }
        });
        tickViewAccent.setOnCheckedChangeListener(new TickView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TickView tickView, boolean isCheck) {
                Toast.makeText(MainActivity.this, isCheck + "", Toast.LENGTH_SHORT).show();
            }
        });
        tickViewAccent.addAnimatorListener(new TickView.TickAnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(TickView tickView) {
                super.onAnimationStart(tickView);
                Log.i(TAG,"tickView animation start");
            }

            @Override
            public void onAnimationEnd(TickView tickView) {
                super.onAnimationEnd(tickView);
                Log.i(TAG,"tickView animation end");
            }
        });
        findViewById(R.id.check_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickView.setChecked(true);
                tickViewAccent.setChecked(true);
            }
        });
        findViewById(R.id.uncheck_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickView.setChecked(false);
                tickViewAccent.setChecked(false);
            }
        });
    }
}
