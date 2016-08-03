package com.fumei.pdfread.adapter;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.database.ContentObservable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fumei.pdfread.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by WCB on 2016/7/8.
 */
public class MyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> maxNumber;
    private LayoutInflater mInflater;

    public MyListViewAdapter(Context context, List<Integer> maxNumber) {
        this.context = context;
        this.maxNumber = maxNumber;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return maxNumber.size();
    }

    @Override
    public Object getItem(int i) {
        return maxNumber.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.particulars_listview_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(maxNumber.get(i));
        return convertView;
    }

}


