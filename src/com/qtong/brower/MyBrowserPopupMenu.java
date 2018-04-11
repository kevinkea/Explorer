package com.qtong.brower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewConstructor")
public class MyBrowserPopupMenu extends PopupWindow {

	/**
	 * 菜单栏的整体布局LinearLayout
	 */
	private LinearLayout linearLayout;

	/**
	 * 菜单栏分类标题布局GridView
	 */
	private GridView gv_title;

	/**
	 * 菜单栏功能布局GridView
	 */
	private ListView lv_body;

	/**
	 * 菜单栏功能图标与名称GridView
	 */
	private GridView gv_body;

	/**
	 * 菜单栏功能图标与名称GridView的适配
	 */
	private BrowerBodyAdapter[] bodyAdapter;

	/**
	 * 菜单栏分类标题GridView的适配
	 */
	private BrowerTitleAdapter titleAdapter;

	private Context context;

	/**
	 * 当前选中的分类标题
	 */
	private int currentIndex = 0;

	/**
	 * 上一次选中的分类标题 用于选择分类标题时的左右移动动画，判断应该怎样移动
	 */
	private int preIndex = 0;

	/**
	 * 标题与功能布局中间的分界线 RelativeLayout + TextView
	 */
	private RelativeLayout divisionLayout;

