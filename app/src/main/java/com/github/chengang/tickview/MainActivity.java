package com.github.chengang.tickview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.chengang.library.TickView;

public class MainActivity extends AppCompatActivity {

    TickView tickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tickView = (TickView) findViewById(R.id.tick_view);
        tickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickView.click();
            }
        });
    }
}
