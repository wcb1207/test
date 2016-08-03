package com.artifex.mupdfdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MuPDFPageAdapter extends BaseAdapter {
	private final Context mContext;
	private final FilePicker.FilePickerSupport mFilePickerSupport;
	private final List<MuPDFCore> mCore;
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
	private       Bitmap mSharedHqBm;

	public MuPDFPageAdapter(Context c, FilePicker.FilePickerSupport filePickerSupport, List<MuPDFCore> core) {
		Log.i("MuPDFCore core", core.toString());
		mContext = c;
		mFilePickerSupport = filePickerSupport;
		mCore = core;
	}

	public int getCount() {
		return mCore.size();
	}

	public Object getItem(int position) {
		return mCore.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void releaseBitmaps()
	{
		//  recycle and release the shared bitmap.
		if (mSharedHqBm!=null)
			mSharedHqBm.recycle();
		mSharedHqBm = null;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MuPDFPageView pageView;
		
		Log.i("pageSize", " MuPDFPageAdapter");//229 300
		
		//if (convertView == null) {
			if (mSharedHqBm == null || mSharedHqBm.getWidth() != parent.getWidth() || mSharedHqBm.getHeight() != parent.getHeight())
				mSharedHqBm = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);
			Log.i("pageSize", " MuPDFPageAdapter ()");
			Log.i("mFilePickerSupport", mFilePickerSupport.toString()+"\t"+mCore.get(position).toString()+"\t"+mSharedHqBm.toString());
			pageView = new MuPDFPageView(mContext, mFilePickerSupport, mCore.get(position), new Point(parent.getWidth(), parent.getHeight()), mSharedHqBm);
		//} else {
			//pageView = (MuPDFPageView) convertView;
		//}

		PointF pageSize = mPageSizes.get(position);
		if (pageSize != null) {
			// We already know the page size. Set it up
			// immediately
			
			Log.i("pageSize", " MuPDFPageView setPage");
			pageView.setPage(position, pageSize);
		} else {
			// Page size as yet unknown. Blank it for now, and
			// start a background task to find the size
			pageView.blank(position);
			AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.get(position).getPageSize(-3);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					// We now know the page size
					mPageSizes.put(position, result);
					// Check that this view hasn't been reused for
					// another page since we started
					if (pageView.getPage() == position)
						pageView.setPage(position, result);
				}
			};

			sizingTask.execute((Void)null);
		}
		return pageView;
	}
	

}
