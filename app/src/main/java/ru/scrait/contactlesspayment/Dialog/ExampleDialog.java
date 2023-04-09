package ru.scrait.contactlesspayment.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import ru.scrait.contactlesspayment.R;
import ru.scrait.contactlesspayment.utils.DevUtils;

public class ExampleDialog extends AppCompatDialogFragment {
    ImageView imgQr;

    public static void setText(String text) {
        ExampleDialog.text = text;
    }

    private static String text;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout2 = layoutInflater.inflate(R.layout.dialog_item, null);
        dialog.setContentView(layout2);

        imgQr = (ImageView) layout2.findViewById(R.id.imageQr);
        creatCode();
        return dialog;
    }

    private void creatCode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imgQr.setImageBitmap(bmp);
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



//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_item, null);
//
//
//        builder.setView(view)
//                .setTitle("Qr-code")
//                .set;
//        return builder.create();
//    }
    }
