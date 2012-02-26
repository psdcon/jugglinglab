package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDListActivity;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class CollectionActivity extends GDListActivity {

	/** DataBase. */
	DataBaseHelper myDbHelper;
	
	/** Collection. */
	Collection collection;

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
        setContentView(R.layout.activity_collection);
        
        /** Get the Collection. */
        Bundle extras = getIntent().getExtras();
        if (extras != null){
        	collection = (Collection) extras.getParcelable("collection");
            
            myDbHelper = DataBaseHelper.init(this);
            
            /** The ArrayList that will populate the ListView. */
            pattern_list = createPatternList();
            
            setTitle(collection.getCUSTOM_DISPLAY());

            listView = getListView();
            mSchedule = new ListAdapterTrick(listView, getLayoutInflater(), pattern_list, this, getIntent(), CollectionActivity.this);
            
            listView.setOnItemClickListener(itemClickListener);
            listView.setOnItemLongClickListener(itemLongClickListener);
            listView.setAdapter(mSchedule);

            myDbHelper.close();

            /** QuickAction. */
            quickActionGrid = new QuickActionGridTrick(this);
        }
    }
    
    /** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	mSchedule.notifyDataSetChanged();
    	super.onResume();
    }

    private ArrayList<PatternRecord> createPatternList() {
    	ArrayList<PatternRecord> pattern_list = new ArrayList<PatternRecord>();
		
	 	String query = "SELECT T.ID_TRICK, T.PATTERN, H.CODE AS HANDS, B.CODE AS BODY, P.CODE AS PROP, T.XML_DISPLAY_LINE_NUMBER, T.CUSTOM_DISPLAY, TC.ID_COLLECTION, C.XML_LINE_NUMBER" +
	 				((collection.getIS_TUTORIAL() == 1)? ", TC.STEP": "") + " " +
					"FROM Trick T, Hands H, Body B, Prop P, TrickCollection TC, Collection C " +
					"WHERE T.ID_HANDS=H.ID_HANDS " + 
					"AND T.ID_BODY=B.ID_BODY " +
					"AND T.ID_PROP=P.ID_PROP " +
					"AND T.ID_TRICK=TC.ID_TRICK " +
					"AND TC.ID_COLLECTION=C.ID_COLLECTION " +
					"AND TC.ID_COLLECTION=" + collection.getID_COLLECTION() +
	 				((collection.getIS_TUTORIAL() == 1)? " ORDER BY TC.STEP": "");
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
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(CollectionActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionGrid.show(view, pattern_list.get(position), getIntent(), CollectionActivity.this);
			return true;
		}
	};
	
}
