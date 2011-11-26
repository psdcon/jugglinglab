package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDListActivity;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class TutorialsActivity extends GDListActivity {
    
    /** Pattern list. */
    ArrayList<PatternRecord> pattern_list;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        
        pattern_list = new ArrayList<PatternRecord>();
        
        ArrayList<HashMap<String, String>> listItem = createPatternList();
        
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.list_item,
                new String[] {"list_item_text"}, new int[] {R.id.list_item_text});

        ListView listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        listView.setAdapter(mSchedule);
    }

    private ArrayList<HashMap<String, String>> createPatternList() {
    	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
    	
		HashMap<String, String> map;
		
		// TODO Gérer les DESCRIPTION et CUSTOM_*
	 	String query = "SELECT T.PATTERN, H.CODE, B.CODE, P.CODE, T.XML_DISPLAY_LINE_NUMBER, TT.ID_COLLECTION, TT.STEP, C.XML_LINE_NUMBER " +
	 					"FROM Trick T, Hands H, Body B, Prop P, TrickTutorial TT, Collection C " +
	 					"WHERE T.ID_HANDS=H.ID_HANDS " + 
	 					"AND T.ID_BODY=B.ID_BODY " +
	 					"AND T.ID_PROP=P.ID_PROP " +
	 					"AND T.ID_TRICK=TT.ID_TRICK " +
	 					"AND TT.ID_COLLECTION=C.ID_COLLECTION " +
	 					"ORDER BY TT.ID_COLLECTION, TT.STEP";
	 	Cursor cursor = DataBaseHelper.execQuery(this, query, null);
        startManagingCursor(cursor);

    	String[] trick = getResources().getStringArray(R.array.trick);
	 	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	String display = trick[cursor.getInt(cursor.getColumnIndex("XML_DISPLAY_LINE_NUMBER"))];
        	map = new HashMap<String, String>();
        	map.put("list_item_text", display);
        	listItem.add(map);
        	pattern_list.add(new PatternRecord(display, "", "siteswap", createAnim(cursor)));
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
			Intent i = new Intent(TutorialsActivity.this, JMLPatternActivity.class);
	        i.putExtra("pattern_record", pattern_list.get(position));
	        startActivity(i);
		}
    	
    };

}
