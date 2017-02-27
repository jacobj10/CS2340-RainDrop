package edu.gatech.robodroids.raindrop;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GetAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public GetAddressIntentService() {
        super("GetAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMsg = "";
        mReceiver = intent.getParcelableExtra("receiver");
        Location mLastLocation = intent.getParcelableExtra("location");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
        } catch (IOException e) {
            errorMsg = "Service not available";
        } catch (IllegalArgumentException e) {
            errorMsg = "Invalid latitude and longitude.";
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMsg.isEmpty()) {
                errorMsg = "No address found";
            }
            deliverResultToReciever(0, errorMsg);
        } else {
            Address mAddress = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            for(int i = 0; i < mAddress.getMaxAddressLineIndex(); i++) {
                addressFragments.add(mAddress.getAddressLine(i));
            }
            deliverResultToReciever(1, TextUtils.join(System.getProperty(
                    "line.separator"), addressFragments));
        }
    }

    private void deliverResultToReciever(int resultCode, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("result", msg);
        mReceiver.send(resultCode, bundle);
    }
}
