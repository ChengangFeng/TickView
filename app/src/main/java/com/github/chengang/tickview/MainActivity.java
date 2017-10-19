package com.github.chengang.tickview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.chengang.library.TickView;

public class MainActivity extends AppCompatActivity {

    TickView tickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tickView = (TickView) findViewById(R.id.tick_view);
        tickView.setOnCheckedChangeListener(new TickView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TickView tickView, boolean isCheck) {
                Toast.makeText(MainActivity.this, isCheck + "", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.check_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickView.setChecked(true);
            }
        });
        findViewById(R.id.uncheck_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickView.setChecked(false);
            }
        });
    }
}
