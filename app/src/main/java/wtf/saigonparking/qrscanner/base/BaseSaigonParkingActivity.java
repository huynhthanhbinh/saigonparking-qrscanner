package wtf.saigonparking.qrscanner.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;

import lombok.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;
import wtf.saigonparking.qrscanner.SaigonParkingApplication;

/**
 * customize Activity Class for Saigon Parking App only
 *
 * @author bht
 */
public abstract class BaseSaigonParkingActivity extends AppCompatActivity {

    /**
     * websocket will be private
     * so as to any child of this base class cannot call websocket directly !!!!
     * if any child class want to use websocket to send message
     * they must call method inherit from their parent
     * for example sendMessage: sendWebSocketBinaryMessage/TextMessage(msg)
     */
    private WebSocket webSocket;
    protected SaigonParkingApplication applicationContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = ((SaigonParkingApplication) getApplicationContext());
        applicationContext.setCurrentActivity(this);
        webSocket = applicationContext.getWebSocket();
    }

    public final void changeActivity(Class<? extends BaseSaigonParkingActivity> nextActivityClass, boolean canBack) {
        startActivity(new Intent(this, nextActivityClass));
        if (!canBack) {
            finish();
        }
    }

    protected final void sendWebSocketBinaryMessage(@NonNull SaigonParkingMessage message) {
        if (webSocket == null) {
            /* TODO: please scan QR code to create new socket connection to server */
            String accessToken = "dang-duc-tai";
            ((SaigonParkingApplication) getApplicationContext()).initWebSocketConnection(accessToken);
            webSocket = ((SaigonParkingApplication) getApplicationContext()).getWebSocket();
        }
        webSocket.send(new ByteString(message.toByteArray()));
    }
}