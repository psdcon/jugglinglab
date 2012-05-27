package com.jonglen7.jugglinglab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;

public class GeneratorActivity extends BaseDisplayModeActivity {
	
	/** Balls. */
	TextView txt_balls;
	EditText edit_balls;
	
	/** Max. throw. */
	TextView txt_max_throw;
	EditText edit_max_throw;

    /** Rhythm. */
	TextView txt_rhythm;
    Spinner spinner_rhythm;

    /** Jugglers. */
    TextView txt_jugglers;
    Spinner spinner_jugglers;
	
	/** Period. */
    TextView txt_period;
	EditText edit_period;

    /** Compositions. */
	TextView txt_compositions;
    Spinner spinner_compositions;
    
    /** Find. */
    TextView txt_find;
    CheckBox cb_ground_state_patterns;
    CheckBox cb_excited_state_patterns;
    CheckBox cb_transition_throws;
    CheckBox cb_pattern_rotations;
    CheckBox cb_juggler_permutations;
    CheckBox cb_connected_patterns_only;
    
    /** Multiplexing. */
    TextView txt_multiplexing;
    CheckBox cb_enable;
    TextView txt_simultaneous_throws;
    EditText edit_simultaneous_throws;
    CheckBox cb_no_simultaneous_catches;
    CheckBox cb_no_clustered_throws;
    CheckBox cb_true_multiplexing_only;
    
    /** Exclude / Include. */
    TextView txt_exclude_these_expressions;
    EditText edit_exclude_these_expressions;
    TextView txt_include_these_expressions;
    EditText edit_include_these_expressions;
    TextView txt_passing_communication_delay;
    EditText edit_passing_communication_delay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setActionBarContentView(R.layout.activity_generator);

    	/** Balls. */
    	txt_balls = (TextView) findViewById(R.id.generator_txt_balls);
    	edit_balls = (EditText) findViewById(R.id.generator_edit_balls);
    	
    	/** Max. throw. */
    	txt_max_throw = (TextView) findViewById(R.id.generator_txt_max_throw);
    	edit_max_throw = (EditText) findViewById(R.id.generator_edit_max_throw);
    	
    	/** Period. */
    	txt_period = (TextView) findViewById(R.id.generator_txt_period);
    	edit_period = (EditText) findViewById(R.id.generator_edit_period);

        /** Rhythm. */
    	txt_rhythm = (TextView) findViewById(R.id.generator_txt_rhythm);
    	spinner_rhythm = (Spinner) findViewById(R.id.generator_spinner_rhythm);
        ArrayAdapter<CharSequence> adapter_rhythm = ArrayAdapter.createFromResource(this, R.array.rhythm, android.R.layout.simple_spinner_item);
        adapter_rhythm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rhythm.setAdapter(adapter_rhythm);

        /** Jugglers. */
        txt_jugglers = (TextView) findViewById(R.id.generator_txt_jugglers);
    	spinner_jugglers = (Spinner) findViewById(R.id.generator_spinner_jugglers);
        spinner_jugglers.setOnItemSelectedListener(itemSelectedListenerJugglers);
        ArrayAdapter<CharSequence> adapter_jugglers = ArrayAdapter.createFromResource(this, R.array.jugglers, android.R.layout.simple_spinner_item);
        adapter_jugglers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_jugglers.setAdapter(adapter_jugglers);
        
        /** Compositions. */
        txt_compositions = (TextView) findViewById(R.id.generator_txt_compositions);
    	spinner_compositions = (Spinner) findViewById(R.id.generator_spinner_compositions);
        ArrayAdapter<CharSequence> adapter_compositions = ArrayAdapter.createFromResource(this, R.array.compositions, android.R.layout.simple_spinner_item);
        adapter_compositions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_compositions.setAdapter(adapter_compositions);
        
