package wtf.saigonparking.qrscanner.base;

import android.app.Service;

import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;

import lombok.NonNull;
import okhttp3.WebSocket;
import okio.ByteString;
import wtf.saigonparking.qrscanner.SaigonParkingApplication;

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

    protected void sendWebSocketBinaryMessage(@NonNull SaigonParkingMessage message) {
        if (webSocket == null) {
            /* TODO: please scan QR code to create new socket connection to server */
            String accessToken = "dang-duc-tai";
            ((SaigonParkingApplication) getApplicationContext()).createWebSocketConnection(accessToken);
            webSocket = ((SaigonParkingApplication) getApplicationContext()).getWebSocket();
        }
        webSocket.send(new ByteString(message.toByteArray()));
    }
}