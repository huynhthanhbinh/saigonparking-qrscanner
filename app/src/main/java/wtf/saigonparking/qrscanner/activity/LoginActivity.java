package wtf.saigonparking.qrscanner.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bht.saigonparking.api.grpc.auth.AuthServiceGrpc;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

@SuppressLint("all")
@SuppressWarnings("all")
@Getter
public final class LoginActivity extends BaseSaigonParkingActivity {

    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    @BindView(R.id.btn_login)
    protected Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (applicationContext.isLoggedIn()) {
            changeActivity(MainActivity.class, false);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.class, false);
            }
        });
    }
}