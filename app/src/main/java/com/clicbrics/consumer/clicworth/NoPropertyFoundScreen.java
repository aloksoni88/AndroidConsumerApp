package com.clicbrics.consumer.clicworth;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.framework.activity.BaseActivity;

public class NoPropertyFoundScreen extends BaseActivity {


    private TextView change_criteria;
    private ImageButton backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nopropertyfoundscreen_layout);
        change_criteria=findViewById(R.id.change_criteria);
        backbutton=findViewById(R.id.backbutton);
        backbutton.setVisibility(View.VISIBLE);
        change_criteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ;

    }
}
