package com.qtong.brower;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class BrowerTitleAdapter extends BaseAdapter {

	private List<String> titles;
	private Context context;
	public TextView[] tv_titles;
	int position = 0;

	public BrowerTitleAdapter(List<String> titles, Context context, int position) {
		this.titles = titles;
		this.context = context;
		tv_titles = new TextView[titles.size()];
		this.position = position;
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		tv_titles[position] = new TextView(context);

		tv_titles[position].setGravity(Gravity.CENTER);

		tv_titles[position].setText(titles.get(position));

		tv_titles[position].setTextSize(20);

		tv_titles[position].setLayoutParams(new GridView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		tv_titles[position].setPadding(20, 50, 20, 50);

		if (position == this.position) {
			tv_titles[position].setTextColor(Color.WHITE);
		} else {
			tv_titles[position].setTextColor(Color.GRAY);
		}
		return tv_titles[position];
	}

}