	/**
	 * 屏幕宽度
	 */
	private int screenWidth = 0;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public MyBrowserPopupMenu(final Context context, List<String> titles,
			final List<List<String>> item_names, List<List<Integer>> item_images) {
		super(context);
		this.context = context;

		/**
		 * 菜单栏的整体布局LinearLayout初始化
		 */
		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(20, 20, 20, 20);// 4个参数按顺序分别是左上右下
		linearLayout.setLayoutParams(layoutParams);
		/**
		 * 获取屏幕宽度
		 */
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();

		/**
		 * 分界线布局初始化
		 */
		// divisionLayout = new RelativeLayout(context);
		// divisionLayout.setLayoutParams(new LayoutParams(
		// LayoutParams.MATCH_PARENT, 1));
		// divisionLayout.setBackgroundColor(Color.DKGRAY);

		// /**
		// * 标题布局初始化
		// */
		// gv_title = new GridView(context);

		// /**
		// * 用于重新初始化adapter
		// */
		// final List<String> l = titles;
		// final Context c = context;
		//
		// titleAdapter = new TitleAdapter(titles, context, 0);

		// /**
		// * 设置被选中后，背景颜色不再是系统原有的黄色，改为TRANSPARENT
		// */
		// gv_title.setSelector(new ColorDrawable(Color.WHITE));
		// gv_title.setAdapter(titleAdapter);
		// /**
		// * 设置GridView列数
		// */
		// gv_title.setNumColumns(titleAdapter.getCount());
		// gv_title.setBackgroundColor(Color.BLUE);
		//
		// /**
		// * 选择分类标题时的响应事件
		// */
		// gv_title.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// /**
		// * 重新初始化adapter，为了改变标题选择颜色
		// */
		// titleAdapter = new TitleAdapter(l, c, position);
		//
		// preIndex = currentIndex;
		// currentIndex = position;
		//
		// gv_title.setAdapter(titleAdapter);
		//
		// /**
		// * 分界线布局中的textView跟随选中标题移动位置的，设置为动画效果
		// */
		// divisionTran(position);
		//
		// /**
		// * 用于功能图标GridView动画效果 TranslateAnimation方法中的参数设置暂时不太明确
		// * 似乎，参数都是相对于控件自身的位置 第一个参数是开始位置，第二个是结束位置 有时间会弄清楚
		// */
		// Animation translateBody;
		// if (preIndex < currentIndex) {
		// translateBody = new TranslateAnimation(screenWidth, 0, 0, 0);
		// translateBody.setDuration(500);
		// gv_body.startAnimation(translateBody);
		// } else if (preIndex > currentIndex) {
		// translateBody = new TranslateAnimation(-screenWidth, 0, 0,
		// 0);
		// translateBody.setDuration(500);
		// gv_body.startAnimation(translateBody);
		// }
		//
		// gv_body.setAdapter(bodyAdapter[position]);
		//
		// }
		// });

		// bodyAdapter = new BodyAdapter[item_names.size()];
		// for (int i = 0; i < item_names.size(); i++) {
		// bodyAdapter[i] = new BodyAdapter(context, item_names.get(i),
		// item_images.get(i));
		// }
		// gv_body = new GridView(context);
		// gv_body.setNumColumns(4);
		// gv_body.setBackgroundColor(Color.TRANSPARENT);
		// gv_body.setPadding(20, 20, 20, 20);
		// gv_body.setAdapter(bodyAdapter[0]);
		//
		 /**
		 * 选择功能图标时的响应事件
		 */
//		 gv_body.setOnItemClickListener(new OnItemClickListener() {
//		
//			 @Override
//			 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				 /**
//				 * 这里只是在控制台输出了一下功能的名称
//				 */
//				 switch (position) {
//					 case 0:
//						 WebActivity.browerReflesh();
//					 break;
//					 case 1:
//						 WebActivity.browerMenuClose();
//					 break;
//					 case 2:
//						 Intent intent = new Intent();
//						 intent.setAction("android.intent.action.VIEW");
//						 Uri content_url = Uri.parse(WebActivity.browerOpen());
//						 intent.setData(content_url);
//						 context.startActivity(intent);
//					 break;
//					 case 3:
//					 break;
//					 default:
//					 break;
//				 }
//				 System.out.println(item_names.get(currentIndex).get(position));
//			 }
//		 });

		/**
		 * 初始化textView位置
		 */
		// divisionTran(0);
		/**
		 * 初始化lv_body位置
		 */
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < item_names.get(0).size(); i++) {
			Map<String, Object> listem = new HashMap<String, Object>();
			listem.put("name", item_names.get(0).get(i));
			listems.add(listem);
		}
		lv_body = new ListView(context);
		lv_body.setAdapter(new SimpleAdapter(context, listems,
				R.layout.activity_main_listview_item, new String[] { "name" },
				new int[] { R.id.main_brower_listview_name }));
//		lv_body.setBackgroundColor(Color.TRANSPARENT);
//		lv_body.setBackground(context.getDrawable(R.drawable.gv_title_background));
		lv_body.setDivider(new ColorDrawable(Color.BLACK));
		lv_body.setDividerHeight(1);
		lv_body.setPadding(1, 1, 1, 1);
		lv_body.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
//				case 0:
//					BrowerActivity.browerCopy();
//					Toast.makeText(context, "已将地址复制到剪切板", 2000).show();
//					break;
				case 0:
					
