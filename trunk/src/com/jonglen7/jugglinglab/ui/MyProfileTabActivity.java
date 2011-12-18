package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.ListAdaptater;

public class MyProfileTabActivity extends ListActivity {

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListView listView;

    /** QuickAction. */
    QuickActionBarTrick quickActionBar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_tab);

        myDbHelper = DataBaseHelper.init(this);
        
        /** The ArrayList that will populate the ListView. */
        pattern_list = createPatternList(getIntent().getStringExtra("tab"));
        
        listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(new ListAdaptater(listView, getLayoutInflater(), pattern_list, this));

        myDbHelper.close();
        
        /** QuickAction. */
        quickActionBar = new QuickActionBarTrick(this);
    }
    
    private ArrayList<PatternRecord> createPatternList(String tab) {
    	ArrayList<PatternRecord> pattern_list = new ArrayList<PatternRecord>();
		
		String query = "";
		if (tab.equals("starred")) {
			// TODO Romain: Gérer CUSTOM_* et cie
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
		 					"FROM Trick T, Hands H, Prop P, Body B " +
		 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
		 					"AND T.ID_BODY=B.ID_BODY " +
		 					"AND T.ID_PROP=P.ID_PROP " +
		 					"AND T.STARRED=1";
		} else if (tab.equals("goals")) {
			// TODO Romain: Gérer CUSTOM_* et cie (date, un seul goal par trick)
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
		 					"FROM Trick T, Hands H, Prop P, Body B, Goal G " +
		 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
		 					"AND T.ID_BODY=B.ID_BODY " +
		 					"AND T.ID_PROP=P.ID_PROP " +
		 					"AND T.ID_TRICK=G.ID_TRICK";
		} else if (tab.equals("practicing")) {
			// TODO Romain: Gérer CUSTOM_* et cie (date)
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
		 					"FROM Trick T, Hands H, Prop P, Body B, Catch C " +
		 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
		 					"AND T.ID_BODY=B.ID_BODY " +
		 					"AND T.ID_PROP=P.ID_PROP " +
		 					"AND T.ID_TRICK=C.ID_TRICK";
		}
		
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);

    	String[] trick = getResources().getStringArray(R.array.trick);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	String display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
        	if (display == null) {
            	display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
        	}
        	pattern_list.add(new PatternRecord(display, "", "siteswap", cursor));
            cursor.moveToNext();
        }

	 	cursor.close();
	 	
		return pattern_list;
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(MyProfileTabActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionBar.show(view, pattern_list.get(position));
			return true;
		}
	};
    

}
