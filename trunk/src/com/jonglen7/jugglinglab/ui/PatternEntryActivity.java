package com.jonglen7.jugglinglab.ui;

import greendroid.app.GDActivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class PatternEntryActivity extends GDActivity {

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
    int body_movement_custom;

    /** Manual settings. */
    TextView txt_manual_settings;
	EditText edit_manual_settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_entry);

        myDbHelper = DataBaseHelper.init(this);
        
        /** Pattern. */
        txt_pattern = (TextView) findViewById(R.id.pattern_entry_txt_pattern);
        edit_pattern = (EditText) findViewById(R.id.pattern_entry_edit_pattern);
        
        /** Hand movement. */
        txt_hand_movement = (TextView) findViewById(R.id.pattern_entry_txt_hand_movement);
        spinner_hand_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_hand_movement);
        spinner_hand_movement.setOnItemSelectedListener(itemSelectedListenerHandMovement);
        ArrayAdapter<CharSequence> adapter_hand_movement = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        hand_movements = getHandMovements();
        for (int i=0; i<hand_movements.size(); i++) adapter_hand_movement.add(hand_movements.get(i).get(0));
        adapter_hand_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_hand_movement.setAdapter(adapter_hand_movement);
        edit_hand_movement = (EditText) findViewById(R.id.pattern_entry_edit_hand_movement);
        edit_hand_movement.addTextChangedListener(textChangedListenerHandMovement);

        /** Prop type. */
        txt_prop_type = (TextView) findViewById(R.id.pattern_entry_txt_prop_type);
        spinner_prop_type = (Spinner) findViewById(R.id.pattern_entry_spinner_prop_type);
        ArrayAdapter<CharSequence> adapter_prop_type = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        prop_types = getPropTypes();
        for (int i=0; i<prop_types.size(); i++) adapter_prop_type.add(prop_types.get(i).get(0));
        adapter_prop_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prop_type.setAdapter(adapter_prop_type);
        // TODO Fred: Delete the setVisibility() calls when ready
