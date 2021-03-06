package wtf.saigonparking.qrscanner.websocket;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bht.saigonparking.api.grpc.contact.BookingFinishContent;
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
import wtf.saigonparking.qrscanner.activity.MainActivity;
import wtf.saigonparking.qrscanner.base.BaseSaigonParkingActivity;

@RequiredArgsConstructor
public final class SaigonParkingWebSocketListener extends WebSocketListener {

    public static final int NORMAL_CLOSURE_STATUS = 1000;
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
                        ErrorContent errorContent = ErrorContent.parseFrom(message.getContent());
                        String internalErrorCode = errorContent.getInternalErrorCode();
                        Log.d("TaiSmile", "Error booking: " + internalErrorCode);
                        ((MainActivity) currentActivity).onContinueQRScan();
                        break;
                    case NOTIFICATION:
                        NotificationContent notificationContent = NotificationContent.parseFrom(message.getContent());
                        Log.d("TaiSmile", "Notification content: " + notificationContent.getNotification());

                        if (CONNECTION_ESTABLISHED_NOTIFICATION.equals(notificationContent.getNotification())) {

                            /* Create socket connection successfully */
                            applicationContext.setLoggedIn(true);
                            String currentMode = "Finish Booking Mode";
                            MainActivity mainActivity = (MainActivity) currentActivity;
                            mainActivity.getTxtMode().setText(currentMode);
                            mainActivity.getBtnLogout().setVisibility(View.VISIBLE);

                            String dialogContent = "Connection established !";

                            Dialog dialog = new Dialog(currentActivity);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.custom_dialog);

                            View view = Objects.requireNonNull(dialog.getWindow()).getDecorView();
                            view.setBackgroundResource(android.R.color.transparent);

                            TextView textView = dialog.findViewById(R.id.txtTextView);
                            textView.setText(dialogContent);

                            ImageView img = dialog.findViewById(R.id.imgOfDialog);
                            img.setImageResource(R.drawable.ic_done_gr);
                            dialog.show();

                            dialog.setOnDismissListener(dialogInterface -> mainActivity.onContinueQRScan());
                        }
                        break;
                    case BOOKING_FINISH:
                        BookingFinishContent bookingFinishContent = BookingFinishContent.parseFrom(message.getContent());
                        String dialogContent = "Finish booking with id: " + bookingFinishContent.getBookingId();

                        MainActivity mainActivity = (MainActivity) currentActivity;
                        Dialog dialog = new Dialog(mainActivity);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.custom_dialog);

                        View view = Objects.requireNonNull(dialog.getWindow()).getDecorView();
                        view.setBackgroundResource(android.R.color.transparent);

                        TextView textView = dialog.findViewById(R.id.txtTextView);
                        textView.setText(dialogContent);

                        ImageView img = dialog.findViewById(R.id.imgOfDialog);
                        img.setImageResource(R.drawable.ic_done_gr);
                        dialog.show();

                        dialog.setOnDismissListener(dialogInterface -> mainActivity.onContinueQRScan());
                        break;
                }
            } catch (Exception e) {
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
        Log.d("TaiSmile", t.toString());
    }
}