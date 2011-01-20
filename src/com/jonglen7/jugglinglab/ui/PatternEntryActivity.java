package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jonglen7.jugglinglab.R;

public class PatternEntryActivity extends Activity {

    /** Pattern. */
	EditText edit_pattern;

    /** Beats per second. */
    TextView txt_beats_per_second_progress;
    SeekBar seekbar_beats_per_second;

    /** Dwell beats. */
    TextView txt_dwell_beats_progress;
    SeekBar seekbar_dwell_beats;

    /** Hand movement. */
    Spinner spinner_hand_movement;
    EditText edit_hand_movement;

    /** Body movement. */
    Spinner spinner_body_movement;
    EditText edit_body_movement;

    /** Prop type. */
    Spinner spinner_prop_type;

    /** Manual settings. */
	EditText edit_manual_settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_entry);

        /** Pattern. */
        edit_pattern = (EditText)findViewById(R.id.pattern_entry_edit_pattern);
        
        /** Beats per second. */
        txt_beats_per_second_progress = (TextView)findViewById(R.id.pattern_entry_txt_beats_per_second_progress);
        seekbar_beats_per_second = (SeekBar)findViewById(R.id.pattern_entry_seekbar_beats_per_second);
        seekbar_beats_per_second.setOnSeekBarChangeListener(seekBarChangeListener);
        
        /** Dwell beats. */
        txt_dwell_beats_progress = (TextView)findViewById(R.id.pattern_entry_txt_dwell_beats_progress);
        seekbar_dwell_beats = (SeekBar)findViewById(R.id.pattern_entry_seekbar_dwell_beats);
        seekbar_dwell_beats.setOnSeekBarChangeListener(seekBarChangeListener);
        
        /** Update Seek Bars (to show the values). */
        updateSeekBars();
        
        /** Hand movement. */
        spinner_hand_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_hand_movement);
        ArrayAdapter adapter_hand_movement = ArrayAdapter.createFromResource(this, R.array.hand_movement, android.R.layout.simple_spinner_item);
        adapter_hand_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_hand_movement.setAdapter(adapter_hand_movement);
        edit_hand_movement = (EditText)findViewById(R.id.pattern_entry_edit_hand_movement);

        /** Body movement. */
        spinner_body_movement = (Spinner) findViewById(R.id.pattern_entry_spinner_body_movement);
        ArrayAdapter adapter_body_movement = ArrayAdapter.createFromResource(this, R.array.body_movement, android.R.layout.simple_spinner_item);
        adapter_body_movement.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_body_movement.setAdapter(adapter_body_movement);
        edit_body_movement = (EditText)findViewById(R.id.pattern_entry_edit_body_movement);

        /** Prop type spinner. */
        Spinner spinner_prop_type = (Spinner) findViewById(R.id.pattern_entry_spinner_prop_type);
        ArrayAdapter adapter_prop_type = ArrayAdapter.createFromResource(this, R.array.prop_type, android.R.layout.simple_spinner_item);
        adapter_prop_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prop_type.setAdapter(adapter_prop_type);

        /** Manual settings. */
        edit_manual_settings = (EditText)findViewById(R.id.pattern_entry_edit_manual_settings);
    }
    
    /** Handle "Juggle" action. */
    public void onJuggleClick(View v) {
    	Context context = getApplicationContext();
    	CharSequence text = ""
    		+ "Pattern          = " + edit_pattern.getText().toString()              + "\n"
    		+ "Beats per second = " + seekbar_beats_per_second.getProgress()         + "\n"
        	+ "Dwell beats      = " + seekbar_dwell_beats.getProgress()              + "\n"
        	+ "Hand movement    = " + edit_hand_movement.getText().toString()        + "\n"
        	+ "Body movement    = " + edit_body_movement.getText().toString()        + "\n"
        	+ "Prop type        = " /*+ spinner_prop_type.*/ + "\n"
        	+ "Manual settings  = " + edit_manual_settings.getText().toString()      + "\n";

    	int duration = Toast.LENGTH_SHORT;
        
    	Toast.makeText(context, text, duration).show();
    	
    	
    }
    
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	updateSeekBars();
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    	// TODO Auto-generated method stub
	    }
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    	// TODO Auto-generated method stub
	    }
    };
    
    /** Update Seek Bars (to show the values). */
    private void updateSeekBars() {
    	txt_beats_per_second_progress.setText(" " + seekbar_beats_per_second.getProgress() + " ");
    	txt_dwell_beats_progress.setText(" " + (seekbar_dwell_beats.getProgress()/10.) + " ");
    }

}
