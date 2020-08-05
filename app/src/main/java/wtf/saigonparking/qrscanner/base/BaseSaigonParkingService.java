package wtf.saigonparking.qrscanner.base;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;

import java.util.Objects;

import lombok.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.SaigonParkingApplication;
import wtf.saigonparking.qrscanner.activity.LoginActivity;

/**
 * customize Service Class for Saigon Parking App only
 *
 * @author bht
 */
public abstract class BaseSaigonParkingService extends Service {

    /**
     * websocket will be private
     * so as to any child of this base class
     * cannot call websocket directly !!!!
     * <p>
     * if any child class want to use websocket to send message
     * they must call method inherit from their parent
     * for example sendMessage: sendWebSocketBinaryMessage/TextMessage(msg)
     */
    private WebSocket webSocket;
    protected SaigonParkingApplication applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = ((SaigonParkingApplication) getApplicationContext());
        webSocket = applicationContext.getWebSocket();
    }

    public final void changeActivity(Class<? extends BaseSaigonParkingActivity> nextActivityClass, boolean canBack) {
        if (applicationContext.getCurrentActivity() != null) {
            startActivity(new Intent(applicationContext.getCurrentActivity(), nextActivityClass));
            if (!canBack) {
                applicationContext.getCurrentActivity().finish();
            }
        }
    }

    protected void sendWebSocketBinaryMessage(@NonNull SaigonParkingMessage message) {
        try {
            webSocket.send(new ByteString(message.toByteArray()));

        } catch (Exception exception) {

            applicationContext.setLoggedIn(false);
            applicationContext.closeWebSocketConnection();

            /* TODO: please scan QR code to create new socket connection to server */
            /* Show dialog to force user scan QR to login again */
            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog);

            View view = Objects.requireNonNull(dialog.getWindow()).getDecorView();
            view.setBackgroundResource(android.R.color.transparent);

            TextView textView = dialog.findViewById(R.id.txtTextView);
            textView.setText("Error occurred. Try to login again !");

            ImageView img = dialog.findViewById(R.id.imgOfDialog);
            img.setImageResource(R.drawable.ic_done_gr);
            dialog.show();

            dialog.setOnDismissListener(dialogInterface ->
                    changeActivity(LoginActivity.class, false));
        }
    }
}