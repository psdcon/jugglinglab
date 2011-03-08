package com.jonglen7.jugglinglab.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class TutorialsActivity extends ListActivity {
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        
        /** ActionBar. */
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        
        pattern_list = new ArrayList<PatternRecord>();
        
        ArrayList<HashMap<String, String>> listItem = createPatternList();
        
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
                new String[] {"list_item_text", "list_item_fav"}, new int[] {R.id.list_item_text, R.id.list_item_fav});

        ListView listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        listView.setAdapter(mSchedule);
    }

    private ArrayList<HashMap<String, String>> createPatternList() {
    	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
    	
		HashMap<String, String> map;
    	
    	DataBaseHelper myDbHelper = new DataBaseHelper(this);
    	 
        try {
        	myDbHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try {
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}
        
	 	//rawQuery("SELECT * FROM TABLE WHERE c.deck_id=? AND c.next_date < ? AND c.next_date > 0 AND c.active > 0 AND c.deck_level=?",
	 	//		new String[] { Long.toString(deckId), Long.toString(now), Long.toString(level)});
	 	

	 	myDbHelper.close();
	 	String query = "SELECT T.PATTERN, H.CODE, B.CODE, P.CODE, T.XML_DISPLAY_LINE_NUMBER, TT.ID_COLLECTION, TT.STEP, C.XML_LINE_NUMBER " +
	 					"FROM Trick T, Hands H, Body B, Prop P, TrickTutorial TT, Collection C " +
	 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
	 					"AND T.ID_BODY=B.ID_BODY " +
	 					"AND T.ID_PROP=P.ID_PROP " +
	 					"AND T.ID_TRICK=TT.ID_TRICK " +
	 					"AND TT.ID_COLLECTION=C.ID_COLLECTION " +
	 					"ORDER BY TT.ID_COLLECTION, TT.STEP";
	 	Cursor cursor = myDbHelper.getReadableDatabase().rawQuery(query, null);
        startManagingCursor(cursor);

    	String[] trick = getResources().getStringArray(R.array.trick);
	 	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	String display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
        	map = new HashMap<String, String>();
        	map.put("list_item_text", display);
        	map.put("list_item_fav", String.valueOf(R.drawable.fav));
        	listItem.add(map);
        	String anim = createAnim(cursor);
        	pattern_list.add(new PatternRecord(display, "", "siteswap", anim));
            cursor.moveToNext();
        }

	 	cursor.close();
    	
		return listItem;
	}

	private String createAnim(Cursor cursor) {
		String anim = "";
		anim += "pattern=" + cursor.getString(cursor.getColumnIndex("PATTERN"));
		anim += ";hands=" + cursor.getString(1);
		anim += ";body=" + cursor.getString(2);
		anim += ";prop=" + cursor.getString(3);
		return anim;
	}

	/** ActionBar. */
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
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
            case R.id.menu_about:
            	startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(TutorialsActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

}
