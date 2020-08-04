package wtf.saigonparking.qrscanner.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

public final class MainActivity extends BaseSaigonParkingActivity implements ZXingScannerView.ResultHandler {

    @BindView(R.id.lightButton)
    ImageView flashImageView;

    private ZXingScannerView mScannerView;
    private boolean flashState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {


        // show custom alert dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        View v = Objects.requireNonNull(dialog.getWindow()).getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        ImageView img = dialog.findViewById(R.id.imgOfDialog);
        img.setImageResource(R.drawable.ic_done_gr);

        Log.d("TaiSmile", "accessToken: " + rawResult.getText());

        dialog.setOnCancelListener(dialog1 ->
                mScannerView.resumeCameraPreview(MainActivity.this));
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @OnClick
    void mainActivityOnClickEvents(View v) {

        switch (v.getId()) {
//            case R.id.historyButton:
//                i = new Intent(this, HistoryActivity.class);
//                startActivity(i);
//                break;
            case R.id.lightButton:
                if (flashState == false) {
                    v.setBackgroundResource(R.drawable.ic_flash_off);
                    Toast.makeText(getApplicationContext(), "Flashlight turned on", Toast.LENGTH_SHORT).show();
                    mScannerView.setFlash(true);
                    flashState = true;
                } else if (flashState) {
                    v.setBackgroundResource(R.drawable.ic_flash_on);
                    Toast.makeText(getApplicationContext(), "Flashlight turned off", Toast.LENGTH_SHORT).show();
                    mScannerView.setFlash(false);
                    flashState = false;
                }
                break;
        }
    }
}