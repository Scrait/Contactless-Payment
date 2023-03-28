package ru.scrait.contactlesspayment.ui.dashboard;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ru.scrait.contactlesspayment.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    public static final String USER_KEY = "User";
    private String uid;
    private String email;
    private int balance;
    private DatabaseReference databaseReference;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textDashboard = binding.textDashboard;
        final TextView textBalance = binding.textBalance;
        final TextView textEmail = binding.textEmail;
        final EditText replenish = binding.replenish;

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textDashboard::setText);
        dashboardViewModel.getTextBalance().observe(getViewLifecycleOwner(), textBalance::setText);
        dashboardViewModel.getTextEmail().observe(getViewLifecycleOwner(), textEmail::setText);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase DatabaseReferences = FirebaseDatabase.getInstance();
        final DatabaseReference MyRefetance = DatabaseReferences.getReference(USER_KEY);

        if (currentUser != null) {
            email = currentUser.getEmail();
            uid = currentUser.getUid();
            databaseReference = MyRefetance.child(uid).child("balance");
            replenish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    balance += Integer.parseInt(replenish.getText().toString());
                    databaseReference.setValue(balance);
                    dashboardViewModel.update(balance, email);
                }
            });
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    balance = snapshot.getValue(Integer.class);
                    dashboardViewModel.update(balance, email);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Произошла ошибка", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            });
        } else {
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Вы не авторизованы!", Toast.LENGTH_SHORT);
            toast1.show();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}