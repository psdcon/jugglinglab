package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class MyProfileTabActivity extends ListActivity {

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListView listView;

    /** QuickAction. */
    MyQuickActionBar quickActionBar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_tab);

        myDbHelper = DataBaseHelper.init(this);
        
        pattern_list = new ArrayList<PatternRecord>();
        
        /** The ArrayList that will populate the ListView. */
        ArrayList<HashMap<String, String>> listItem = createPatternList(getIntent().getStringExtra("tab"));
        
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
               new String[] {"list_item_text"}, new int[] {R.id.list_item_text});
        
        listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        // TODO Romain: Uncomment when ready: listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(mSchedule);

        myDbHelper.close();
        
        /** QuickAction. */
        quickActionBar = new MyQuickActionBar(this);
    }
    
    private ArrayList<HashMap<String, String>> createPatternList(String tab) {
    	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
    	
		HashMap<String, String> map;
		
		String query = "";
		if (tab.equals("starred")) {
			// TODO Romain: Gérer CUSTOM_* et cie
		 	query = "SELECT T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER " +
		 					"FROM Trick T, Hands H, Prop P, Body B " +
		 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
		 					"AND T.ID_BODY=B.ID_BODY " +
		 					"AND T.ID_PROP=P.ID_PROP " +
		 					"AND T.STARRED=1";
		} else if (tab.equals("goals")) {
			// TODO Romain: Gérer CUSTOM_* et cie (date, un seul goal par trick)
		 	query = "SELECT T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER " +
		 					"FROM Trick T, Hands H, Prop P, Body B, Goal G " +
		 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
		 					"AND T.ID_BODY=B.ID_BODY " +
		 					"AND T.ID_PROP=P.ID_PROP " +
		 					"AND T.ID_TRICK=G.ID_TRICK";
		} else if (tab.equals("practicing")) {
			// TODO Romain: Gérer CUSTOM_* et cie (date)
		 	query = "SELECT T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER " +
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
        	String display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
        	map = new HashMap<String, String>();
        	map.put("list_item_text", display);
        	listItem.add(map);
        	pattern_list.add(new PatternRecord(display, "", "siteswap", cursor));
            cursor.moveToNext();
        }

	 	cursor.close();
        
        return listItem;
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