					WebActivity.browerReflesh();
					break;
//				case 2:
//					Intent intent = new Intent();
//					intent.setAction("android.intent.action.VIEW");
//					Uri content_url = Uri.parse(BrowerActivity.browerOpen());
//					intent.setData(content_url);
//					context.startActivity(intent);
//					break;
//				case 3:
//					// 判断是否开启gps
//					LocationManager locationManager = (LocationManager) context
//							.getSystemService(Context.LOCATION_SERVICE);
//					if (!locationManager
//							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//						//如果未开启gps，进入gps设置界面
//						Intent intent1 = new Intent();
//						intent1.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						try {
//							context.startActivity(intent1);
//
//						} catch (ActivityNotFoundException ex) {
//
//							// The Android SDK doc says that the location
//							// settings activity
//							// may not be found. In that case show the general
//							// settings.
//
//							// General settings activity
//							intent1.setAction(Settings.ACTION_SETTINGS);
//							try {
//								context.startActivity(intent1);
//							} catch (Exception e) {
//							}
//						}
//					}
//					BrowerActivity.browerMenuClose();
//					break;
				case 1:
					WebActivity.browerMenuClose();
					break;
				case 2:
					((Activity) context).finish();
					break;
//				case 3:
//					UMImage image = new UMImage(context,
//							"http://www.umeng.com/images/pic/social/integrated_3.png");
//					new ShareAction((Activity) context)
//							.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ,
//									SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,
//									SHARE_MEDIA.WEIXIN_CIRCLE)
//							.withText("来自友盟分享面板").withMedia(image)
//							.setCallback(new UMShareListener() {
//								@Override
//								public void onResult(SHARE_MEDIA platform) {
//									Log.d("plat", "platform" + platform);
//									Toast.makeText(context,
//											platform + " 分享成功啦",
//											Toast.LENGTH_SHORT).show();
//								}
//
//								@Override
//								public void onError(SHARE_MEDIA platform,
//										Throwable t) {
//									Toast.makeText(context,
//											platform + " 分享失败啦",
//											Toast.LENGTH_SHORT).show();
//								}
//
//								@Override
//								public void onCancel(SHARE_MEDIA platform) {
//									Toast.makeText(context,
//											platform + " 分享取消了",
//											Toast.LENGTH_SHORT).show();
//								}
//							}).open();
//					BrowerActivity.browerMenuClose();
//					break;
				default:
					break;
				}
			}
		});
		// for (int i = 0; i < item_names.size(); i++) {
		// bodyAdapter[i] = new BodyAdapter(context, item_names.get(i),
		// item_images.get(i));
		// }
		// gv_body = new GridView(context);
		// gv_body.setNumColumns(4);
		// gv_body.setBackgroundColor(Color.TRANSPARENT);
		// gv_body.setPadding(20, 20, 20, 20);
		// gv_body.setAdapter(bodyAdapter[0]);

		/**
		 * 把三个子布局加入到整体布局中去
		 */
		// linearLayout.addView(gv_title);
		// linearLayout.addView(divisionLayout);
		// linearLayout.addView(gv_body);
		linearLayout.addView(lv_body);

		this.setContentView(linearLayout);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);

		/**
		 * 以下代码是为了解决，菜单栏出现后，不能响应再次按menu按键使菜单栏消失的问题
		 * 在这个网址找到的答案http://blog.csdn.net/admin_/article/details/7278402 可以自己去看
		 */
		this.setFocusable(true);
		linearLayout.setFocusableInTouchMode(true);
		linearLayout.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU)
						&& (MyBrowserPopupMenu.this.isShowing())) {
					MyBrowserPopupMenu.this.dismiss();
					// titleAdapter = new TitleAdapter(l, c, 0);
					// gv_title.setAdapter(titleAdapter);
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * 分界线布局中的textView跟随选中标题移动位置的，设置为动画效果
	 */
	public void divisionTran(int position) {

		/**
		 * 先移除了RelativeLayout中原有的textView
		 */
		divisionLayout.removeAllViews();

		/**
		 * 重新设置textView布局属性 动态改变控件位置 第一步
		 */
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				screenWidth / 3, LayoutParams.MATCH_PARENT);

		/**
		 * 设置动画效果
		 */
		Animation translateTextView;
		translateTextView = new TranslateAnimation((preIndex - currentIndex)
				* screenWidth / 3, 0, 0, 0);

		/**
		 * 根据选中的标题确定布局 动态改变控件位置 第二步
		 */
		if (position == 0) {
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		} else if (position == 1) {
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		} else {
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		}

		/**
		 * 动态改变控件位置 第三步
		 */
		TextView line = new TextView(context);
		line.setBackgroundColor(Color.WHITE);
		divisionLayout.addView(line, lp);

		/**
		 * 设置动画执行时间
		 */
		translateTextView.setDuration(200);

		/**
		 * 启动动画
		 */
		line.startAnimation(translateTextView);
	}

}