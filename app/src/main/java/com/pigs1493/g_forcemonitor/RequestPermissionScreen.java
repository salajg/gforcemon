package com.pigs1493.g_forcemonitor;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.OnClickListener;
import androidx.car.app.model.ParkedOnlyOnClickListener;
import androidx.car.app.model.Template;

import java.util.ArrayList;
import java.util.List;

public class RequestPermissionScreen extends Screen {

    /** Callback called when the location permission is granted. */
    public interface LocationPermissionCheckCallback {
        /** Callback called when the location permission is granted. */
        void onPermissionGranted();
    }

    LocationPermissionCheckCallback mLocationPermissionCheckCallback;

    public RequestPermissionScreen(@NonNull CarContext carContext, @NonNull LocationPermissionCheckCallback callback) {
        super(carContext);
        mLocationPermissionCheckCallback = callback;
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        List<String> permissions = new ArrayList<>();
        permissions.add(ACCESS_FINE_LOCATION);

        String message = "This app needs access to location in order to pull sensor data";

        OnClickListener listener = ParkedOnlyOnClickListener.create(() ->
                getCarContext().requestPermissions(
                        permissions,
                        (approved, rejected) -> {
                            CarToast.makeText(
                                    getCarContext(),
                                    String.format("Approved: %s Rejected: %s", approved, rejected),
                                    CarToast.LENGTH_LONG).show();
                            if (!approved.isEmpty()) {
                                mLocationPermissionCheckCallback.onPermissionGranted();
                                finish();
                            }
                        }));

        Action action = new Action.Builder()
                .setTitle("Grant Access")
                .setBackgroundColor(CarColor.GREEN)
                .setOnClickListener(listener)
                .build();

        return new MessageTemplate.Builder(message).addAction(action).setHeaderAction(
                Action.APP_ICON).build();
    }
}
