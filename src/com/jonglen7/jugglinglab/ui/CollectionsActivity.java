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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class CollectionsActivity extends GDListActivity {
	
	String table;

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
	/** Collection list. */
	ArrayList<Collection> collection_list;

    /** ListView. */
    ListView listView;

    /** QuickAction. */
    QuickActionBarCollection quickActionBar;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
    	table = (String) getIntent().getStringExtra("table");
    	
    	setTitle((new HashMap<String, String>() {
        	{ put("TrickTutorial", getString(R.string.tutorials_title));}
        	{ put("TrickCollection", getString(R.string.pattern_list_title));}
        	}).get(table));

        myDbHelper = DataBaseHelper.init(this);

        collection_list = createCollectionList();
        
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), collectionsToHashMaps(collection_list), R.layout.list_group,
                new String[] {"list_group_text"}, new int[] {R.id.list_group_text});
        
        listView = getListView();
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(mSchedule);

        myDbHelper.close();

        /** QuickAction. */
        quickActionBar = new QuickActionBarCollection(this);
    }

    private ArrayList<Collection> createCollectionList() {
    	ArrayList<Collection> collections = new ArrayList<Collection>();
		
		// TODO Romain: Gérer CUSTOM_*
	 	String query = "SELECT DISTINCT TT.ID_COLLECTION AS ID_COLLECTION, XML_LINE_NUMBER " +
	 					"FROM " + table + " TT, Collection C " +
	 					"WHERE TT.ID_COLLECTION=C.ID_COLLECTION " +
	 					"ORDER BY TT.ID_COLLECTION";
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);

    	String[] collection = getResources().getStringArray(R.array.collection);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	int ID_COLLECTION = cursor.getInt(cursor.getColumnIndex("ID_COLLECTION"));
        	String display = collection[cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"))];
        	collections.add(new Collection(table, ID_COLLECTION, display));
            cursor.moveToNext();
        }

	 	cursor.close();
    	
		return collections;
	}
    
    private ArrayList<HashMap<String, String>> collectionsToHashMaps(ArrayList<Collection> collections) {
    	ArrayList<HashMap<String, String>> hashmaps = new ArrayList<HashMap<String, String>>();
    	
		HashMap<String, String> map;
    	for (Collection c: collections) {
        	map = new HashMap<String, String>();
        	map.put("list_group_text", c.getDisplay());
        	hashmaps.add(map);
    	}
    	
    	return hashmaps;
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
			Intent i = new Intent(CollectionsActivity.this, CollectionActivity.class);
	        i.putExtra("collection", collection_list.get(position));
	        startActivity(i);
		}
    	
    };

    /** QuickAction. */
    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			quickActionBar.show(view, collection_list.get(position));
			return true;
		}
	};

}
