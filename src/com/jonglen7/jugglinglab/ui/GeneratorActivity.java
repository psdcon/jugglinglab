package com.jonglen7.jugglinglab.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.generator.GeneratorTarget;
import com.jonglen7.jugglinglab.jugglinglab.generator.siteswapGenerator;
import com.jonglen7.jugglinglab.jugglinglab.util.JuggleExceptionUser;

public class GeneratorActivity extends Activity {
	
	/** Balls. */
	EditText edit_balls;
	
	/** Max. throw. */
	EditText edit_max_throw;

    /** Rhythm. */
    Spinner spinner_rhythm;

    /** Jugglers. */
    Spinner spinner_jugglers;
	
	/** Period. */
	EditText edit_period;

    /** Compositions. */
    Spinner spinner_compositions;
    
    /** Find. */
    CheckBox cb_ground_state_patterns;
    CheckBox cb_excited_state_patterns;
    CheckBox cb_transition_throws;
    CheckBox cb_pattern_rotations;
    CheckBox cb_juggler_permutations;
    CheckBox cb_connected_patterns_only;
    
    /** Multiplexing. */
    CheckBox cb_enable;
    TextView txt_simultaneous_throws;
    EditText edit_simultaneous_throws;
    CheckBox cb_no_simultaneous_catches;
    CheckBox cb_no_clustered_throws;
    CheckBox cb_true_multiplexing_only;
    
    /** Exclude / Include. */
    EditText edit_exclude_these_expressions;
    EditText edit_include_these_expressions;
    TextView txt_passing_communication_delay;
    EditText edit_passing_communication_delay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_generator);

    	/** Balls. */
    	edit_balls = (EditText)findViewById(R.id.generator_edit_balls);
    	
    	/** Max. throw. */
    	edit_max_throw = (EditText)findViewById(R.id.generator_edit_max_throw);
    	
    	/** Period. */
    	edit_period = (EditText)findViewById(R.id.generator_edit_period);

        /** Rhythm. */
    	spinner_rhythm = (Spinner) findViewById(R.id.generator_spinner_rhythm);
        ArrayAdapter adapter_rhythm = ArrayAdapter.createFromResource(this, R.array.rhythm, android.R.layout.simple_spinner_item);
        adapter_rhythm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rhythm.setAdapter(adapter_rhythm);
        //edit_rhythm = (EditText)findViewById(R.id.generator_edit_rhythm);

        /** Jugglers. */
    	spinner_jugglers = (Spinner) findViewById(R.id.generator_spinner_jugglers);
        spinner_jugglers.setOnItemSelectedListener(itemSelectedListenerJugglers);
        ArrayAdapter adapter_jugglers = ArrayAdapter.createFromResource(this, R.array.jugglers, android.R.layout.simple_spinner_item);
        adapter_jugglers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_jugglers.setAdapter(adapter_jugglers);
        
        /** Compositions. */
    	spinner_compositions = (Spinner) findViewById(R.id.generator_spinner_compositions);
        ArrayAdapter adapter_compositions = ArrayAdapter.createFromResource(this, R.array.compositions, android.R.layout.simple_spinner_item);
        adapter_compositions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_compositions.setAdapter(adapter_compositions);
        
        /** Find. */
        cb_ground_state_patterns = (CheckBox) findViewById(R.id.generator_cb_ground_state_patterns);
        cb_ground_state_patterns.setOnCheckedChangeListener(checkedChangeListenerGroundExcitedStatePatterns);
        cb_excited_state_patterns = (CheckBox) findViewById(R.id.generator_cb_excited_state_patterns);
        cb_excited_state_patterns.setOnCheckedChangeListener(checkedChangeListenerGroundExcitedStatePatterns);
        cb_transition_throws = (CheckBox) findViewById(R.id.generator_cb_transition_throws);
        cb_pattern_rotations = (CheckBox) findViewById(R.id.generator_cb_pattern_rotations);
        cb_juggler_permutations = (CheckBox) findViewById(R.id.generator_cb_juggler_permutations);
        cb_connected_patterns_only = (CheckBox) findViewById(R.id.generator_cb_connected_patterns_only);
        
        /** Multiplexing. */
        cb_enable = (CheckBox) findViewById(R.id.generator_cb_enable);
        cb_enable.setOnCheckedChangeListener(checkedChangeListenerEnable);
        txt_simultaneous_throws = (TextView)findViewById(R.id.generator_txt_simultaneous_throws);
        edit_simultaneous_throws = (EditText)findViewById(R.id.generator_edit_simultaneous_throws);
        cb_no_simultaneous_catches = (CheckBox) findViewById(R.id.generator_cb_no_simultaneous_catches);
        cb_no_clustered_throws = (CheckBox) findViewById(R.id.generator_cb_no_clustered_throws);
        cb_true_multiplexing_only = (CheckBox) findViewById(R.id.generator_cb_true_multiplexing_only);
        
        /** Exclude / Include. */
        edit_exclude_these_expressions = (EditText)findViewById(R.id.generator_edit_exclude_these_expressions);
        edit_include_these_expressions = (EditText)findViewById(R.id.generator_edit_include_these_expressions);
        txt_passing_communication_delay = (TextView)findViewById(R.id.generator_txt_passing_communication_delay);
        edit_passing_communication_delay = (EditText)findViewById(R.id.generator_edit_passing_communication_delay);
    }
    
    /** Handle "Run" action. */
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
        /*target.clearPatternList();
        siteswapGenerator sg = new siteswapGenerator();
        try {
			sg.initGenerator(text.toString());
			sg.runGenerator(target);
		} catch (JuggleExceptionUser e) {
			e.printStackTrace();
		}
        Log.v("GeneratorActivity", target.getPattern_list().toString());
        Intent i = new Intent(this, GeneratorListActivity.class);
        i.putExtra("pattern_list", target.getPattern_list());*/
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