package wtf.saigonparking.qrscanner.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

public final class SplashScreenActivity extends BaseSaigonParkingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LoginActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withBeforeLogoText("SAIGON PARKING")
                .withLogo(R.mipmap.ic_launcher_round);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
