package ru.scrait.contactlesspayment.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        Date date = new Date();
        NotificationsFragment.setMy_code_text(NotificationsFragment.currentUser.getEmail() + " : " + 0 + "x" +  " : " + date);
    }

    public LiveData<String> getText() {
        return mText;
    }
}