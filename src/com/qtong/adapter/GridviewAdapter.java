package com.qtong.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.qtong.bean.bean;
import com.qtong.brower.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GridviewAdapter extends BaseAdapter {
	private Context context;
	private List<bean> listitem;
	private LayoutInflater mLayoutInflater;
	public GridviewAdapter(Context context, List<bean> listitem) {
		this.context = context;
		this.listitem = listitem;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listitem.size();
	}

	@Override
	public Object getItem(int position) {
		return listitem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView==null){
			holder=new ViewHolder();
			mLayoutInflater = LayoutInflater.from(context);
			convertView=mLayoutInflater.inflate(R.layout.gridview_item, null);
			holder.image=(ImageView) convertView.findViewById(R.id.image);
			holder.textView=(TextView) convertView.findViewById(R.id.textView);
			holder.url=(TextView) convertView.findViewById(R.id.url);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
//		holder.image.setBackgroundResource(listitem.get(position).getIcon());
		LoadImage loadImage = new LoadImage(context,holder.image,listitem.get(position).getIcon());  
		loadImage.execute();
		holder.textView.setText(listitem.get(position).getAppName());
		holder.url.setText(listitem.get(position).getFrontUrl());
		return convertView;
	}

	public class ViewHolder{
		private ImageView  image;
		private TextView  textView;
		private TextView  url;
	}
	class LoadImage extends AsyncTask<Void,Integer,Bitmap>{  
        private Context context;
		private ImageView imageView;
		private String imageurl;
		private HttpURLConnection uRLConnection;
		private URL url;
        LoadImage(Context context,ImageView imageView,String imageurl) {  
            this.context = context; 
            this.imageView = imageView;
            this.imageurl = imageurl;
        }  
        
        @Override  
        protected void onPreExecute() {  
//            Toast.makeText(context,"开始执行图片加载",Toast.LENGTH_SHORT).show();  
        }  
        
        @Override  
        protected Bitmap doInBackground(Void... params) {
        	 Bitmap bitmap;
        	 try {  
                 url = new URL(imageurl);  
                 uRLConnection = (HttpURLConnection)url.openConnection();
                 InputStream is = uRLConnection.getInputStream();  
                 bitmap = BitmapFactory.decodeStream(is); 
                 is.close();    
                 uRLConnection.disconnect(); 
             } catch (MalformedURLException e) {  
                 e.printStackTrace();  
                 return null;  
             } catch (IOException e) {  
                 e.printStackTrace();  
                 return null;  
             }
            return bitmap;  
        }  
  
        @Override  
        protected void onPostExecute(Bitmap bitmap) {  
            imageView.setImageBitmap(bitmap);
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
//        	log.setText(""+values[0]);  
        }  
    } 
}



