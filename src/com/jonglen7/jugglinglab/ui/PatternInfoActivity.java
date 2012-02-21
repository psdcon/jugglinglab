package com.jonglen7.jugglinglab.ui;

import java.util.HashMap;

import greendroid.app.GDActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;
import com.jonglen7.jugglinglab.util.DataBaseHelper;

public class PatternInfoActivity extends GDActivity {

	/** Display. */
	TextView txt_display_value;
	
	/** Pattern. */
	TextView txt_pattern_value;

	/** Hand movement. */
	TextView txt_hand_movement_code;
	TextView txt_hand_movement_display;

	/** Prop type. */
	TextView txt_prop_type_code;

	/** Dwell beats. */
	TextView txt_dwell_beats_code;

	/** Beats per second. */
	TextView txt_beats_per_second_code;

	/** Body movement. */
	TextView txt_body_movement_code;
	TextView txt_body_movement_display;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_info);

    	/** Display. */
        txt_display_value = (TextView) findViewById(R.id.pattern_info_txt_display_value);
    	
    	/** Pattern. */
        txt_pattern_value = (TextView) findViewById(R.id.pattern_info_txt_pattern_value);

    	/** Hand movement. */
        txt_hand_movement_code = (TextView) findViewById(R.id.pattern_info_txt_hand_movement_code);
        txt_hand_movement_display = (TextView) findViewById(R.id.pattern_info_txt_hand_movement_display);

    	/** Prop type. */
    	txt_prop_type_code = (TextView) findViewById(R.id.pattern_info_txt_prop_type_code);

    	/** Dwell beats. */
    	txt_dwell_beats_code = (TextView) findViewById(R.id.pattern_info_txt_dwell_beats_code);

    	/** Beats per second. */
    	txt_beats_per_second_code = (TextView) findViewById(R.id.pattern_info_txt_beats_per_second_code);

    	/** Body movement. */
        txt_body_movement_code = (TextView) findViewById(R.id.pattern_info_txt_body_movement_code);
        txt_body_movement_display = (TextView) findViewById(R.id.pattern_info_txt_body_movement_display);
    	
        PatternRecord pattern_record = (PatternRecord) getIntent().getExtras().getParcelable("pattern_record");
        HashMap<String, String> pattern_record_values = PatternRecord.animToValues(pattern_record.getAnim());

    	/** Display. */
        String display = pattern_record.getDisplay();
        setTitle(display);
        txt_display_value.setText(display);
        // TODO Romain (PatternInfo): Possibility to (re)name a trick

    	/** Pattern. */
        txt_pattern_value.setText(pattern_record_values.get("pattern"));

    	/** Hand movement. */
        String handsCode = getHandsCode(pattern_record_values.get("hands"));
        String handsDisplay = getHandsDisplay(handsCode);
        txt_hand_movement_code.setText(handsCode);
        txt_hand_movement_display.setText(handsDisplay);
        // TODO Romain (PatternInfo): Possibility to (re)name a hand movement and to add/remove it to/from the list

    	/** Prop type. */
    	txt_prop_type_code.setText(pattern_record_values.get("prop"));

    	/** Dwell beats. */
    	txt_dwell_beats_code.setText(pattern_record_values.get("dwell"));

    	/** Beats per second. */
    	txt_beats_per_second_code.setText(pattern_record_values.get("bps"));

    	/** Body movement. */
        String bodyCode = getBodyCode(pattern_record_values.get("body"));
        String bodyDisplay = getBodyDisplay(bodyCode);
        txt_body_movement_code.setText(bodyCode);
        txt_body_movement_display.setText(bodyDisplay);
        // TODO Romain (PatternInfo): Possibility to (re)name a body movement and to add/remove it to/from the list

    }
    
    // TODO Romain (PatternInfo): Factorize get{Hands,Body}Code() and get{Hands,Body}Display()
    
    private String getHandsCode(String code) {
    	if (code.length() == 0) code = "(10)(32.5).";  // TODO Romain (PatternInfo): Get the default value from somewhere
    	return code;
    }

    private String getHandsDisplay(String code) {
    	String display = "";
		DataBaseHelper myDbHelper = DataBaseHelper.init(this);

		Cursor cursor = null;
		String query = "SELECT H.XML_LINE_NUMBER, H.CUSTOM_DISPLAY " +
					"FROM Hands H " +
					"WHERE H.CODE = '" + code + "'";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			String[] hand_movement = this.getResources().getStringArray(R.array.hand_movement);

			String custom_display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
			if (custom_display != null) {
				display = custom_display;
			}
			else {
				int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
				if (xml_line_number > 0) {
					display = hand_movement[xml_line_number];
				}
			}
			
		}
		
		cursor.close();
        myDbHelper.close();
		
    	return display;
    }
    
    private String getBodyCode(String code) {
    	if (code.length() == 0) code = "";  // TODO Romain (PatternInfo): Get the default value from somewhere
    	return code;
    }

    private String getBodyDisplay(String code) {
    	String display = "";
		DataBaseHelper myDbHelper = DataBaseHelper.init(this);

		Cursor cursor = null;
		String query = "SELECT B.XML_LINE_NUMBER, B.CUSTOM_DISPLAY " +
					"FROM BODY B " +
					"WHERE B.CODE = '" + code + "'";
		cursor = myDbHelper.execQuery(query);
		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			String[] body_movement = this.getResources().getStringArray(R.array.body_movement);

			String custom_display = cursor.getString(cursor.getColumnIndex("CUSTOM_DISPLAY"));
			if (custom_display != null) {
				display = custom_display;
			}
			else {
				int xml_line_number = cursor.getInt(cursor.getColumnIndex("XML_LINE_NUMBER"));
				if (xml_line_number > 0) {
					display = body_movement[xml_line_number];
				}
			}
			
		}
		
		cursor.close();
        myDbHelper.close();
		
    	return display;
    }

}
