package wtf.saigonparking.qrscanner.websocket;

import android.util.Log;

import com.bht.saigonparking.api.grpc.contact.ErrorContent;
import com.bht.saigonparking.api.grpc.contact.NotificationContent;
import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import wtf.saigonparking.qrscanner.SaigonParkingApplication;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

@RequiredArgsConstructor
public final class SaigonParkingWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private final SaigonParkingApplication applicationContext;

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {

    }

    @SneakyThrows
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        BaseSaigonParkingActivity currentActivity = applicationContext.getCurrentActivity();
        currentActivity.runOnUiThread(() -> {
            try {
                SaigonParkingMessage message = SaigonParkingMessage.parseFrom(bytes.toByteArray());

                switch (message.getType()) {
                    case ERROR:
                        Log.d("BachMap", "Error booking again");
                        ErrorContent errorContent = ErrorContent.parseFrom(message.getContent());
                        String internalErrorCode = errorContent.getInternalErrorCode();
                        Log.d("BachMap", "Error booking again: " + internalErrorCode);
                        break;
                    case NOTIFICATION:
                        NotificationContent notificationContent = NotificationContent.parseFrom(message.getContent());
                        Log.d("BachMap", "Ket qua:" + notificationContent);
                        break;
                }

            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onClosing(WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {
        Log.d("BachMap", t.getMessage());
    }
}