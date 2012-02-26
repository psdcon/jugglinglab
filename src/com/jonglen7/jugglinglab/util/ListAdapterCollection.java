package com.jonglen7.jugglinglab.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.ui.QuickActionGridCollection;

public class ListAdapterCollection  extends BaseAdapter {

	// TODO Romain (ListAdapterCollection): Can't we reduce the number of needed attributes ?
	private ListView listView;
	private LayoutInflater layoutInflater;
	private ArrayList<Collection> collection_list;
	private Context context;
	private Intent intent;
	private Activity activity;

    public ListAdapterCollection(ListView listView, LayoutInflater layoutInflater, ArrayList<Collection> collection_list, Context context, Intent intent, Activity activity) {
    	this.listView = listView;
    	this.layoutInflater = layoutInflater;
    	this.collection_list = collection_list;
		this.context = context;
		this.intent = intent;
		this.activity = activity;
    }

	@Override
	public int getCount() {
        return collection_list.size();
	}

	@Override
	public Collection getItem(int position) {
        return collection_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ListViewHolder holder = null;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.list_group, parent, false);

            holder = new ListViewHolder();
            holder.list_text = (TextView) convertView.findViewById(R.id.list_text);
            holder.quick_action = (ImageView) convertView.findViewById(R.id.i_quick_action);

            convertView.setTag(holder);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }

        holder.list_text.setText(getItem(position).getCUSTOM_DISPLAY());

        holder.quick_action.setOnClickListener(mQuickActionlickListener);

        return convertView;
	}

    /** QuickAction. */
    private OnClickListener mQuickActionlickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			QuickActionGridCollection quickActionGrid = new QuickActionGridCollection(context);
            final int position = listView.getPositionForView(view);
			quickActionGrid.show(view, getItem(position), intent, activity);
		}

	};

}
