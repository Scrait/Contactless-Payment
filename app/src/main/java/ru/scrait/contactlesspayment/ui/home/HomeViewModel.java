package ru.scrait.contactlesspayment.ui.home;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

import ru.scrait.contactlesspayment.R;
import ru.scrait.contactlesspayment.ui.notifications.NotificationsFragment;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mTextSum;
    private final MutableLiveData<String> mTextX;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        mTextSum = new MutableLiveData<>();
        mTextSum.setValue("Сумма к оплате\n" + HomeFragment.fare + " руб");
        mTextX = new MutableLiveData<>();
        mTextX.setValue("1x" + HomeFragment.fare);
        Date date = new Date();
        NotificationsFragment.setMy_code_text(NotificationsFragment.currentUser.getEmail() + " : " + 0 + "x" +  " : " + date);
    }

    public void update(String number, int sum) {
        mTextSum.setValue("Сумма к оплате\n" + sum + " руб");
        mTextX.setValue(number + "x" + HomeFragment.fare);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getTextSum() {
        return mTextSum;
    }

    public LiveData<String> getTextX() {
        return mTextX;
    }
}