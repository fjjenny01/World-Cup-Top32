package com.example.top32show;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * MyAdapter extends BaseExpandaleListAdapter. The expandlelist expands all groups
 * in default. The list displays an image, Chinese name and a delete button for each
 * child item. You can choose the "skip" checkbox to skip asking "你确定删除吗".
 * @author jennyfang
 *
 */
public class MyAdapter extends BaseExpandableListAdapter {

	private static String TAG = "Jenny";
	LayoutInflater mInflater;
	List<Group> groupData;
	Context context;
	View alertDialogView;
	CheckBox donShowAgain;
	boolean skip = false;
	File file;
	ImageLoader mImageLoader;
	private boolean mIsFling;
	int addCount = 0;
	

	public MyAdapter(Context context, List<Group> groupData, File file) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.groupData = groupData;
		this.file = file;
		this.mImageLoader = ImageLoader.getInstance(context, handler);
	}

	@Override
	public int getGroupCount() {

		return groupData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupData.get(groupPosition).members.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupData.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupData.get(groupPosition).members.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		TextView textView = getGenericView();
		Group group = (Group) getGroup(groupPosition);
		textView.setText(group.groupName
				+ "                                                                       "
				+ "(" + group.members.size() + ")");

		return textView;
	}

	public TextView getGenericView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 64);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(36, 0, 0, 0);
		return textView;
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView name;
		public Button deleteBtn;
		public String url;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		Country country = groupData.get(groupPosition).getMember(childPosition);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.child, null);

			holder.img = (ImageView) convertView.findViewById(R.id.icon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.deleteBtn = (Button) convertView
					.findViewById(R.id.delete_btn);
			holder.url = country.largeUrl;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String dir = "sdcard/namecard";
		String picFileName = country.getImgUrl().substring(
				country.getImgUrl().lastIndexOf("/") + 1);
		if (isFling()) {
			holder.img.setImageResource(R.drawable.ic_launcher);
		} else if (MyFile.isFileExisting(dir, picFileName)) {
			holder.img.setImageBitmap(ImageFileCache.getImage(dir, picFileName,
					70, 70));
		} else {
			holder.img.setImageResource(R.drawable.ic_launcher);
			if (!mImageLoader.isTaskExist(picFileName)) {
				 mImageLoader.addTask(country.getLargeUrl(), dir, picFileName);
			}
		}

		holder.name.setText(groupData.get(groupPosition).members.get(
				childPosition).getChineseName());

		holder.deleteBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialogView = mInflater.inflate(R.layout.alertdialog, null);
				donShowAgain = (CheckBox) alertDialogView
						.findViewById(R.id.skip);
				if (!skip) {
					new AlertDialog.Builder(context)
							.setView(alertDialogView)
							.setIconAttribute(android.R.attr.alertDialogIcon)
							.setTitle("你确定要删除" + groupData.get(groupPosition).getMember(childPosition).getChineseName() + "？")
							.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
											if (donShowAgain.isChecked()) {
												skip = true;
											}
											deleteData(groupData.get(groupPosition).getMember(childPosition).getId());
											groupData.get(groupPosition).members.remove(childPosition);
											notifyDataSetChanged();
										}

									})
							.setNegativeButton(R.string.alert_dialog_cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
										}
									}).create().show();
				} else {
					groupData.get(groupPosition).members.remove(childPosition);
					notifyDataSetChanged();
				}

			}
		});

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * The method deletes the node with certain id(if exists) in file.
	 * @param id
	 */
	private void deleteData(String id) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			inputStream = new FileInputStream(file);
			document = builder.parse(inputStream);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Element root = document.getDocumentElement();
		NodeList nodes = root.getElementsByTagName("country");
		int i;
		for (i = 0; i < nodes.getLength(); i++) {
			Element countryElement = (Element) (nodes.item(i));
			if (countryElement.getAttribute("id").equals(id)) {
				break;

			}
		}
		if (i < nodes.getLength()) {
			nodes.item(i).getParentNode().removeChild(nodes.item(i));
			try {
				StreamResult result = new StreamResult(file);
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			notifyDataSetChanged();
			Log.i("James", "HandleMessage notifyDataSetChanged");
		};
	};
	
	public boolean isFling() {
		return mIsFling;
	}

	public void setFling(boolean mIsFling) {
		this.mIsFling = mIsFling;
	}
	
	public void clearWaitingQueue()
	{
		mImageLoader.waitingQueue.clear();
	}

}