        /** Find. */
        txt_find = (TextView) findViewById(R.id.generator_txt_find);
        cb_ground_state_patterns = (CheckBox) findViewById(R.id.generator_cb_ground_state_patterns);
        cb_ground_state_patterns.setOnCheckedChangeListener(checkedChangeListenerGroundExcitedStatePatterns);
        cb_excited_state_patterns = (CheckBox) findViewById(R.id.generator_cb_excited_state_patterns);
        cb_excited_state_patterns.setOnCheckedChangeListener(checkedChangeListenerGroundExcitedStatePatterns);
        cb_transition_throws = (CheckBox) findViewById(R.id.generator_cb_transition_throws);
        cb_pattern_rotations = (CheckBox) findViewById(R.id.generator_cb_pattern_rotations);
        cb_juggler_permutations = (CheckBox) findViewById(R.id.generator_cb_juggler_permutations);
        cb_connected_patterns_only = (CheckBox) findViewById(R.id.generator_cb_connected_patterns_only);
        
        /** Multiplexing. */
        txt_multiplexing = (TextView) findViewById(R.id.generator_txt_multiplexing);
        cb_enable = (CheckBox) findViewById(R.id.generator_cb_enable);
        cb_enable.setOnCheckedChangeListener(checkedChangeListenerEnable);
        txt_simultaneous_throws = (TextView) findViewById(R.id.generator_txt_simultaneous_throws);
        edit_simultaneous_throws = (EditText) findViewById(R.id.generator_edit_simultaneous_throws);
        cb_no_simultaneous_catches = (CheckBox) findViewById(R.id.generator_cb_no_simultaneous_catches);
        cb_no_clustered_throws = (CheckBox) findViewById(R.id.generator_cb_no_clustered_throws);
        cb_true_multiplexing_only = (CheckBox) findViewById(R.id.generator_cb_true_multiplexing_only);
        
        /** Exclude / Include. */
        txt_exclude_these_expressions = (TextView) findViewById(R.id.generator_txt_exclude_these_expressions);
        edit_exclude_these_expressions = (EditText) findViewById(R.id.generator_edit_exclude_these_expressions);
        txt_include_these_expressions = (TextView) findViewById(R.id.generator_txt_include_these_expressions);
        edit_include_these_expressions = (EditText) findViewById(R.id.generator_edit_include_these_expressions);
        txt_passing_communication_delay = (TextView) findViewById(R.id.generator_txt_passing_communication_delay);
        edit_passing_communication_delay = (EditText) findViewById(R.id.generator_edit_passing_communication_delay);

