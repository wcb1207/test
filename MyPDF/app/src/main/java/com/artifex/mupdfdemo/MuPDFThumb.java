package com.artifex.mupdfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

public class MuPDFThumb extends MuPDFCore {

    public MuPDFThumb(Context context, String filename) throws Exception {
        super(context,filename);
    }

    public Bitmap thumbOfFirstPage(int w, int h) {
        //获取PDF的宽和高
        PointF pageSize = getPageSize(-3);

        Log.i("pageSize", ""+pageSize.x+"|"+pageSize.y);
        float mSourceScale = Math.max(w/pageSize.x, h/pageSize.y);
        Point size = new Point((int)(pageSize.x*mSourceScale), (int)(pageSize.y*mSourceScale));
        final Bitmap bp = Bitmap.createBitmap(size.x,size.y, Bitmap.Config.ARGB_8888);
        Log.i("pageSize", " size:"+size.x+"|"+size.y);//229 300
        drawPage(bp,-3,size.x, size.y, 0, 0, size.x, size.y,new Cookie());
        //	drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
        return bp;

    }

}
