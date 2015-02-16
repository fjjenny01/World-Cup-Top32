package com.example.top32show;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;


/**
 * This class is the main activity and implements the interface NetObserver. 
 * @author jennyfang
 *
 */
public class MainActivity extends Activity implements NetObserver{


	private static String TAG = "Jenny";
	List<Group> groupData;
	File file = new File("sdcard/namecard2/country2.xml");   // 备份xml文件
	ImageLoader mImageLoader;
	MyAdapter mAdapter;
	NetworkChangeReceiver mReceiver;
	SQLiteDatabase db;  // database
	final String DELETE_TABLE_SQL = "drop table if exists countries";
	final String CREATE_TABLE_SQL = 
			"create table if not exists countries (_id integer primary key autoincrement , name varchar(20), hobby varchar(255))";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		mAdapter = new MyAdapter(this, groupData, file);
		mImageLoader  = ImageLoader.getInstance(this, mAdapter.handler);
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.list);
		
		
		expandableListView.setAdapter(mAdapter);
		mReceiver = new NetworkChangeReceiver();
		registerReceiver(mReceiver, new IntentFilter());
		mReceiver.registerNetObserver(this);

		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Country country = groupData.get(groupPosition).getMember(childPosition);
				String path = "sdcard/namecard/" +     
                        country.getImgUrl().substring(country.getImgUrl().lastIndexOf("/") + 1);
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				Bundle bundle = new Bundle();
				String position = country.getImgUrl().substring(country.getImgUrl().lastIndexOf("/") + 1,country.getImgUrl().lastIndexOf("."));
				bundle.putString("position", position);
				bundle.putString("name", country.chineseName);
				bundle.putString("path", path);
				intent.putExtras(bundle);
				startActivity(intent);
				
				return true;
			}
		});
		for(int i = 0; i < groupData.size(); i++)
		{
			expandableListView.expandGroup(i);
		}
		expandableListView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_FLING:
                	mAdapter.setFling(true);
                	mAdapter.clearWaitingQueue();
                    break;
                case OnScrollListener.SCROLL_STATE_IDLE:
                	mAdapter.setFling(false);
                	mAdapter.notifyDataSetChanged();
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                	mAdapter.setFling(false);
                    break;
                default:
                    break;
            }				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {	
			}
		});			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	/**
	 * get all data and initiate list of childData from xml file 
	 */
	private void init() {

		List<Country> childData = getAllDataFromXML(MyFile.isFileExisting("sdcard/namecard2", "country2.xml"));
		
		
		if (childData.size() > 0) {
		    Collections.sort(childData, new Comparator<Country>() {
		        @Override
		        public int compare(final Country object1, final Country object2) {
		            return object1.firstLetter.compareTo(object2.firstLetter);
		        }
		       } );
		}
		groupData = new ArrayList<Group>();
		groupData = divideDataByFirstLetter(childData);

	}

	/**
	 * divide  the list childData by first letter of name into groups and then sort all groups
	 * @param childData
	 * @return List<Group>
	 */
	private List<Group> divideDataByFirstLetter(List<Country> childData) {

		HashMap<String, Group> hm = new HashMap<String, Group>();
		
		for(Country country: childData)
		{
			String key = country.getFirstLetter();
			if(!hm.containsKey(key))
			{
				Group newGroup = new Group();
				newGroup.groupName = key;
				newGroup.members = new ArrayList<Country>();
				newGroup.members.add(country);
				hm.put(key, newGroup);
			}
			else
			{
				hm.get(key).members.add(country);
			}
		}
		ArrayList<Group> list = new ArrayList<Group>();
		for (String key: hm.keySet())
		{
			list.add(hm.get(key));
		}
		
		if (list.size() > 0) {
			
		    Collections.sort(list, new Comparator<Group>() {
				@Override
				public int compare(Group group1, Group group2) {
					return group1.groupName.compareTo(group2.groupName);
				}
		       } );
		}
		return list;
	}

	public void getDataFromXMLToDatabase(boolean hasCopy)
	{
		try {
			db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir() + "/my.db3", null);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}
	/**
	 * If the copy file exists in sdcard folder, read from the copy file.
	 * If not, make one copy to sdcard folder. 
	 * Store the data in sdcard instead of /asset to make the permanent operation on data.
	 * @param hasCopy
	 * @return
	 */
	private List<Country> getAllDataFromXML(boolean hasCopy) {

		List<Country> countries = new ArrayList<Country>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		FileOutputStream  out = null;
		InputStream in = null;
		
		
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		//如果没有copy，从asset/country.xml 读取， copy到 country2.xml
		if (!hasCopy)
		{

			try {
				inputStream = this.getResources().getAssets().open("country.xml");
				out = new FileOutputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			MyFile.copyFile(inputStream, out);
			
		}
		try {
			in = new FileInputStream(file);
			document = builder.parse(in);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = document.getDocumentElement();
		NodeList nodes = root.getElementsByTagName("country");
		Country country =  null;
		for(int i=0;i < nodes.getLength();i++){
			country = new Country();
	        Element countryElement=(Element)(nodes.item(i));
	        country.setId(countryElement.getAttribute("id"));
	        Element chineseName = (Element)countryElement.getElementsByTagName("chineseName").item(0);
	        country.setChineseName(chineseName.getFirstChild().getNodeValue());
	        Element firstLetter = (Element)countryElement.getElementsByTagName("firstLetter").item(0);
	        country.setFirstLetter(firstLetter.getFirstChild().getNodeValue());
	        Element imgUrl = (Element) countryElement.getElementsByTagName("imgUrl").item(0);
	        Element largeUrl = (Element) countryElement.getElementsByTagName("imgLarge").item(0);
	        country.setLargeUrl(largeUrl.getFirstChild().getNodeValue()); // 换地址
	        country.setImgUrl(imgUrl.getFirstChild().getNodeValue());
	        countries.add(country);
	     }
		
        try{
           in.close();
        } catch (IOException e) {    
           e.printStackTrace();
        }
		return countries;
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem deletePic = menu.findItem(R.id.delete_pic);
		deletePic.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem item){
				MyFile.deleteAllFiles("sdcard/namecard");
	            return true;
	        }
		});
		return true;
	}

	@Override
	public void notifyDataChanged() {
		mAdapter.notifyDataSetChanged();
	}

}
