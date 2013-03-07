package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;
import com.jonglen7.jugglinglab.util.Trick;

/**
 * The Pattern Entry activity.
 * 
 * TODO: Factorize getValuesFromDB() and getPropTypes() if we allow custom
 * names for the props
 */
public class PatternEntryActivity extends BaseDisplayModeActivity {

	/** DataBase. */
	DataBaseHelper myDbHelper;
	
    /** Pattern. */
	TextView txt_pattern;
	EditText edit_pattern;

    /** Hand movement. */
	TextView txt_hand_movement;
    Spinner spinner_hand_movement;
    EditText edit_hand_movement;
    // ArrayList of (String name, String code)
    ArrayList<ArrayList<String>> hand_movements;
    // Index of the custom movement (necessary to differentiate from the default value)
    int hand_movement_custom;

    /** Prop type. */
    TextView txt_prop_type;
    Spinner spinner_prop_type;
    // ArrayList of (String name, String code)
    ArrayList<ArrayList<String>> prop_types;

    /** Dwell beats. */
    TextView txt_dwell_beats;
    TextView txt_dwell_beats_progress;
    SeekBar seekbar_dwell_beats;

    /** Beats per second. */
    TextView txt_beats_per_second;
    TextView txt_beats_per_second_progress;
    SeekBar seekbar_beats_per_second;

    /** Body movement. */
    TextView txt_body_movement;
    Spinner spinner_body_movement;
    EditText edit_body_movement;
    // ArrayList of (String name, String code)
    ArrayList<ArrayList<String>> body_movements;
    // Index of the custom movement (necessary to differentiate from the default value)
    int body_movement_custom;

