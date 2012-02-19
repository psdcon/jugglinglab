package com.jonglen7.jugglinglab.widget;

import greendroid.widget.NormalActionBarItem;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.cyrilmottier.android.greendroid.R;
import com.jonglen7.jugglinglab.util.Collection;
import com.jonglen7.jugglinglab.util.Trick;

public class StarActionBarItem extends NormalActionBarItem {

    private CheckBox mCheckBox;
    private Trick trick;

    public StarActionBarItem() {
    }
    
    public void setTrick(Trick trick) {
    	super.getItemView();  // To prevent NullPointerException
    	this.trick = trick;
    	boolean isStarred = false;
    	for (Collection collection : trick.getCollections()) {
    		if (collection.isStarred()) isStarred = true;
    	}
    	this.mCheckBox.setChecked(isStarred);
	}

    @Override
    protected void prepareItemView() {
        super.prepareItemView();
        mCheckBox = (CheckBox) mItemView.findViewById(R.id.btn_star);
    }

    @Override
    protected View createItemView() {
        return LayoutInflater.from(mContext).inflate(R.layout.action_bar_item_star, mActionBar, false);
    }
    
	@Override
    protected void onItemClicked() {
        super.onItemClicked();
    	mCheckBox.setChecked(!mCheckBox.isChecked());
    	trick.star();
    }
	
}
