package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDExpandableListActivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class PatternListActivity extends GDExpandableListActivity {

	DataBaseHelper myDbHelper;
    
    /** Pattern list. */
	ArrayList<ArrayList<PatternRecord>> pattern_list;

    /** QuickAction. */
    MyQuickActionBar quickActionBar;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_pattern_list);

        myDbHelper = DataBaseHelper.init(this);

        pattern_list = new ArrayList<ArrayList<PatternRecord>>();

        ArrayList<HashMap<String, String>> listGroup = createGroupList();
        ArrayList<ArrayList<HashMap<String, String>>> listItem = createPatternList();
        
        SimpleExpandableListAdapter mSchedule = new SimpleExpandableListAdapter(
        		this.getBaseContext(),
        		listGroup,
        		R.layout.list_item,
                new String[] {"list_item_text"},
                new int[] {R.id.list_item_text},
        		listItem,
        		R.layout.list_item,
                new String[] {"list_item_text"},
                new int[] {R.id.list_item_text}
        		);
        
        ExpandableListView expandableListView = getExpandableListView();
        expandableListView.setOnChildClickListener(childClickListener);
        // TODO Romain: Uncomment when ready: expandableListView.setOnItemLongClickListener(itemLongClickListener);
        expandableListView.setAdapter(mSchedule);

        myDbHelper.close();
        
        /** QuickAction. */
        quickActionBar = new MyQuickActionBar(this);
    }

    // TODO Romain: Just a copy/paste of the methods in TutorialsActivity to see if it works, so it might needs some changes
    private ArrayList<HashMap<String, String>> createGroupList() {
    	ArrayList<HashMap<String, String>> listGroup = new ArrayList<HashMap<String, String>>();
    	
		HashMap<String, String> map;
		
		// TODO Romain: Gérer CUSTOM_*
	 	String query = "SELECT DISTINCT XML_LINE_NUMBER " +
	 					"FROM TrickCollection TC, Collection C " +
	 					"WHERE TC.ID_COLLECTION=C.ID_COLLECTION " +
	 					"ORDER BY TC.ID_COLLECTION";
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);

    	String[] collection = getResources().getStringArray(R.array.collection);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	String display = collection[cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"))];
        	map = new HashMap<String, String>();
        	map.put("list_item_text", display);
        	listGroup.add(map);
            cursor.moveToNext();
        }

	 	cursor.close();
    	
		return listGroup;
	}

    private ArrayList<ArrayList<HashMap<String, String>>> createPatternList() {
    	ArrayList<ArrayList<HashMap<String, String>>> listItem = new ArrayList<ArrayList<HashMap<String, String>>>();
    	
		HashMap<String, String> map;
		
		// TODO Romain: Gérer DESCRIPTION et CUSTOM_*
	 	String query = "SELECT T.PATTERN, H.CODE, B.CODE, P.CODE, T.XML_DISPLAY_LINE_NUMBER, TC.ID_COLLECTION, C.XML_LINE_NUMBER " +
					"FROM Trick T, Hands H, Body B, Prop P, TrickCollection TC, Collection C " +
					"WHERE T.ID_HANDS=H.ID_HANDS " + 
					"AND T.ID_BODY=B.ID_BODY " +
					"AND T.ID_PROP=P.ID_PROP " +
					"AND T.ID_TRICK=TC.ID_TRICK " +
					"AND TC.ID_COLLECTION=C.ID_COLLECTION " +
					"ORDER BY TC.ID_COLLECTION";
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);

    	String[] trick = getResources().getStringArray(R.array.trick);
	 	
    	int lastCollection = -1;
    	ArrayList<HashMap<String, String>> collectionTricks = null;
    	ArrayList<PatternRecord> patterns = null;
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	String display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
        	int collection = cursor.getInt(cursor.getColumnIndex("ID_COLLECTION"));
        	map = new HashMap<String, String>();
        	map.put("list_item_text", display);
        	if (lastCollection != collection) {
        		if (lastCollection != -1) {
        			listItem.add(collectionTricks);
                	pattern_list.add(patterns);
        		}
        		collectionTricks = new ArrayList<HashMap<String, String>>();
        		patterns = new ArrayList<PatternRecord>();
        		lastCollection = collection;
        	}
        	collectionTricks.add(map);
        	patterns.add(new PatternRecord(display, "", "siteswap", createAnim(cursor)));
            cursor.moveToNext();
        }
        if (collectionTricks != null) {
        	listItem.add(collectionTricks);
        	pattern_list.add(patterns);
        }

	 	cursor.close();
    	
		return listItem;
	}
    
//    private ArrayList<HashMap<String, String>> createPatternList() {
//    	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
//    	
//		HashMap<String, String> map;
//		
//	 	String query = "SELECT T.PATTERN, H.CODE, B.CODE, P.CODE, T.XML_DISPLAY_LINE_NUMBER, TC.ID_COLLECTION, C.XML_LINE_NUMBER " +
//	 					"FROM Trick T, Hands H, Body B, Prop P, TrickCollection TC, Collection C " +
//	 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
//	 					"AND T.ID_BODY=B.ID_BODY " +
//	 					"AND T.ID_PROP=P.ID_PROP " +
//	 					"AND T.ID_TRICK=TC.ID_TRICK " +
//	 					"AND TC.ID_COLLECTION=C.ID_COLLECTION " +
//	 					"ORDER BY TC.ID_COLLECTION";
//    	Cursor cursor = myDbHelper.execQuery(query);
//        startManagingCursor(cursor);
//
//    	String[] trick = getResources().getStringArray(R.array.trick);
//	 	
//	 	cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//        	String display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
//        	map = new HashMap<String, String>();
//        	map.put("list_item_text", display);
//        	listItem.add(map);
//        	pattern_list.add(new PatternRecord(display, "", "siteswap", createAnim(cursor)));
//            cursor.moveToNext();
//        }
//
//	 	cursor.close();
//    	
//		return listItem;
//	}

	private String createAnim(Cursor cursor) {
		String anim = "";
		anim += "pattern=" + cursor.getString(cursor.getColumnIndex("PATTERN"));
		anim += ";hands=" + cursor.getString(1);
		anim += ";body=" + cursor.getString(2);
		anim += ";prop=" + cursor.getString(3);
		return anim;
	}
    
    /** Menu button. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	startActivity(new Intent(this, SettingsHomeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private OnChildClickListener childClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			Intent i = new Intent(PatternListActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(groupPosition).get(childPosition));
	        startActivity(i);
	        return true;
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			// Hack Romain: there is no OnChildLongClickListener so I found this hack here http://stackoverflow.com/a/8320128
			if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
	            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
	            int childPosition = ExpandableListView.getPackedPositionChild(id);
	            quickActionBar.show(view, pattern_list.get(groupPosition).get(childPosition));
	            return true;
	        }
	        return false;
		}
		
	};
}
