package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.jml.JMLPattern;
import com.jonglen7.jugglinglab.jugglinglab.notation.Notation;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionInternal;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class PatternEntryActivity extends Activity {

    /** Pattern. */
	EditText edit_pattern;

    /** Hand movement. */
    Spinner spinner_hand_movement;
    EditText edit_hand_movement;

    /** Prop type. */
    Spinner spinner_prop_type;

    /** Dwell beats. */
    TextView txt_dwell_beats_progress;
    SeekBar seekbar_dwell_beats;

    /** Beats per second. */
    TextView txt_beats_per_second_progress;
    SeekBar seekbar_beats_per_second;

    /** Body movement. */
    Spinner spinner_body_movement;
    EditText edit_body_movement;

    /** Manual settings. */
	EditText edit_manual_settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_entry);

        /** Pattern. */
        edit_pattern = (EditText)findViewById(R.id.pattern_entry_edit_pattern);
        
        /** Hand movement. */
        spinner_hand_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_hand_movement);
        spinner_hand_movement.setOnItemSelectedListener(itemSelectedListenerHandMovement);
        ArrayAdapter<CharSequence> adapter_hand_movement = ArrayAdapter.createFromResource(this, R.array.hand_movement, android.R.layout.simple_spinner_item);
        adapter_hand_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_hand_movement.setAdapter(adapter_hand_movement);
        edit_hand_movement = (EditText)findViewById(R.id.pattern_entry_edit_hand_movement);

        /** Prop type. */
        spinner_prop_type = (Spinner) findViewById(R.id.pattern_entry_spinner_prop_type);
        ArrayAdapter<CharSequence> adapter_prop_type = ArrayAdapter.createFromResource(this, R.array.prop_type, android.R.layout.simple_spinner_item);
        adapter_prop_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prop_type.setAdapter(adapter_prop_type);
        
        /** Dwell beats. */
        txt_dwell_beats_progress = (TextView)findViewById(R.id.pattern_entry_txt_dwell_beats_progress);
        seekbar_dwell_beats = (SeekBar)findViewById(R.id.pattern_entry_seekbar_dwell_beats);
        seekbar_dwell_beats.setOnSeekBarChangeListener(seekBarChangeListenerDwellBeats);
    	txt_dwell_beats_progress.setText(" " + (seekbar_dwell_beats.getProgress()/10.) + " ");
        
        /** Beats per second. */
        txt_beats_per_second_progress = (TextView)findViewById(R.id.pattern_entry_txt_beats_per_second_progress);
        seekbar_beats_per_second = (SeekBar)findViewById(R.id.pattern_entry_seekbar_beats_per_second);
        seekbar_beats_per_second.setOnSeekBarChangeListener(seekBarChangeListenerBeatsPerSecond);
        txt_beats_per_second_progress.setText(" " + seekbar_beats_per_second.getProgress() + " ");

        /** Body movement. */
        spinner_body_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_body_movement);
        spinner_body_movement.setOnItemSelectedListener(itemSelectedListenerBodyMovement);
        ArrayAdapter<CharSequence> adapter_body_movement = ArrayAdapter.createFromResource(this, R.array.body_movement, android.R.layout.simple_spinner_item);
        adapter_body_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_body_movement.setAdapter(adapter_body_movement);
        edit_body_movement = (EditText)findViewById(R.id.pattern_entry_edit_body_movement);

        /** Manual settings. */
        edit_manual_settings = (EditText)findViewById(R.id.pattern_entry_edit_manual_settings);
    }
    
    /** Handle "Juggle" action. */
    public void onJuggleClick(View v) {
    	StringBuffer text = new StringBuffer(256);
    	text.append("pattern=" + edit_pattern.getText().toString());
		text.append((edit_hand_movement.getText().toString().length() > 0) ? (";hands=" + edit_hand_movement.getText().toString()) : "");
    	text.append(";prop=" + spinner_prop_type.getSelectedItem().toString().toLowerCase());
    	text.append(";dwell=" + txt_dwell_beats_progress.getText().toString());
    	text.append(";bps=" + txt_beats_per_second_progress.getText().toString());
		text.append((edit_body_movement.getText().toString().length() > 0) ? (";body=" + edit_body_movement.getText().toString()) : "");
		text.append((edit_manual_settings.getText().toString().length() > 0) ? (";" + edit_manual_settings.getText().toString()) : "");
    	
		JMLPattern pat = null;
		try {
	    	Notation ssn = Notation.getNotation("siteswap");
	    	pat = ssn.getJMLPattern(text.toString());
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		} catch (JuggleExceptionInternal e) {
			e.printStackTrace();
		}
    	Log.v("PatternEntryActivity", pat.toString());
    }
    
    private OnItemSelectedListener itemSelectedListenerHandMovement = new OnItemSelectedListener() {

	    @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	edit_hand_movement.setText(getResources().getStringArray(R.array.hand_movement_values)[pos]);
	    	edit_hand_movement.setEnabled(pos!=0);
        }

	    @Override
        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    };
    
    private OnItemSelectedListener itemSelectedListenerBodyMovement = new OnItemSelectedListener() {

	    @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	edit_body_movement.setText(getResources().getStringArray(R.array.body_movement_values)[pos]);
	    	edit_body_movement.setEnabled(pos!=0);
        }

	    @Override
        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    };
    
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerDwellBeats= new SeekBar.OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	txt_dwell_beats_progress.setText(" " + (seekbar_dwell_beats.getProgress()/10.) + " ");
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {}
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {}
    };
    
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerBeatsPerSecond = new SeekBar.OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	txt_beats_per_second_progress.setText(" " + seekbar_beats_per_second.getProgress() + " ");
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {}
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {}
    };

}
