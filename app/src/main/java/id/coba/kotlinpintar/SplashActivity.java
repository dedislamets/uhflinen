package id.coba.kotlinpintar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle b = getIntent().getExtras();
                String link = "";
                if( b == null) {
                    link= "default";
                }else{
                    link = b.getString("link");
                }
                Intent resultIntent;

                if(link == "notifikasi"){
                    resultIntent = new Intent(getApplicationContext(), ActivityNotification.class);
                    resultIntent.putExtra("message", "");
                    startActivity(resultIntent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }



            }
        }, 2000);
    }
}
