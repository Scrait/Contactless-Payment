package ru.scrait.contactlesspayment.ui.dashboard;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<String> mTextBalance;
    private MutableLiveData<String> mTextEmail;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        mTextBalance = new MutableLiveData<>();
        mTextBalance.setValue("Error");
        mTextEmail = new MutableLiveData<>();
        mTextEmail.setValue("Error");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void update(int balance, String email) {
        mTextBalance.setValue("Баланс:\n" + balance + " руб");
        mTextEmail.setValue("Email:\n" + email);
    }

    public LiveData<String> getTextBalance() {
        return mTextBalance;
    }

    public LiveData<String> getTextEmail() {
        return mTextEmail;
    }
}