package ru.scrait.contactlesspayment.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Date;

import ru.scrait.contactlesspayment.MainActivity;
import ru.scrait.contactlesspayment.adapters.RecyclerViewHistoryAdapter;
import ru.scrait.contactlesspayment.databinding.FragmentNotificationsBinding;
import ru.scrait.contactlesspayment.models.Ticket;
import ru.scrait.contactlesspayment.ui.home.HomeFragment;
import ru.scrait.contactlesspayment.utils.DevUtils;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    public static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static String my_code_text;

    ArrayList<String> listDates = new ArrayList<String>();

    ArrayList<Integer> listAmounts = new ArrayList<Integer>();
    ArrayList<Integer> listSums = new ArrayList<Integer>();

    RecyclerViewHistoryAdapter recyclerViewHistoryAdapter;
    RecyclerView rvHistory;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvHistory = binding.rvHistory;

        recyclerViewHistoryAdapter = new RecyclerViewHistoryAdapter(getContext(), listDates, listSums, listAmounts);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(recyclerViewHistoryAdapter);

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        if (currentUser != null) {
            //creatCode(my_code_text);
            getTransactionHistory();
        }

        return root;
    }

    private void creatCode(String my_code_text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(my_code_text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) binding.imgResultQr).setImageBitmap(bmp);
            if (DevUtils.isCodding) {
                Toast.makeText(getActivity().getApplicationContext(), "успешно", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            e.printStackTrace();
            if (DevUtils.isCodding) {
                Toast.makeText(getActivity().getApplicationContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void setMy_code_text(String text) {
        my_code_text = text;
    }

    private void getTransactionHistory() {

        DatabaseReference databaseReferenceTransactions = FirebaseDatabase.getInstance().getReference("User").child(currentUser.getUid()).child("Transactions");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listAmounts.clear();
                listSums.clear();
                listDates.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Ticket ticket = dataSnapshot.getValue(Ticket.class);

                    listAmounts.add(0, ticket.amount);
                    listSums.add(0, ticket.sum);
                    listDates.add(0, ticket.date);
                }

                recyclerViewHistoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

        databaseReferenceTransactions.addValueEventListener(valueEventListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}