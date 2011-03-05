package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;

public class QuickActionClickListener implements OnItemLongClickListener, OnClickListener {
	
	Context context;
	ArrayList<PatternRecord> pattern_list;

	public QuickActionClickListener(ArrayList<PatternRecord> pattern_list) {
		this.pattern_list = pattern_list;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final PatternRecord pattern_record = pattern_list.get(position);
		return createQuickAction(view, pattern_record);
	}
	
	@Override
	public void onClick(View view) {
		final PatternRecord pattern_record = pattern_list.get(0);
		createQuickAction(view, pattern_record);
	}
	
	private boolean createQuickAction(View view, final PatternRecord pattern_record) {
		context = view.getContext();
		QuickAction qa = new QuickAction(view);
		
		ActionItem fav = new ActionItem();
    	fav.setTitle(context.getString(R.string.quickactions_fav));
    	fav.setIcon(context.getResources().getDrawable(R.drawable.chart));
    	fav.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Fav selected" , Toast.LENGTH_SHORT).show();
    		}
    	});
    	
    	ActionItem practising = new ActionItem();
    	practising.setTitle(context.getString(R.string.quickactions_practising));
    	practising.setIcon(context.getResources().getDrawable(R.drawable.production));
    	practising.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Practising selected", Toast.LENGTH_SHORT).show();
    		}
    	});
    	
    	ActionItem record = new ActionItem();
    	record.setTitle(context.getString(R.string.quickactions_record));
    	record.setIcon(context.getResources().getDrawable(R.drawable.record));
    	record.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Record selected" , Toast.LENGTH_SHORT).show();
    		}
    	});

    	ActionItem share = new ActionItem();
    	share.setTitle(context.getString(R.string.quickactions_share));
    	share.setIcon(context.getResources().getDrawable(R.drawable.share));
    	share.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Share selected " , Toast.LENGTH_SHORT).show();
    			share("Juggling Lab", context.getString(R.string.quickactions_share_working) + " " + pattern_record.getDisplay() + " " + context.getString(R.string.quickactions_share_thanks));
    		}
    	});

    	ActionItem stats = new ActionItem();
    	stats.setTitle(context.getString(R.string.quickactions_stats));
    	stats.setIcon(context.getResources().getDrawable(R.drawable.stats));
    	stats.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Stats selected" , Toast.LENGTH_SHORT).show();
    		}
    	});
		
		qa.addActionItem(fav);
		qa.addActionItem(practising);
		qa.addActionItem(record);
		qa.addActionItem(share);
		qa.addActionItem(stats);
		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		
		qa.show();
		return true;
	}
    
    private void share(String subject, String text) {
    	 final Intent intent = new Intent(Intent.ACTION_SEND);

    	 intent.setType("text/plain");
    	 intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	 intent.putExtra(Intent.EXTRA_TEXT, text);

    	 context.startActivity(Intent.createChooser(intent, context.getString(R.string.quickactions_share)));
    }

}
