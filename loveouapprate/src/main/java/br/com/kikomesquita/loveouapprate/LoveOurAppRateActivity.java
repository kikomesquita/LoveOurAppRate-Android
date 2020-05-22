package br.com.kikomesquita.loveouapprate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class LoveOurAppRateActivity extends AppCompatActivity {

    Button btnRate;
    TextView tvNoRate;
    String appPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.love_our_app_rate);
        btnRate = (Button) findViewById(R.id.btnRate);
        tvNoRate = (TextView) findViewById(R.id.noRate);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            appPackageName = extras.getString("appPackageName");
        }

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate(appPackageName);
            }
        });

        tvNoRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoveOurAppRate.clearSharedPreferences(getApplicationContext());
                LoveOurAppRate.storeAskLaterDate(getApplicationContext());
                finish();
            }
        });


    }

    public void rate(String appPackageName){
        String url = "market://details?id=" + appPackageName;

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        LoveOurAppRate.setOptOut(this, true);
    }


}
