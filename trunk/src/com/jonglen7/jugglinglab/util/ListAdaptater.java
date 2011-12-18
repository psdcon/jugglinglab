package com.jonglen7.jugglinglab.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;

/**
 * The Adapter used in the demonstration.
 * 
 * @author Cyril Mottier
 */
public class ListAdaptater extends BaseAdapter {

	// TODO Romain: Do we need all this ?
	private ListView listView;
	private LayoutInflater layoutInflater;
	private ArrayList<PatternRecord> pattern_list;
	private Context context;

    public ListAdaptater(ListView listView, LayoutInflater layoutInflater, ArrayList<PatternRecord> pattern_list, Context context) {
    	this.listView = listView;
    	this.layoutInflater = layoutInflater;
    	this.pattern_list = pattern_list;
		this.context = context;
    }
    
    @Override
    public int getCount() {
        return pattern_list.size();
    }

    @Override
    public String getItem(int position) {
        return pattern_list.get(position).getDisplay();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	ListViewHolder holder = null;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.list_item, parent, false);

            holder = new ListViewHolder();
            holder.star = (CheckBox) convertView.findViewById(R.id.btn_star);
            holder.list_item_text = (TextView) convertView.findViewById(R.id.list_item_text);

            convertView.setTag(holder);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }

        /*
         * The Android API provides the OnCheckedChangeListener interface
         * and its onCheckedChanged(CompoundButton buttonView, boolean
         * isChecked) method. Unfortunately, this implementation suffers
         * from a big problem: you can't determine whether the checking
         * state changed from code or because of a user action. As a result
         * the only way we have is to prevent the CheckBox from callbacking
         * our listener by temporary removing the listener.
         */
        holder.star.setOnCheckedChangeListener(null);
    	Trick trick = new Trick(pattern_list.get(position).getValuesFromAnim(), context);
    	holder.star.setChecked(trick.getSTARRED() > 0);
        holder.star.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);

        holder.list_item_text.setText(this.pattern_list.get(position).getDisplay());

        return convertView;
    }

    private OnCheckedChangeListener mStarCheckedChanceChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = listView.getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION) {
            	Trick trick = new Trick(pattern_list.get(position).getValuesFromAnim(), context);
            	trick.star();
            }
        }
    };
}