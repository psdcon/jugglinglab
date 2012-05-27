package com.jonglen7.jugglinglab.ui;

import greendroid.graphics.drawable.ActionBarDrawable;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.ListAdapterCollection;

/**
 * The Collections Activity that displays the list of collections of tricks
 * (tutorials or pattern list).
 */
public class CollectionsActivity extends BaseListActivity {
	
	/** Used to differentiate Tutorials and Pattern List */
	int IS_TUTORIAL;
    
	/** Collection list. */
	ArrayList<Collection> collection_list;

    /** ListView. */
    ListAdapterCollection mSchedule;

    /** QuickAction. */
    QuickActionGridCollection quickActionGrid;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TUTORIAL = getIntent().getIntExtra("IS_TUTORIAL", 0);

    	setTitle((new SparseArray<String>() {
			{ put(0, getString(R.string.pattern_list_title));}
        	{ put(1, getString(R.string.tutorials_title));}
        	}).get(IS_TUTORIAL));
    	
        addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(new ActionBarDrawable(this, R.drawable.gd_action_bar_add)), R.id.action_bar_add);

        myDbHelper = DataBaseHelper.init(this);

        collection_list = createCollectionList();
        
        listView = getListView();
        mSchedule = new ListAdapterCollection(listView, collection_list, CollectionsActivity.this);
        
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setAdapter(mSchedule);

        myDbHelper.close();

        /** QuickAction. */
        quickActionGrid = new QuickActionGridCollection(this);
    }
    
    /** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	mSchedule.notifyDataSetChanged();
    	super.onResume();
    }
    
    /** Get the list of collections. */
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

    /** ActionBar. */
    @Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_add:
            	final EditText input = new EditText(this);
            	input.setSingleLine();
        		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setView(input);
        		builder.setTitle(this.getString(R.string.gd_add));
        		
        		builder.setPositiveButton(this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			Collection collection = new Collection(IS_TUTORIAL, CollectionsActivity.this);
		        		collection.edit(input.getText().toString());
		        		Intent intent = getIntent();
		        		finish();
		        		startActivity(intent);
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
			quickActionGrid.show(view, collection_list.get(position));
			return true;
		}
	};

}
