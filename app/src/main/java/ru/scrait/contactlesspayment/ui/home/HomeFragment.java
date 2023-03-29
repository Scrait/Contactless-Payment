package ru.scrait.contactlesspayment.ui.home;

import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import ru.scrait.contactlesspayment.databinding.FragmentHomeBinding;
import ru.scrait.contactlesspayment.ui.dashboard.DashboardFragment;
import ru.scrait.contactlesspayment.ui.notifications.NotificationsFragment;
import ru.scrait.contactlesspayment.utils.DevUtils;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int balance;
    private int sum;
    protected static int fare;
    protected LocationManager locationManager;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fare = getFare(getActivity());
        if (fare == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Подключитесь к сети транспорта!", Toast.LENGTH_SHORT).show();
        }
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final TextView textSum = binding.sum;
        final TextView textX = binding.numberXOne;
        final Button pay = binding.pay;
        final Button map = binding.map;
        final EditText number = binding.numberOfTickets;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        homeViewModel.getTextSum().observe(getViewLifecycleOwner(), textSum::setText);
        homeViewModel.getTextX().observe(getViewLifecycleOwner(), textX::setText);
        sum = fare * Integer.parseInt(number.getText().toString());
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sum = fare * Integer.parseInt(number.getText().toString());
                homeViewModel.update(number.getText().toString(), sum);
            }
        });
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference MyRefetance = firebaseDatabase.getReference(DashboardFragment.USER_KEY);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        final DatabaseReference databaseReference = MyRefetance.child(uid).child("balance");
        Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Произошла ошибка", Toast.LENGTH_SHORT);
        if (currentUser != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    balance = snapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    toast1.show();
                }
            });
        } else {
            toast1.show();
        }
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sum = fare * Integer.parseInt(number.getText().toString());
                if (balance >= sum) {
                    balance -= sum;
                } else if (DevUtils.isCodding) {
                    balance -= sum;
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Произошла ошибка, недостаточно средств", Toast.LENGTH_SHORT);
                }
                databaseReference.setValue(balance);
                Date date = new Date();
                NotificationsFragment.setMy_code_text(currentUser.getEmail() + " : " + number.getText().toString() + "x" + " : " + myGetWifiName(getActivity()) + " : " + date);
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(showCurrentLocation()));
                startActivity(intent);
            }
        });
        return root;
    }

    public static String myGetWifiName(Context context) {
        String wifiname = null;
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            if (manager.isWifiEnabled()) {
                WifiInfo wifiInfo = manager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        wifiname = wifiInfo.getSSID();
                        if (wifiname.startsWith("\"") && wifiname.endsWith("\""))
                            wifiname = wifiname.substring(1, wifiname.length() - 1);
                        Log.d("WifiName", "WifiName is: " + wifiname);
                        return wifiname;
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    private int getFare(Context context) {
        final String wifiName = myGetWifiName(context).toLowerCase();
        if (wifiName.contains("bus")) {
            return 30;
        } else if (wifiName.contains("minibus")) {
            return 30;
        } else if (wifiName.contains("trolleybus")) {
            return 27;
        } else if (wifiName.contains("electrictrain")) {
            return 150;
        } else if (DevUtils.isCodding) {
            return 150;
        } else {
            return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected String showCurrentLocation() {

        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "geo:%1$s, %2$s",
                    location.getLatitude(), location.getLongitude()
            );
            if (DevUtils.isCodding) {
                Toast.makeText(getActivity().getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
            return message;
        } else {
            return "error";
        }
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "geo:%1$s, %2$s",
                    location.getLatitude(), location.getLongitude()
            );
            if (DevUtils.isCodding) {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            if (DevUtils.isCodding) Toast.makeText(getActivity().getApplicationContext(), "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            if (DevUtils.isCodding) Toast.makeText(getActivity().getApplicationContext(),
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            if (DevUtils.isCodding) Toast.makeText(getActivity().getApplicationContext(),
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }
}