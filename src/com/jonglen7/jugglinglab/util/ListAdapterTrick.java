package com.jonglen7.jugglinglab.util;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.ui.QuickActionGridTrick;

/**
 * The Adapter used in the demonstration.
 * 
 * @author Cyril Mottier
 */
public class ListAdapterTrick extends BaseAdapter {

	private ListView listView;
	private ArrayList<PatternRecord> pattern_list;
	private Activity activity;

    public ListAdapterTrick(ListView listView, ArrayList<PatternRecord> pattern_list, Activity activity) {
    	this.listView = listView;
    	this.pattern_list = pattern_list;
		this.activity = activity;
    }
    
    @Override
    public int getCount() {
        return pattern_list.size();
    }

    @Override
    public PatternRecord getItem(int position) {
        return pattern_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	ListViewHolder holder = null;

        if (convertView == null) {
            convertView = this.activity.getLayoutInflater().inflate(R.layout.list_item, parent, false);

            holder = new ListViewHolder();
            holder.star = (CheckBox) convertView.findViewById(R.id.btn_star);
            holder.list_text = (TextView) convertView.findViewById(R.id.list_text);
            holder.quick_action = (ImageView) convertView.findViewById(R.id.i_quick_action);

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
        
    	// Check if the trick is starred
        Trick trick = new Trick(getItem(position), this.activity);
    	boolean isStarred = false;
    	for (Collection collection : trick.getCollections()) {
    		if (collection.isStarred()) isStarred = true;
    	}
    	
    	holder.star.setChecked(isStarred);
        holder.star.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);

        holder.list_text.setText(getItem(position).getDisplay());

        holder.quick_action.setOnClickListener(mQuickActionlickListener);

        return convertView;
    }

    private OnCheckedChangeListener mStarCheckedChanceChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = listView.getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION) {
            	Trick trick = new Trick(getItem(position), activity);
            	trick.star();
            }
        }
    };

    /** QuickAction. */
    private OnClickListener mQuickActionlickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			QuickActionGridTrick quickActionGrid = new QuickActionGridTrick(activity);
            final int position = listView.getPositionForView(view);
			quickActionGrid.show(view, getItem(position));
		}

	};

}