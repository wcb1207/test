package com.fumei.pdfread;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.artifex.mupdfdemo.MuPDFThumb;
import com.artifex.mupdfdemo.domain.OutlineActivityData;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PdfThumbAdapter extends RecyclerView.Adapter<PdfThumbAdapter.PDFPreviewViewHolder> {
    private Context mContext;
    private List<Bitmap> list;

    public static class PDFPreviewViewHolder extends RecyclerView.ViewHolder {

        ImageView previewPageImageView = null;
        ProgressBar progress_bar;

        public PDFPreviewViewHolder(View view) {
            super(view);
            this.previewPageImageView = (ImageView) view
                    .findViewById(R.id.PreviewPageImageView);
            //this.progress_bar = (ProgressBar)view.findViewById(R.id.progress_bar);
        }
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener litener) {
        this.mOnItemClickLitener = litener;
    }

    public PdfThumbAdapter(Context context, List<Bitmap> imgList) {
        mContext = context;
        list = imgList;
    }

    @Override
    public PdfThumbAdapter.PDFPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.preview_pager_item_layout, parent, false);
        return new PDFPreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PdfThumbAdapter.PDFPreviewViewHolder holder, final int position) {
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
        holder.previewPageImageView.setImageBitmap(list.get(position));
        //holder.progress_bar.setVisibility(View.VISIBLE);
        //holder.previewPageImageView.setVisibility(View.INVISIBLE);
//        Observable.create(new Observable.OnSubscribe<Bitmap>() {
//            @Override
//            public void call(Subscriber<? super Bitmap> subscriber) {
//                try {
//                    MuPDFThumb pdfthum = new MuPDFThumb(list.get(position));
//                    OutlineActivityData.set(null);
//                    Bitmap bm = pdfthum.thumbOfFirstPage(1000, 1000);
//                    subscriber.onNext(bm);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Bitmap>() {
//                    @Override
//                    public void onCompleted() {
//                        //   holder.progress_bar.setVisibility(View.INVISIBLE);
//                        holder.previewPageImageView.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Bitmap bitmap) {
//                        Bitmap bm = bitmap;
//                        if (!bm.equals( holder.previewPageImageView.getTag())) {
//                            holder.previewPageImageView.setImageBitmap(bitmap);
//                            holder.previewPageImageView.setTag(bitmap);
//                            ImageLoader imageLoader = ImageLoader.getInstance();
//                        }
//
//
//                    }
//                });
//        try {


         /*   File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/", "Test.png");
            if (f.exists()) {
                f.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