    /** Manual settings. */
    TextView txt_manual_settings;
	EditText edit_manual_settings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.activity_pattern_entry);

    	/** DataBase. */
        myDbHelper = DataBaseHelper.init(this);
        
        /** Pattern. */
        txt_pattern = (TextView) findViewById(R.id.pattern_entry_txt_pattern);
        edit_pattern = (EditText) findViewById(R.id.pattern_entry_edit_pattern);
        
        /** Hand movement. */
        hand_movements = getHandMovements();
        ArrayAdapter<CharSequence> adapter_hand_movement = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (int i=0; i<hand_movements.size(); i++) adapter_hand_movement.add(hand_movements.get(i).get(0));
        adapter_hand_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txt_hand_movement = (TextView) findViewById(R.id.pattern_entry_txt_hand_movement);
        spinner_hand_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_hand_movement);
        spinner_hand_movement.setOnItemSelectedListener(itemSelectedListenerHandMovement);
        spinner_hand_movement.setAdapter(adapter_hand_movement);
        edit_hand_movement = (EditText) findViewById(R.id.pattern_entry_edit_hand_movement);
        edit_hand_movement.addTextChangedListener(textChangedListenerHandMovement);

        /** Prop type. */
        prop_types = getPropTypes();
        ArrayAdapter<CharSequence> adapter_prop_type = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (int i=0; i<prop_types.size(); i++) adapter_prop_type.add(prop_types.get(i).get(0));
        adapter_prop_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txt_prop_type = (TextView) findViewById(R.id.pattern_entry_txt_prop_type);
        spinner_prop_type = (Spinner) findViewById(R.id.pattern_entry_spinner_prop_type);
        spinner_prop_type.setAdapter(adapter_prop_type);
        
        /** Dwell beats. */
        txt_dwell_beats = (TextView) findViewById(R.id.pattern_entry_txt_dwell_beats);
        txt_dwell_beats_progress = (TextView) findViewById(R.id.pattern_entry_txt_dwell_beats_progress);
        seekbar_dwell_beats = (SeekBar) findViewById(R.id.pattern_entry_seekbar_dwell_beats);
        seekbar_dwell_beats.setOnSeekBarChangeListener(seekBarChangeListenerDwellBeats);
    	txt_dwell_beats_progress.setText(" " + ((seekbar_dwell_beats.getProgress() + 1)/10.) + " ");
        
        /** Beats per second. */
    	txt_beats_per_second = (TextView) findViewById(R.id.pattern_entry_txt_beats_per_second);
        txt_beats_per_second_progress = (TextView) findViewById(R.id.pattern_entry_txt_beats_per_second_progress);
        seekbar_beats_per_second = (SeekBar) findViewById(R.id.pattern_entry_seekbar_beats_per_second);
        seekbar_beats_per_second.setOnSeekBarChangeListener(seekBarChangeListenerBeatsPerSecond);
        txt_beats_per_second_progress.setText(" " + (seekbar_beats_per_second.getProgress() + 1) + " ");

        /** Body movement. */
        body_movements = getBodyMovements();
        ArrayAdapter<CharSequence> adapter_body_movement = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (int i=0; i<body_movements.size(); i++) adapter_body_movement.add(body_movements.get(i).get(0));
        adapter_body_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txt_body_movement = (TextView) findViewById(R.id.pattern_entry_txt_body_movement);
        spinner_body_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_body_movement);
        spinner_body_movement.setOnItemSelectedListener(itemSelectedListenerBodyMovement);
        spinner_body_movement.setAdapter(adapter_body_movement);
        edit_body_movement = (EditText) findViewById(R.id.pattern_entry_edit_body_movement);
        edit_body_movement.addTextChangedListener(textChangedListenerBodyMovement);

        /** Manual settings. */
        txt_manual_settings = (TextView) findViewById(R.id.pattern_entry_txt_manual_settings);
        edit_manual_settings = (EditText) findViewById(R.id.pattern_entry_edit_manual_settings);
        
    	/** Normal or advanced mode. */
        switchDisplayMode();

    	/** DataBase. */
        myDbHelper.close();
        
        /** Get the PatternRecord from the Intent, if any, and modify attributes of the activity accordingly. */
        Bundle extras = getIntent().getExtras();
        if (extras != null){
        	HashMap<String, String> pattern_record_values = PatternRecord.animToValues(((PatternRecord) extras.getParcelable("pattern_record")).getAnim());
        	if (pattern_record_values.get("pattern").length() > 0) edit_pattern.setText(pattern_record_values.get("pattern"));
        	if (pattern_record_values.get("hands").length() > 0) edit_hand_movement.setText(pattern_record_values.get("hands"));
        	if (pattern_record_values.get("prop").length() > 0) spinner_prop_type.setSelection(adapter_prop_type.getPosition(pattern_record_values.get("prop")));
        	if (pattern_record_values.get("dwell").length() > 0) seekbar_dwell_beats.setProgress((int) (10 * Double.parseDouble(pattern_record_values.get("dwell"))) - 1);
        	if (pattern_record_values.get("bps").length() > 0) seekbar_beats_per_second.setProgress(Integer.parseInt(pattern_record_values.get("bps")) - 1);
        	if (pattern_record_values.get("body").length() > 0) edit_body_movement.setText(pattern_record_values.get("body"));
        }
    }
    
    @Override
    public void switchDisplayMode(int visibility) {
    	txt_dwell_beats.setVisibility(visibility);
    	txt_dwell_beats_progress.setVisibility(visibility);
    	txt_dwell_beats.setVisibility(visibility);
    	seekbar_dwell_beats.setVisibility(visibility);
    	txt_beats_per_second.setVisibility(visibility);
    	txt_beats_per_second_progress.setVisibility(visibility);
    	txt_beats_per_second.setVisibility(visibility);
    	seekbar_beats_per_second.setVisibility(visibility);
    	txt_body_movement.setVisibility(visibility);
    	spinner_body_movement.setVisibility(visibility);
    	edit_body_movement.setVisibility(visibility);
    	txt_manual_settings.setVisibility(visibility);
    	edit_manual_settings.setVisibility(visibility);
    }
    
    /**
     * Handle "Juggle" action.
     * Create the String corresponding to the pattern according to the notation
     * found here: http://jugglinglab.sourceforge.net/html/sspanel.html
     * @param v The View
     */
    public void onJuggleClick(View v) {
		String display = edit_pattern.getText().toString();
		
    	StringBuffer text = new StringBuffer(256);
    	text.append("pattern=" + display);
		text.append((edit_hand_movement.getText().toString().length() > 0) ? (";hands=" + edit_hand_movement.getText().toString()) : "");
    	text.append(";prop=" + prop_types.get(spinner_prop_type.getSelectedItemPosition()).get(1));
    	text.append(";dwell=" + txt_dwell_beats_progress.getText().toString());
    	text.append(";bps=" + txt_beats_per_second_progress.getText().toString());
		text.append((edit_body_movement.getText().toString().length() > 0) ? (";body=" + edit_body_movement.getText().toString()) : "");
		text.append((edit_manual_settings.getText().toString().length() > 0) ? (";" + edit_manual_settings.getText().toString()) : "");
		
		if (spinner_hand_movement.getSelectedItemPosition() != 0 && spinner_hand_movement.getSelectedItemPosition() != spinner_hand_movement.getCount() - 1)
			display += " " + getResources().getStringArray(R.array.hand_movement)[spinner_hand_movement.getSelectedItemPosition()];
		
		PatternRecord pattern_record = new PatternRecord(display, "", "siteswap", text.toString());
    	display = new Trick(pattern_record, this).getCUSTOM_DISPLAY();
    	if (display != "") pattern_record.setDisplay(display);
		
		Intent i = new Intent(this, AnimationActivity.class);
        i.putExtra("pattern_record", pattern_record);
        startActivity(i);
    }
    
    /** Hand movement. */
    /**
     * Returns the list of hand movements found in the database.
     * Each element of this list is a list containing two elements: the name
     * of the movement and its code.
     * @return The list of hand movements found in the database
     */
    private ArrayList<ArrayList<String>> getHandMovements() {
    	return getValuesFromDB("HANDS");
    }
    
    private OnItemSelectedListener itemSelectedListenerHandMovement = new OnItemSelectedListener() {

	    @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	if (pos!= hand_movement_custom) edit_hand_movement.setText(hand_movements.get(pos).get(1));
	    	edit_hand_movement.setEnabled(pos!=0);
        }

	    @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
    
    private TextWatcher textChangedListenerHandMovement = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			for (int pos=0; pos < hand_movements.size(); pos ++) {
				if (s.toString().compareTo(hand_movements.get(pos).get(1)) == 0 && spinner_hand_movement.getSelectedItemPosition() != hand_movement_custom) {
					spinner_hand_movement.setSelection(pos);
					return;
				}
			}
			spinner_hand_movement.setSelection(hand_movement_custom);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };
    
    /** Prop type. */
    /**
     * Returns the list of prop types found in the database.
     * Each element of this list is a list containing two elements: the name
     * of the prop and its code.
     * @return The list of prop types found in the database
     */
    private ArrayList<ArrayList<String>> getPropTypes() {
    	ArrayList<ArrayList<String>> propTypes = new ArrayList<ArrayList<String>>();
    	
    	String query = "SELECT CODE, XML_LINE_NUMBER " +
    					"FROM PROP " +
    					"WHERE CODE in ('ball', 'cube')" +  // TODO Fred (Props): Modify/Delete this when new props available
    					"ORDER BY ID_PROP";
    	
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);

    	String[] prop_type = getResources().getStringArray(R.array.prop_type);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
        	String code = cursor.getString(cursor.getColumnIndex("CODE"));
        	String name = prop_type[xml_line_number];
        	ArrayList<String> props = new ArrayList<String>();
        	props.add(name);
        	props.add(code);
        	propTypes.add(props);
            cursor.moveToNext();
        }

	 	cursor.close();
    	
    	return propTypes;
    }
    
    /** Dwell beats. */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerDwellBeats= new SeekBar.OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	txt_dwell_beats_progress.setText(" " + ((seekbar_dwell_beats.getProgress() + 1)/10.) + " ");
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {}
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {}
    };
    
    /** Beats per second. */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerBeatsPerSecond = new SeekBar.OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	txt_beats_per_second_progress.setText(" " + (seekbar_beats_per_second.getProgress() + 1) + " ");
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {}
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {}
    };
    
    /** Body movement. */
    /**
     * Returns the list of body movements found in the database.
     * Each element of this list is a list containing two elements: the name
     * of the movement and its code.
     * @return The list of body movements found in the database
     */
    private ArrayList<ArrayList<String>> getBodyMovements() {
    	return getValuesFromDB("BODY");
    }
    
    private OnItemSelectedListener itemSelectedListenerBodyMovement = new OnItemSelectedListener() {

	    @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	if (pos!= body_movement_custom) edit_body_movement.setText(body_movements.get(pos).get(1));
	    	edit_body_movement.setEnabled(pos!=0);
        }

	    @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
    
    private TextWatcher textChangedListenerBodyMovement = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			for (int pos=0; pos < body_movements.size(); pos ++) {
				if (s.toString().compareTo(body_movements.get(pos).get(1)) == 0 && spinner_body_movement.getSelectedItemPosition() != body_movement_custom) {
					spinner_body_movement.setSelection(pos);
					return;
				}
			}
			spinner_body_movement.setSelection(body_movement_custom);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    /**
     * Returns the list of movements found in the database in the chosen table.
     * Each element of this list is a list containing two elements: the name
     * of the movement and its code.
     * @param table The name of the table to use
     * @return The list of movements found in the database
     */
    private ArrayList<ArrayList<String>> getValuesFromDB(String table) {
    	ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
    	
    	String query = "SELECT CODE, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
    					"FROM " + table.toUpperCase(Locale.US) + " " +
    					"ORDER BY ID_" + table.toUpperCase(Locale.US);

    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);
        
        int custom_count = 0;
        int item = 0;

        int arrayId = 0;
        if (table.toUpperCase(Locale.US) == "HANDS") arrayId = R.array.hand_movement;
    	else if (table.toUpperCase(Locale.US) == "BODY") arrayId = R.array.body_movement;
    	String[] names_array = getResources().getStringArray(arrayId);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
        	String code = cursor.getString(cursor.getColumnIndex("CODE"));
        	String custom_display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
        	if (xml_line_number > 0 || code.compareTo("") == 0 || custom_display != null) {
        		String name = "";
        		if (custom_display != null) name = custom_display;
        		else name = names_array[xml_line_number];
            	ArrayList<String> value = new ArrayList<String>();
            	value.add(name);
            	value.add(code);
            	values.add(value);
        	}
            cursor.moveToNext();
            
            // We need to know the index of the custom movement, since there are
            // two movements in the DataBase with CODE="" (Default and Custom),
            // we need this kind of hack to find this index
            if (code.compareTo("") == 0) {
            	custom_count++;
                if (custom_count == 2) {
                	if (table.toUpperCase(Locale.US) == "HANDS") hand_movement_custom = item;
                	else if (table.toUpperCase(Locale.US) == "BODY") body_movement_custom = item;
                }
            }
        	item++;
        }

	 	cursor.close();
    	
    	return values;
    }
    
    
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
