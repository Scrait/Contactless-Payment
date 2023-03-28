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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Date;

import ru.scrait.contactlesspayment.MainActivity;
import ru.scrait.contactlesspayment.databinding.FragmentNotificationsBinding;
import ru.scrait.contactlesspayment.ui.home.HomeFragment;
import ru.scrait.contactlesspayment.utils.DevUtils;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    public static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static String my_code_text;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        Date date = new Date();
        if (currentUser != null) {
            creatCode(my_code_text);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}