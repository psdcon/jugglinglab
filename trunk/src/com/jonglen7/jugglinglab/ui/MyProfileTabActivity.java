package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.ListAdapterTrick;

/**
 * The My Profile Tab Activity that holds the content of a tab in the My Profile Activity.
 */
public class MyProfileTabActivity extends ListActivity {

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;

    /** ListView. */
    ListView listView;
    ListAdapterTrick mSchedule;

    /** QuickAction. */
    QuickActionGridTrick quickActionGrid;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_tab);

        myDbHelper = DataBaseHelper.init(this);
        
        /** The ArrayList that will populate the ListView. */
        pattern_list = createPatternList(getIntent().getStringExtra("tab"));

        listView = getListView();
        mSchedule = new ListAdapterTrick(listView, pattern_list, MyProfileTabActivity.this);
        
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(mSchedule);

        myDbHelper.close();
        
        /** QuickAction. */
        // TODO Romain (QuickAction): when on MyProfile, if (for example) the
        // STAR option is used, MyProfileTabActivity won't be restarted but
        // just the content of the tab (i.e. no ActionBar, no tabs, ...)
        quickActionGrid = new QuickActionGridTrick(this);
    }
    
    /** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    	mSchedule.notifyDataSetChanged();
    }
    
    /** Create the list of patterns to display in the tab. */
    private ArrayList<PatternRecord> createPatternList(String tab) {
    	ArrayList<PatternRecord> pattern_list = new ArrayList<PatternRecord>();
		
		String query = "";
		if (tab.equals("starred")) {
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
 					"FROM Trick T, Hands H, Prop P, Body B, TrickCollection TC, Collection C " +
 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
 					"AND T.ID_BODY=B.ID_BODY " +
 					"AND T.ID_PROP=P.ID_PROP " +
 					"AND T.ID_TRICK=TC.ID_TRICK " +
 					"AND TC.ID_COLLECTION = C.ID_COLLECTION " + 
 					"AND C.XML_LINE_NUMBER=" + Collection.STARRED_XML_LINE_NUMBER;
		} else if (tab.equals("unsorted")) {
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
 					"FROM Trick T, Hands H, Prop P, Body B " +
 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
 					"AND T.ID_BODY=B.ID_BODY " +
 					"AND T.ID_PROP=P.ID_PROP " +
 					"AND T.ID_TRICK NOT IN (SELECT ID_TRICK FROM TrickCollection)";
		} else if (tab.equals("goals")) {
			// TODO Romain (Stats): Gérer date, un seul goal par trick
		 	query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, P.CODE AS PROP, B.CODE AS BODY, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY " +
 					"FROM Trick T, Hands H, Prop P, Body B, Goal G " +
 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
 					"AND T.ID_BODY=B.ID_BODY " +
 					"AND T.ID_PROP=P.ID_PROP " +
 					"AND T.ID_TRICK=G.ID_TRICK";
		} else if (tab.equals("training")) {
			// TODO Romain (Stats): Gérer date
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
        	String display = (cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) != null ? cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY")) : trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))]);
        	pattern_list.add(new PatternRecord(display, "", "siteswap", cursor));
            cursor.moveToNext();
        }

	 	cursor.close();
	 	
		return pattern_list;
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(MyProfileTabActivity.this, AnimationActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionGrid.show(view, pattern_list.get(position));
			return true;
		}
	};
    
	
    /**
     * To solve the following error for honeycomb:
     * 		java.lang.RuntimeException: Unable to resume activity 
     *  	java.lang.IllegalStateException: trying to re-query an already closed cursor
     *  
     *  @TODO : startManagingCursor(Cursor c) and stopManagingCursor(Cursor c) have been deprecated in API level 11 (Android 3.0.X HoneyComb)
     *          Use the new CursorLoader class with LoaderManager instead; 
     *          this is also available on older platforms through the Android compatibility package. 
     */
    @Override
    public void startManagingCursor(Cursor c) {
        if (Build.VERSION.SDK_INT < 11) {
            super.startManagingCursor(c);
        }
    }

}
