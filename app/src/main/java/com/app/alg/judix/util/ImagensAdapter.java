package com.app.alg.judix.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.app.alg.judix.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//import util.imagebasics.R;


/**
 * Created by AndreBTS on 30/09/2015.
 */
public class ImagensAdapter extends BaseAdapter {
    private ArrayList<String> images = new ArrayList<>();// list of file paths
    private ArrayList<Integer> selecionados = new ArrayList<>();// list of file paths
    private Context context;

    public ImagensAdapter(String img_path, Context context) {
        final ImageHandler imageHandler = new ImageHandler();
        images = imageHandler.getFromSdcard(img_path);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CheckableLayout l;
        ImageView i;

        if (convertView == null) {
            i = new ImageView(context);
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            i.setLayoutParams(new ViewGroup.LayoutParams(330, 340));
            l = new CheckableLayout(context);
            l.setLayoutParams(new GridView.LayoutParams(
                    GridView.LayoutParams.WRAP_CONTENT,
                    GridView.LayoutParams.WRAP_CONTENT));
            l.addView(i);
        } else {
            l = (CheckableLayout) convertView;
            i = (ImageView) l.getChildAt(0);
        }

        if (!TextUtils.isEmpty(images.get(position))) {
            Uri uri = Uri.parse("file://" + images.get(position));
            //appendLog(uri.toString());


            Glide.with(context)
                    .load(uri)
                    .centerCrop()
                    //.transform(new RoundedCornersTransformation(context, 10, 10 ))

                    //.transform(new RoundedCornersTransformation(context, 30, 0, RoundedCornersTransformation.CornerType.BOTTOM))
                    //.transform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(i);


            /*Picasso picasso = new Picasso.Builder(context).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            }).build();

            picasso.with(context)
                    .setLoggingEnabled(true);
            picasso.load(uri)
                    .error(R.drawable.ic_img_broken)
                    .placeholder(R.drawable.ic_media_pause)
                    .fit().centerCrop()
                    .tag(context)
                    .into(i);*/
        }

        return l;
    }

    public final int getCount() {
        return images.size();
    }

    public final String getItem(int position) {
        return images.get(position);
    }

    public final void addItem(String item) {
        images.add(item);
        notifyDataSetChanged();
    }

    public void removeImage(int i) {
        images.remove(i);
    }

    public final long getItemId(int position) {
        return position;
    }

    public void addSelection(int adapterPos) {
        selecionados.add(adapterPos);
    }

    public ArrayList<Integer> getSelecteds() {
        return selecionados;
    }

    public void clearSelection() {
        selecionados.clear();
    }

    public int getSelected(int pos) {
        return selecionados.get(pos);
    }

    public int getSelectedCount() {
        return selecionados.size();
    }

    public void removeSelection(int i) {
        selecionados.remove(i);
    }

    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        @SuppressWarnings("deprecation")
        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackgroundDrawable(checked ? getResources().getDrawable(
                    R.drawable.img_selected) : null);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }
    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/imagemCaminho.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}