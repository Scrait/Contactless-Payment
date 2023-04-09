package ru.scrait.contactlesspayment.ui.notifications;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import ru.scrait.contactlesspayment.Dialog.ExampleDialog;
import ru.scrait.contactlesspayment.R;
import ru.scrait.contactlesspayment.RecyclerHistoryInterface;
import ru.scrait.contactlesspayment.adapters.RecyclerViewHistoryAdapter;
import ru.scrait.contactlesspayment.databinding.FragmentNotificationsBinding;
import ru.scrait.contactlesspayment.models.Ticket;
import ru.scrait.contactlesspayment.utils.DevUtils;

public class NotificationsFragment extends Fragment implements RecyclerHistoryInterface {

    private FragmentNotificationsBinding binding;
    public static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static String my_code_text;

    ArrayList<String> listDates = new ArrayList<String>();

    ArrayList<Integer> listAmounts = new ArrayList<Integer>();
    ArrayList<Integer> listSums = new ArrayList<Integer>();

    RecyclerViewHistoryAdapter recyclerViewHistoryAdapter;
    RecyclerView rvHistory;
    ImageView imgQr;

    String qrText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvHistory = binding.rvHistory;

        recyclerViewHistoryAdapter = new RecyclerViewHistoryAdapter(getContext(), listDates, listSums, listAmounts, this);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(recyclerViewHistoryAdapter);

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        if (currentUser != null) {
            //creatCode(my_code_text);
            getTransactionHistory();
        }

        imgQr =  getActivity().findViewById(R.id.imageQr);

        return root;
    }

    public static void getQrText(String text) {
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

    @Override
    public void OnItemClick(int position) {
        String date = ((TextView) rvHistory.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tvRecyclerViewDate)).getText().toString();
        String amount = ((TextView) rvHistory.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tvRecyclerViewAmount)).getText().toString();
        String sum = ((TextView) rvHistory.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.tvRecyclerViewSum)).getText().toString();
        qrText = date + "; " + amount + "; "+ sum + ";";
        Log.i("ААААААААААААААААА СУКА", qrText);
        openDialog();
    }

    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.setText(qrText);
        exampleDialog.show(getParentFragmentManager(), "Qr-code");
    }
}