        /** Normal or advanced mode. */
        switchDisplayMode();
    }
    
    /** Hide or show some parameters depending if the Advanced mode is selected. */
    @Override
    public void switchDisplayMode(int visibility) {
        txt_compositions.setVisibility(visibility);
    	spinner_compositions.setVisibility(visibility);
    	txt_find.setVisibility(visibility);
    	cb_ground_state_patterns.setVisibility(visibility);
    	cb_excited_state_patterns.setVisibility(visibility);
    	cb_transition_throws.setVisibility(visibility);
    	cb_pattern_rotations.setVisibility(visibility);
    	cb_juggler_permutations.setVisibility(visibility);
    	cb_connected_patterns_only.setVisibility(visibility);
    	txt_multiplexing.setVisibility(visibility);
    	cb_enable.setVisibility(visibility);
    	txt_simultaneous_throws.setVisibility(visibility);
    	edit_simultaneous_throws.setVisibility(visibility);
    	cb_no_simultaneous_catches.setVisibility(visibility);
    	cb_no_clustered_throws.setVisibility(visibility);
    	cb_true_multiplexing_only.setVisibility(visibility);
    	txt_exclude_these_expressions.setVisibility(visibility);
    	edit_exclude_these_expressions.setVisibility(visibility);
    	txt_include_these_expressions.setVisibility(visibility);
    	edit_include_these_expressions.setVisibility(visibility);
    	txt_passing_communication_delay.setVisibility(visibility);
    	edit_passing_communication_delay.setVisibility(visibility);
    }
    
    /** Handle "Run" action.
     * Create a string to pass to the siteswap generator.
     * @param v The View
     */
    public void onRunClick(View v) {
    	StringBuffer text = new StringBuffer(256);
        text.append(edit_balls.getText().toString() + " " + ((edit_max_throw.getText().length() > 0) ? edit_max_throw.getText().toString() : "-") + " " + ((edit_period.getText().length() > 0) ? edit_period.getText().toString() : "-"));
        text.append((spinner_rhythm.getSelectedItemPosition() == 1) ? " -s" : "");
        if (spinner_jugglers.getSelectedItemPosition() > 0) {
        	text.append(" -j " + spinner_jugglers.getSelectedItem().toString());
        	text.append((edit_passing_communication_delay.isEnabled() && edit_passing_communication_delay.getText().length() > 0) ? (" -d " + edit_passing_communication_delay.getText().toString() + " -l 1") : "");
        	text.append((cb_juggler_permutations.isEnabled() && cb_juggler_permutations.isChecked()) ? " -jp" : "");
        	text.append((cb_connected_patterns_only.isChecked()) ? " -cp" : "");
        }
        text.append((spinner_compositions.getSelectedItemPosition() == 0) ? " -f" : "");
        text.append((spinner_compositions.getSelectedItemPosition() == 2) ? " -prime" : "");
    	text.append((cb_ground_state_patterns.isChecked() && !cb_excited_state_patterns.isChecked()) ? " -g" : "");
    	text.append((!cb_ground_state_patterns.isChecked() && cb_excited_state_patterns.isChecked()) ? " -ng" : "");
    	text.append((cb_transition_throws.isEnabled() && !cb_transition_throws.isChecked()) ? " -se" : "");
    	text.append((cb_pattern_rotations.isChecked()) ? " -rot" : "");
    	if (cb_enable.isChecked() && edit_simultaneous_throws.getText().length()>0) {
    		text.append(" -m " + edit_simultaneous_throws.getText());
    		text.append((cb_no_simultaneous_catches.isChecked()) ? " -mf" : "");
    		text.append((cb_no_clustered_throws.isChecked()) ? " -mc" : "");
    		text.append((cb_true_multiplexing_only.isChecked()) ? " -mt" : "");
    	}
    	text.append((edit_exclude_these_expressions.getText().length() > 0) ? (" -x " + edit_exclude_these_expressions.getText()) : "");
    	text.append((edit_include_these_expressions.getText().length() > 0) ? (" -i " + edit_include_these_expressions.getText()) : "");
        Log.v("GeneratorActivity", text.toString());

        Intent i = new Intent(this, GeneratorListActivity.class);
        i.putExtra("pattern", text.toString());
        startActivity(i);
    }
    
    private OnItemSelectedListener itemSelectedListenerJugglers = new OnItemSelectedListener() {

	    @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	cb_connected_patterns_only.setEnabled(pos>0);
	    	cb_juggler_permutations.setEnabled(pos>0 && cb_ground_state_patterns.isChecked() && cb_excited_state_patterns.isChecked());
	    	edit_passing_communication_delay.setEnabled(pos>0 && cb_ground_state_patterns.isChecked() && !cb_excited_state_patterns.isChecked());
	    	txt_passing_communication_delay.setEnabled(pos>0 && cb_ground_state_patterns.isChecked() && !cb_excited_state_patterns.isChecked());
        }

	    @Override
        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    };
    
    private OnCheckedChangeListener checkedChangeListenerEnable = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			cb_no_simultaneous_catches.setEnabled(isChecked);
			cb_no_clustered_throws.setEnabled(isChecked);
			txt_simultaneous_throws.setEnabled(isChecked);
			edit_simultaneous_throws.setEnabled(isChecked);
			cb_true_multiplexing_only.setEnabled(isChecked);
		}
    	
    };

    private OnCheckedChangeListener checkedChangeListenerGroundExcitedStatePatterns = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    	edit_passing_communication_delay.setEnabled(cb_ground_state_patterns.isChecked() && !cb_excited_state_patterns.isChecked() && (spinner_jugglers.getSelectedItemPosition() > 0));
	    	txt_passing_communication_delay.setEnabled(cb_ground_state_patterns.isChecked() && !cb_excited_state_patterns.isChecked() && (spinner_jugglers.getSelectedItemPosition() > 0));
			cb_juggler_permutations.setEnabled(cb_ground_state_patterns.isChecked() && cb_excited_state_patterns.isChecked() && (spinner_jugglers.getSelectedItemPosition() > 0));
			cb_transition_throws.setEnabled(cb_excited_state_patterns.isChecked());
		}
    	
    };
}