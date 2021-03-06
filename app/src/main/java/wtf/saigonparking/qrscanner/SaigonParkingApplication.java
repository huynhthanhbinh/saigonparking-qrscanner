package wtf.saigonparking.qrscanner;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;
import wtf.saigonparking.qrscanner.websocket.SaigonParkingWebSocketListener;

import static wtf.saigonparking.qrscanner.websocket.SaigonParkingWebSocketListener.NORMAL_CLOSURE_STATUS;

/**
 * Entry point of android application
 * Will be run first before all Activity classes
 *
 * @author bht
 */
@Getter
public final class SaigonParkingApplication extends Application {

    private static final String SERVER_PATH =
            BuildConfig.WEBSOCKET_PREFIX +
                    BuildConfig.GATEWAY_HOST + ":" +
                    BuildConfig.GATEWAY_HTTP_PORT +
                    "/contact/qrscanner";

    @Setter
    private BaseSaigonParkingActivity currentActivity = null;
    @Setter
    private boolean isLoggedIn = false;

    private static Context applicationContext;
    private WebSocket webSocket;

    @Override
    public void onCreate() {
        Log.d("BachMap", "onCreate: wtf.saigonparking.qrscanner.SaigonParkingApplication");
        super.onCreate();
        Locale.setDefault(Locale.US);
        applicationContext = getApplicationContext();
    }

    public final void createWebSocketConnection(@NonNull String token) {
        if (webSocket == null) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(SERVER_PATH)
                    .addHeader("Authorization", token)
                    .build();

            SaigonParkingWebSocketListener listener = new SaigonParkingWebSocketListener(this);
            webSocket = client.newWebSocket(request, listener);
        }
    }

    public final void closeWebSocketConnection() {
        if (webSocket != null) {
            try {
                webSocket.close(NORMAL_CLOSURE_STATUS, null);
            } catch (Exception exception) {
                Log.d("TaiSmile", "Error closeWebSocketConnection: " + exception.getClass().getSimpleName());
            }
            webSocket = null;
        }
    }
}