//        txt_prop_type.setVisibility(View.GONE);
//        spinner_prop_type.setVisibility(View.GONE);
        
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
        txt_body_movement = (TextView) findViewById(R.id.pattern_entry_txt_body_movement);
        spinner_body_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_body_movement);
        spinner_body_movement.setOnItemSelectedListener(itemSelectedListenerBodyMovement);
        ArrayAdapter<CharSequence> adapter_body_movement = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        body_movements = getBodyMovements();
        for (int i=0; i<body_movements.size(); i++) adapter_body_movement.add(body_movements.get(i).get(0));
        adapter_body_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_body_movement.setAdapter(adapter_body_movement);
        edit_body_movement = (EditText) findViewById(R.id.pattern_entry_edit_body_movement);
        edit_body_movement.addTextChangedListener(textChangedListenerBodyMovement);

        /** Manual settings. */
        txt_manual_settings = (TextView) findViewById(R.id.pattern_entry_txt_manual_settings);
        edit_manual_settings = (EditText) findViewById(R.id.pattern_entry_edit_manual_settings);
        
    	/** Normal or advanced mode. */
        switchDisplayMode();

        myDbHelper.close();
        
        /** Get the PatternRecord. */
        Bundle extras = getIntent().getExtras();
        if (extras != null){
        	HashMap<String, String> pattern_record_values = PatternRecord.animToValues(((PatternRecord) extras.getParcelable("pattern_record")).getAnim());

        	if (pattern_record_values.get("pattern").length() > 0) {
                edit_pattern.setText(pattern_record_values.get("pattern"));
            }

        	if (pattern_record_values.get("hands").length() > 0) {
                edit_hand_movement.setText(pattern_record_values.get("hands"));
            }

        	if (pattern_record_values.get("prop").length() > 0) {
                spinner_prop_type.setSelection(adapter_prop_type.getPosition(pattern_record_values.get("prop")));
            }

        	if (pattern_record_values.get("dwell").length() > 0) {
                seekbar_dwell_beats.setProgress((int) (10 * Double.parseDouble(pattern_record_values.get("dwell"))) - 1);
            }

        	if (pattern_record_values.get("bps").length() > 0) {
                seekbar_beats_per_second.setProgress(Integer.parseInt(pattern_record_values.get("bps")) - 1);
            }

        	if (pattern_record_values.get("body").length() > 0) {
                edit_body_movement.setText(pattern_record_values.get("body"));
            }
        }
    }

	/** Called when the activity is resumed. */
    @Override
    public void onResume() {
    	super.onResume();
    	switchDisplayMode();
    }
    
    /** Hide or show some parameters depending if the Advanced mode is selected. */
    public void switchDisplayMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int visibility = (preferences.getBoolean("user_advanced_mode", false))?View.VISIBLE:View.GONE;
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
    
    /** Handle "Juggle" action. */
    public void onJuggleClick(View v) {
    	StringBuffer text = new StringBuffer(256);
    	text.append("pattern=" + edit_pattern.getText().toString());
		text.append((edit_hand_movement.getText().toString().length() > 0) ? (";hands=" + edit_hand_movement.getText().toString()) : "");
    	text.append(";prop=" + prop_types.get(spinner_prop_type.getSelectedItemPosition()).get(1));
    	text.append(";dwell=" + txt_dwell_beats_progress.getText().toString());
    	text.append(";bps=" + txt_beats_per_second_progress.getText().toString());
		text.append((edit_body_movement.getText().toString().length() > 0) ? (";body=" + edit_body_movement.getText().toString()) : "");
		text.append((edit_manual_settings.getText().toString().length() > 0) ? (";" + edit_manual_settings.getText().toString()) : "");
		
		String display = edit_pattern.getText().toString();
		if (spinner_hand_movement.getSelectedItemPosition() != 0 && spinner_hand_movement.getSelectedItemPosition() != spinner_hand_movement.getCount() - 1)
			display += " " + getResources().getStringArray(R.array.hand_movement)[spinner_hand_movement.getSelectedItemPosition()];
		
		PatternRecord pattern_record = new PatternRecord(display, "", "siteswap", text.toString());
    	display = new Trick(pattern_record, this).getCUSTOM_DISPLAY();
    	if (display != "") pattern_record.setDisplay(display);
		
		Intent i = new Intent(this, JMLPatternActivity.class);
        i.putExtra("pattern_record", pattern_record);
        startActivity(i);
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
    

    private ArrayList<ArrayList<String>> getHandMovements() {
    	ArrayList<ArrayList<String>> handMovements = new ArrayList<ArrayList<String>>();
    	
    	String query = "SELECT CODE, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
    					"FROM HANDS " +
    					"ORDER BY ID_HANDS";

    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);
        
        int hand_movement_custom_count = 0;
        int hand_movement_item = 0;

    	String[] hand_movement = getResources().getStringArray(R.array.hand_movement);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
        	String code = cursor.getString(cursor.getColumnIndex("CODE"));
        	String custom_display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
        	if (xml_line_number > 0 || code.compareTo("") == 0 || custom_display != null) {
        		String name = "";
        		if (custom_display != null) name = custom_display;
        		else name = hand_movement[xml_line_number];
            	ArrayList<String> hands = new ArrayList<String>();
            	hands.add(name);
            	hands.add(code);
            	handMovements.add(hands);
        	}
            cursor.moveToNext();
            // TODO Romain: Hack pour la gestion de Custom vraiment moche
            if (code.compareTo("") == 0) {
            	hand_movement_custom_count++;
                if (hand_movement_custom_count == 2) hand_movement_custom = hand_movement_item;
            }
        	hand_movement_item++;
        }

	 	cursor.close();
    	
    	return handMovements;
    }
    

    private ArrayList<ArrayList<String>> getBodyMovements() {
    	ArrayList<ArrayList<String>> bodyMovements = new ArrayList<ArrayList<String>>();
    	
    	String query = "SELECT CODE, XML_LINE_NUMBER, CUSTOM_DISPLAY " +
    					"FROM BODY " +
    					"ORDER BY ID_BODY";
    	
    	Cursor cursor = myDbHelper.execQuery(query);
        startManagingCursor(cursor);
        
        int body_movement_custom_count = 0;
        int body_movement_item = 0;

    	String[] body_movement = getResources().getStringArray(R.array.body_movement);
    	
	 	cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
        	int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
        	String code = cursor.getString(cursor.getColumnIndex("CODE"));
        	String custom_display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
        	if (xml_line_number > 0 || code.compareTo("") == 0 || custom_display != null) {
        		String name = "";
        		if (custom_display != null) name = custom_display;
        		else name = body_movement[xml_line_number];
            	ArrayList<String> bodies = new ArrayList<String>();
            	bodies.add(name);
            	bodies.add(code);
            	bodyMovements.add(bodies);
        	}
            cursor.moveToNext();
            // TODO Romain: Hack pour la gestion de Custom vraiment moche
            if (code.compareTo("") == 0) {
            	body_movement_custom_count++;
                if (body_movement_custom_count == 2) body_movement_custom = body_movement_item;
            }
            body_movement_item++;
        }

	 	cursor.close();
    	
    	return bodyMovements;
    }
    

    private ArrayList<ArrayList<String>> getPropTypes() {
    	ArrayList<ArrayList<String>> propTypes = new ArrayList<ArrayList<String>>();
    	
    	String query = "SELECT CODE, XML_LINE_NUMBER " +
    					"FROM PROP " +
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


}
