package com.upshft.upshiftapp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.upshft.upshiftapp.R;
import java.util.ArrayList;

public class ScreenShotAdapter extends RecyclerView.Adapter<ScreenShotAdapter.Holder>
{
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<Bitmap>();
    Activity m_activity;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ss_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Log.e("bitmapArrayList","......bitmapArrayList........" + bitmapArrayList);
        holder.imageView.setImageBitmap(bitmapArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmapArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public ScreenShotAdapter() {

    }

    public ScreenShotAdapter(ArrayList<Bitmap> arrayList, Activity activity) {

        this.bitmapArrayList = arrayList;
        Log.e("bitmapArrayList","......bitmapArrayList........" + bitmapArrayList);
        m_activity = activity;
    }

}
