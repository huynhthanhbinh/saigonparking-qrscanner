package wtf.saigonparking.qrscanner.websocket;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bht.saigonparking.api.grpc.contact.ErrorContent;
import com.bht.saigonparking.api.grpc.contact.NotificationContent;
import com.bht.saigonparking.api.grpc.contact.SaigonParkingMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import wtf.saigonparking.qrscanner.R;
import wtf.saigonparking.qrscanner.SaigonParkingApplication;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

@RequiredArgsConstructor
public final class SaigonParkingWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String CONNECTION_ESTABLISHED_NOTIFICATION
            = "Connection to Contact service established !";

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
                        Log.d("TaiSmile", "Notification content: " + notificationContent.getNotification());

                        if (CONNECTION_ESTABLISHED_NOTIFICATION.equals(notificationContent.getNotification())) {

                            /* Create socket connection successfully */
                            applicationContext.setLoggedIn(true);

                            TextView textView = new TextView(currentActivity);
                            textView.setText("Connection established");

                            Dialog dialog = new Dialog(currentActivity);
                            dialog.setContentView(textView);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.custom_dialog);

                            View v = Objects.requireNonNull(dialog.getWindow()).getDecorView();
                            v.setBackgroundResource(android.R.color.transparent);

                            ImageView img = dialog.findViewById(R.id.imgOfDialog);
                            img.setImageResource(R.drawable.ic_done_gr);
                            dialog.show();
                        }
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