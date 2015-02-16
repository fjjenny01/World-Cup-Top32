package com.example.top32show;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {
	TextView detail;
	TextView name;
	ImageView icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		detail = (TextView) findViewById(R.id.detail);
		name = (TextView) findViewById(R.id.country_name);
		icon = (ImageView) findViewById(R.id.picture);
		Bundle bundle = this.getIntent().getExtras();  

		String group = bundle.getString("position").substring(0, 1);
		String child = bundle.getString("position").substring(1);
		String path = bundle.getString("path");
		name.setText(bundle.getString("name"));
		Bitmap bmp = BitmapFactory.decodeFile(path);
		if (bmp != null)
		{
			icon.setImageBitmap(BitmapFactory.decodeFile(path));
		}
		else
		{
			icon.setImageResource(R.drawable.ic_launcher);
		}
		
		detail.setText("在本次世界杯32强小组赛中排在第" + group+  "组" + "第" + child + "个");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

}
