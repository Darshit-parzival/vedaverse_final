package com.vedaverse.vedaverse_final.utils;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtil {

    public static void showToast(Context cn, String message)
    {
        Toast.makeText(cn,message,Toast.LENGTH_LONG).show();
    }
}
