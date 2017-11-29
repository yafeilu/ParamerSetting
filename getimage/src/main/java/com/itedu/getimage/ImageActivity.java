package com.itedu.getimage;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ImageActivity extends AppCompatActivity {

    private ImageView ivContent;//添加了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivContent = (ImageView) findViewById(R.id.iv_content);
        findViewById(R.id.btn_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.schedule(task, 0, 2300);
            }
        });

    }
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    showAnimation();
                }
            });
        }
    };
    private void showAnimation() {
        Animator animator = AnimatorInflater.loadAnimator(ImageActivity.this, R.animator.xinshou);
        animator.setTarget(ivContent);
        animator.start();
    }
}
