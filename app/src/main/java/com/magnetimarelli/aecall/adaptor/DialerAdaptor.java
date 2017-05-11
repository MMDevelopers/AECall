package com.magnetimarelli.aecall.adaptor;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magnetimarelli.aecall.R;
import com.magnetimarelli.aecall.fragment.Dialer;

/**
 * Created by F49558B on 5/9/2017.
 */

public class DialerAdaptor extends BaseAdapter {

    private Context mContext;
    private static LayoutInflater inflater=null;
    private Dialer dialer = null;
    public DialerAdaptor(Context c, Dialer dialer) {
        mContext = c;
        inflater = ( LayoutInflater )c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dialer = dialer;
    }
    @Override
    public int getCount() {
        return numbers.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.dialer_paid_card, null);
        holder.tv=(AppCompatButton) rowView.findViewById(R.id.dial_number);
        holder.tv.setText(numbers[position]);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatButton button = (AppCompatButton)v;
                dialer.contactEdit.append(button.getText());
                dialer.contactEdit.setCursorVisible(false);
            }
        });
        return rowView;
    }
    public class Holder
    {
        AppCompatButton tv;
    }
    String []numbers = {"1","2","3","4","5","6","7","8","9","*","0","#"};
}
