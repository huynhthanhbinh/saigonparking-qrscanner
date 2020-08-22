package wtf.saigonparking.qrscanner.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bht.saigonparking.api.grpc.contact.BookingFinishContent;
import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;
import com.google.zxing.Result;

import lombok.Getter;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

public final class MainActivity extends BaseSaigonParkingActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    @Getter
    private Button btnLogout;
    @Getter
    private TextView txtMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, 1);

        txtMode = findViewById(R.id.txtMode);
        txtMode.setText(applicationContext.isLoggedIn() ? "Finish Booking Mode" : "Login Mode");

        btnLogout = findViewById(R.id.logoutButton);
        btnLogout.setOnClickListener(this::onLogout);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @SuppressWarnings("unused")
    private void onLogout(@NonNull View view) {
        applicationContext.setLoggedIn(false);
        applicationContext.closeWebSocketConnection();

        txtMode.setText("Login Mode");
        btnLogout.setVisibility(View.INVISIBLE);

        changeActivity(LoginActivity.class, false);
        Toast.makeText(applicationContext.getCurrentActivity(),
                "Connection closed !", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onContinueQRScan() {
        /* release camera for next scan */
        mScannerView.resumeCameraPreview(this);
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

    private boolean isContentAJSONWebToken(String content) {
        return content.matches("^[^.]+\\.[^.]+\\.[^.]+$");
    }

    private boolean isContentAUUID(String content) {
        return content.matches("^[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}$");
    }

    @Override
    public void handleResult(Result rawResult) {
        String content = rawResult.getText();
//        Toast.makeText(this, "token: " + content, Toast.LENGTH_LONG).show();

        if (!applicationContext.isLoggedIn()) {
            if (isContentAJSONWebToken(content)) { /* login mode, content is a JWT */
                applicationContext.createWebSocketConnection(content);
            } else { /* login mode, content is not a JWT */
                Toast.makeText(this, "Content is not a JWT. Please scan again! " + content, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (isContentAUUID(content)) { /* finish mode, content is a UUID */
                SaigonParkingMessage message = SaigonParkingMessage.newBuilder()
                        .setClassification(SaigonParkingMessage.Classification.PARKING_LOT_MESSAGE)
                        .setType(SaigonParkingMessage.Type.BOOKING_FINISH)
                        .setReceiverId(0)
                        .setContent(BookingFinishContent.newBuilder()
                                .setBookingId(content)
                                .build()
                                .toByteString())
                        .build();
                sendWebSocketBinaryMessage(message);
            } else { /* finish mode, content is not a UUID */
                Toast.makeText(this, "Content is not a UUID. Please scan again! " + content, Toast.LENGTH_SHORT).show();
            }
        }
        onContinueQRScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!applicationContext.isLoggedIn()) {
            changeActivity(LoginActivity.class, false);
        }
    }
}