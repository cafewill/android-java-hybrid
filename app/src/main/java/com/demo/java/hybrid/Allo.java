package com.demo.java.hybrid;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Allo
{
    public static final String CUBE = "cube";
    public static final boolean CUBE_DEBUG = true;

    public static final String CUBE_LINK = "link";
    public static final String CUBE_TOKEN = "token";

    public static final String CUBE_CHARSET = "UTF-8";
    public static final String CUBE_SITE = "https://github.com/cafewill";

    public static void i (final String message) {
        if (CUBE_DEBUG) Log.i (CUBE, message);
    }
    public static void t (final Context context, String message) {
        if (CUBE_DEBUG) Toast.makeText (context, message, Toast.LENGTH_SHORT).show ();
    }
}
