package com.magnetimarelli.aecall.adaptor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnetimarelli.aecall.R;
import com.magnetimarelli.aecall.fragment.Dialer;
import com.magnetimarelli.aecall.fragment.RecentCall;
import com.magnetimarelli.aecall.model.RecentCallModel;
import com.magnetimarelli.aecall.view.CircularView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by F49558B on 5/10/2017.
 */

public class RecentCallAdaptor extends RecyclerView.Adapter<RecentCallAdaptor.ViewHolder> {

    Context context = null;
    List<RecentCallModel> recentCallList = null;
    RecentCall parent;
    public RecentCallAdaptor(Context context, List<RecentCallModel> recentCalls, RecentCall parent){
        recentCallList = recentCalls;
        this.context = context;
        this.parent = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_call_card, parent, false);
        ViewHolder lViewHolder = new ViewHolder(lView);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecentCallModel RM = recentCallList.get(position);
        if(RM.getName()!=null){
            holder.caller_info.setText(RM.getName());
            Drawable background = holder.circularName.getBackground();
            holder.circularName.setVisibility(View.VISIBLE);
            holder.imageAvtar.setVisibility(View.GONE);
            holder.circularNameText.setText(RM.getName().substring(0,1));
        }
        else {
            holder.caller_info.setText(RM.getContact());
            holder.imageAvtar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_blue_avtar));
            holder.circularName.setVisibility(View.GONE);
            holder.imageAvtar.setVisibility(View.VISIBLE);
        }
        if(RM.getImageUrl()!=null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(RM.getImageUrl()));
                holder.imageAvtar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) { // TODO Auto-generated catch block
                e.printStackTrace();
                holder.imageAvtar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_blue_avtar));
            }
            catch (IOException e) { // TODO Auto-generated catch block
                e.printStackTrace();
                holder.imageAvtar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_blue_avtar));
            }

            holder.circularName.setVisibility(View.GONE);
            holder.imageAvtar.setVisibility(View.VISIBLE);
        }
        holder.call_info.setText(RM.getDateTimes().toString()+","+RM.getDuration());
        holder.call_info.setCompoundDrawablesWithIntrinsicBounds(RM.getCallType(), 0, 0, 0);
        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.makeCall(RM.getContact());
            }
        });
    }


    @Override
    public int getItemCount() {
        return recentCallList.size();
    }

    private int colorGenerator(){
        Random rm = new Random();
        int x = rm.nextInt(11447982)+4210752;
        String hex = Integer.toHexString(x);
        return Color.parseColor("#"+hex);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CircularView imageAvtar;
        public FrameLayout circularName;
        public TextView circularNameText,call_info,caller_info;
        public ImageButton callBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            imageAvtar = (CircularView) itemView.findViewById(R.id.caller_image_view);
            circularName = (FrameLayout) itemView.findViewById(R.id.caller_name_image);
            circularNameText = (TextView) itemView.findViewById(R.id.caller_name_text);
            call_info = (TextView) itemView.findViewById(R.id.call_info);
            caller_info = (TextView) itemView.findViewById(R.id.contact_info);
            callBtn = (ImageButton) itemView.findViewById(R.id.call_btn);
        }
    }
}
