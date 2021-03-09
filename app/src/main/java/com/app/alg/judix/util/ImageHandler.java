package com.app.alg.judix.util;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by AndreBTS on 22/09/2015.
 */
public class ImageHandler {
    File[] listFile;
    ArrayList<String> f = new ArrayList<>();// list of file paths

    public ArrayList<String> getFromSdcard(String path)
    {
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constantes.ANDROID_IMG_DIR);
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator +
//                Constantes.APP_DIR + File.separator +
//                Constantes.IMG_DIR + File.separator);

        File file = new File(path);

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            for (File aux: listFile) {
                f.add(aux.getAbsolutePath());
                Log.d("file ", aux.getAbsolutePath());
            }

            Log.d("files count ", f.size() + "");
        }
        return f;
    }
}
