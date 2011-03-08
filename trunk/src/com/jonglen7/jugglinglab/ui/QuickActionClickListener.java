package com.jonglen7.jugglinglab.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.jonglen7.jugglinglab.R;
import com.jonglen7.jugglinglab.jugglinglab.core.PatternRecord;

public class QuickActionClickListener implements OnItemLongClickListener, OnClickListener {
	
	Context context;
	ArrayList<PatternRecord> pattern_list;
	
	private static final long HOUR = 3600 * 1000;

	private static final long DAY = HOUR * 24;

	private static final int HOURS = 24;

	public QuickActionClickListener(ArrayList<PatternRecord> pattern_list) {
		this.pattern_list = pattern_list;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final PatternRecord pattern_record = pattern_list.get(position);
		return createQuickAction(view, pattern_record);
	}
	
	@Override
	public void onClick(View view) {
		final PatternRecord pattern_record = pattern_list.get(0);
		createQuickAction(view, pattern_record);
	}
	
	private boolean createQuickAction(View view, final PatternRecord pattern_record) {
		context = view.getContext();
		QuickAction qa = new QuickAction(view);
		
		ActionItem fav = new ActionItem();
    	fav.setTitle(context.getString(R.string.quickactions_fav));
    	fav.setIcon(context.getResources().getDrawable(R.drawable.fav));
    	fav.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Fav selected" , Toast.LENGTH_SHORT).show();
    		}
    	});
    	
    	ActionItem practising = new ActionItem();
    	practising.setTitle(context.getString(R.string.quickactions_catches));
    	practising.setIcon(context.getResources().getDrawable(R.drawable.catches));
    	practising.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Catches selected", Toast.LENGTH_SHORT).show();
    		}
    	});
    	
    	ActionItem record = new ActionItem();
    	record.setTitle(context.getString(R.string.quickactions_goal));
    	record.setIcon(context.getResources().getDrawable(R.drawable.goal));
    	record.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Goal selected" , Toast.LENGTH_SHORT).show();
    		}
    	});

    	ActionItem share = new ActionItem();
    	share.setTitle(context.getString(R.string.quickactions_share));
    	share.setIcon(context.getResources().getDrawable(R.drawable.share));
    	share.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Share selected " , Toast.LENGTH_SHORT).show();
    			//share("Juggling Lab", context.getString(R.string.quickactions_share_working) + " " + pattern_record.getDisplay() + " " + context.getString(R.string.quickactions_share_thanks));
    			String subject = "Juggling Lab";
    			String text = context.getString(R.string.quickactions_share_working) + " " + pattern_record.getDisplay() + " " + context.getString(R.string.quickactions_share_thanks);
    			Intent shareIntent = createShareIntent(subject, text);
    	    	context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.quickactions_share)));
    		}
    	});

    	ActionItem stats = new ActionItem();
    	stats.setTitle(context.getString(R.string.quickactions_stats));
    	stats.setIcon(context.getResources().getDrawable(R.drawable.stats));
    	stats.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Toast.makeText(context, "Stats selected" , Toast.LENGTH_SHORT).show();
    	    	Intent achartIntent = createStatsIntent(context, pattern_record);
    	    	context.startActivity(achartIntent);
    		}
    	});
		
		qa.addActionItem(fav);
		qa.addActionItem(practising);
		qa.addActionItem(record);
		qa.addActionItem(share);
		qa.addActionItem(stats);
		qa.setAnimStyle(QuickAction.ANIM_AUTO);
		
		qa.show();
		return true;
	}

	private Intent createShareIntent(String subject, String text) {
    	 final Intent intent = new Intent(Intent.ACTION_SEND);

    	 intent.setType("text/plain");
    	 intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	 intent.putExtra(Intent.EXTRA_TEXT, text);

    	 return intent;
    }
    
    private Intent createStatsIntent(Context context, PatternRecord pattern_record) {
    	String[] titles = new String[] { "Goal", "Catches" };
        long now = Math.round(new Date().getTime() / DAY) * DAY;
        List<Date[]> x = new ArrayList<Date[]>();
        for (int i = 0; i < titles.length; i++) {
          Date[] dates = new Date[HOURS];
          for (int j = 0; j < HOURS; j++) {
            dates[j] = new Date(now - (HOURS - j) * HOUR);
          }
          x.add(dates);
        }
        List<double[]> values = new ArrayList<double[]>();

        values.add(new double[] { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
            30, 30, 30, 30, 30, 30, 30, 40, 40, 40, 40, 40, 40 });
        values.add(new double[] { 12, 14, 10, 17, 13,  9, 15, MathHelper.NULL_VALUE, MathHelper.NULL_VALUE, 18, 23,
        	14, 21, 19, 23, 28, 25, 31, 27, 28, 24, MathHelper.NULL_VALUE, 29, 35, 36 });

        int[] colors = new int[] { Color.GREEN, Color.BLUE };
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND };
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
          ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        setChartSettings(renderer, pattern_record.getDisplay(), "Hour", "Catches", x.get(0)[0]
            .getTime(), x.get(0)[HOURS - 1].getTime(), -5, 30, Color.LTGRAY, Color.LTGRAY);
        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);
        Intent intent = ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, x, values),
            renderer, "h:mm a");
        return intent;
      }

    /**
     * Builds an XY multiple series renderer.
     * 
     * @param colors the series rendering colors
     * @param styles the series point styles
     * @return the XY multiple series renderers
     */
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
      XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
      renderer.setAxisTitleTextSize(16);
      renderer.setChartTitleTextSize(20);
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      renderer.setPointSize(5f);
      renderer.setMargins(new int[] { 20, 30, 15, 0 });
      int length = colors.length;
      for (int i = 0; i < length; i++) {
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(colors[i]);
        r.setPointStyle(styles[i]);
        renderer.addSeriesRenderer(r);
      }
      return renderer;
    }

    /**
     * Sets a few of the series renderer settings.
     * 
     * @param renderer the renderer to set the properties to
     * @param title the chart title
     * @param xTitle the title for the X axis
     * @param yTitle the title for the Y axis
     * @param xMin the minimum value on the X axis
     * @param xMax the maximum value on the X axis
     * @param yMin the minimum value on the Y axis
     * @param yMax the maximum value on the Y axis
     * @param axesColor the axes color
     * @param labelsColor the labels color
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
        String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
        int labelsColor) {
      renderer.setChartTitle(title);
      renderer.setXTitle(xTitle);
      renderer.setYTitle(yTitle);
      renderer.setXAxisMin(xMin);
      renderer.setXAxisMax(xMax);
      renderer.setYAxisMin(yMin);
      renderer.setYAxisMax(yMax);
      renderer.setAxesColor(axesColor);
      renderer.setLabelsColor(labelsColor);
    }

    /**
     * Builds an XY multiple time dataset using the provided values.
     * 
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple time dataset
     */
    protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
        List<double[]> yValues) {
      XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
      int length = titles.length;
      for (int i = 0; i < length; i++) {
        TimeSeries series = new TimeSeries(titles[i]);
        Date[] xV = xValues.get(i);
        double[] yV = yValues.get(i);
        int seriesLength = xV.length;
        for (int k = 0; k < seriesLength; k++) {
          series.add(xV[k], yV[k]);
        }
        dataset.addSeries(series);
      }
      return dataset;
    }
}
