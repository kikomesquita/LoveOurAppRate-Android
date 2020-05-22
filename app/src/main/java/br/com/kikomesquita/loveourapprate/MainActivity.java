package br.com.kikomesquita.loveourapprate;

import androidx.appcompat.app.AppCompatActivity;
import br.com.kikomesquita.loveouapprate.LoveOurAppRate;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoveOurAppRate.Config config = new LoveOurAppRate.Config(1,2);
        LoveOurAppRate.init(config);
        // Monitor launch times and interval from installation
        LoveOurAppRate.onCreate(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        LoveOurAppRate.showRateRequestIfNeeded(this);


    }
}
