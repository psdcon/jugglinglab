package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDListActivity;
import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class CollectionsActivity extends GDListActivity {
	
	int IS_TUTORIAL;

	/** DataBase. */
	DataBaseHelper myDbHelper;
    
	/** Collection list. */
	ArrayList<Collection> collection_list;

    /** ListView. */
    ListView listView;

    /** QuickAction. */
    QuickActionGridCollection quickActionBar;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        IS_TUTORIAL = getIntent().getIntExtra("IS_TUTORIAL", 0);
    	
    	setTitle((new HashMap<Integer, String>() {
        	{ put(0, getString(R.string.pattern_list_title));}
        	{ put(1, getString(R.string.tutorials_title));}
        	}).get(IS_TUTORIAL));
    	
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_add)), R.id.action_bar_add);

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
        quickActionBar = new QuickActionGridCollection(this);
    }

    private ArrayList<Collection> createCollectionList() {
    	ArrayList<Collection> collections = new ArrayList<Collection>();
		
	 	String query = "SELECT ID_COLLECTION, IS_TUTORIAL, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
	 					"FROM Collection C " +
	 					"WHERE C.IS_TUTORIAL=" + this.IS_TUTORIAL + " " +
	 					"ORDER BY C.ID_COLLECTION";
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	Collection c = new Collection(cursor, this);
        	if (!c.isStarred()) collections.add(c);
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
        	map.put("list_group_text", c.getCUSTOM_DISPLAY());
        	hashmaps.add(map);
    	}
    	
    	return hashmaps;
    }

    /** ActionBar. */
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_add:
            	final EditText input = new EditText(this);
        		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setView(input);
        		builder.setTitle(this.getString(R.string.gd_add));
        		
        		builder.setPositiveButton(this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			Collection collection = new Collection(IS_TUTORIAL, CollectionsActivity.this);
		        		collection.edit(input.getText().toString());
		        		// TODO Romain (update ListView): Add the name in the ListView
	        		}
        		});

        		builder.setNegativeButton(this.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			dialog.cancel();
	        		}
        		});
        		builder.show();
                return true;

            default:
                return super.onHandleActionBarItemClick(item, position);
        }
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
