package com.cupster.blechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.concurrent.PriorityBlockingQueue;

public class MainActivity extends AppCompatActivity {

    Button skip;
    int clock = 1;
    MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式通知栏--- 没有toolbar 时，好用.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);

        skip = findViewById(R.id.welcome_skip);
        skip.setOnClickListener(btn_listener);
        skip.setText(getResources().getString(R.string.skip) + clock);

        handler = new MyHandler(this);

        setAdImg();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //广告页倒计时
        handler.postDelayed(skipRun, 1000);
    }

    Runnable skipRun = new Runnable() {
        @Override
        public void run() {
            clock--;
            skip.setText(getResources().getString(R.string.skip) + clock);
            if (clock == 0) {
                skipActivity();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> activityWeakReference;

        public MyHandler(MainActivity activity) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {

            }
        }
    }

    private View.OnClickListener btn_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.welcome_skip:
                    handler.removeCallbacks(skipRun);
                    skipActivity();
                    break;
                default:
                    break;
            }
        }
    };

    private void setAdImg() {
        ImageView imageView = findViewById(R.id.welcome_page);
        SharedPreferences sp = getSharedPreferences("configs", 0);
//        SharedPreferences.Editor editor = sp.edit();
        //https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1533547129413&di=3895eea02c9e01512157348698d9d38a&imgtype=0&src=http%3A%2F%2Ftupian.aladd.net%2Fphoto3%2F632.jpg
        String url = sp.getString("ad_url"
                , "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534141779&di=508c8d314dfb9310605533031afb33ca&imgtype=jpg&er=1&src=http%3A%2F%2Fa3.topitme.com%2Ff%2F5b%2Fc4%2F1128664504f43c45bfo.jpg");
        Glide.with(this)
                .load(url)
                .into(imageView);
    }

    /**
     * 跳转，并自杀
     */
    private void skipActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
        finish();
//        onDestroy();
    }


}
