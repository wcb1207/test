package com.fumei.pdfread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.LinkInfoExternal;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.MuPDFThumb;
import com.artifex.mupdfdemo.ReaderView;
import com.artifex.mupdfdemo.domain.OutlineActivityData;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.fumei.pdfread.adapter.SpaceItemDecoration;
import com.fumei.pdfread.utils.DensityUtil;
import com.fumei.pdfread.utils.FileUtils;
import com.fumei.pdfread.utils.MyKeyChain;
import com.fumei.pdfread.utils.PDFParser;
import com.fumei.pdfread.utils.SaveBitmap;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WCB on 2016/7/11.
 */


public class MyActivity extends Activity implements FilePicker.FilePickerSupport{
    private List<String> pdfList = new ArrayList<>();
    private List<Bitmap> bmpList = new ArrayList<>();
    private List<String> pdfBoffer = new ArrayList<>();
    private String fileName;
    private SparseArray<LinkInfoExternal[]> linkOfDocument;
    private RecyclerView mPreview;
    private boolean isshow = true;
    private int current = 0;
    private PdfThumbAdapter adapter;
    private List<ReaderView> readerViews;
    private SparseArray<ReaderView> readerViewSparseArray;
    private Crypto crypto;
    private RelativeLayout head_inc, imgView;
    private int indexNumber;
    private TextView backTv, numberTv, rightTv;


    static private AlertDialog.Builder gAlertBuilder;

    static public AlertDialog.Builder getAlertBuilder() {
        return gAlertBuilder;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_view);
//        //隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window=MyActivity.this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);
        WindowManager wm = this.getWindowManager();
        int w = wm.getDefaultDisplay().getWidth();
        int h =  wm.getDefaultDisplay().getHeight();
        load();
        initdata();
        addView();
        getMyplvaData();
        getBitmap(w,h);

        test();
         MuPDFPageAdapter mDocViewAdapter = new MuPDFPageAdapter(MyActivity.this, this,openBuffer());
        final MuPDFReaderView docView = new MuPDFReaderView(this){

            @Override
            protected void onMoveToChild(int i) {
                mPreview.scrollToPosition(i);
                mPreview.setVisibility(View.INVISIBLE);
                head_inc.setVisibility(View.INVISIBLE);
                isshow = false;
                numberTv.setText((i+1) + "/" + pdfList.size());
                backTv.setText((i+1) + ".png");
                super.onMoveToChild(i);
            }

            @Override
            protected void onTapMainDocArea() {
                if (isshow) {
                    isshow = false;
                    hideRecyclerView();
                } else {
                    isshow = true;
                    showRecyclerView();
                }
            }

            @Override
            protected void onDocMotion() {
               // hideRecyclerView();
            }



        };
        docView.setAdapter(mDocViewAdapter);

        imgView.addView(docView);
        mPreview.setBackgroundColor(getResources().getColor(R.color.bar));
        mPreview.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        //去掉中间的间隙
        mPreview.addItemDecoration(new SpaceItemDecoration(10));
        mPreview.setAdapter(adapter = new PdfThumbAdapter(MyActivity.this, bmpList));
        adapter.setmOnItemClickLitener(new PdfThumbAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (current != position) {
                    current = position;
                    indexNumber = position;
                    docView.setDisplayedViewIndex(indexNumber);
                    isshow = false;
                    mPreview.smoothScrollToPosition(position);
                    mPreview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        //mPreview.setVisibility(View.VISIBLE);


        //test();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 动态添加so
     */
    private void load() {
        int num = FileUtils.assetToFile(this, "/libmupdf/libmupdf.so");
    }

    /**
     * 生成缩略图
     */
    public void getBitmap(int w,int h) {
        for (int i = 1; i <= pdfList.size();i++) {
            MuPDFThumb pdfthum = null;
            try {
                pdfthum = new MuPDFThumb(this,pdfList.get(i-1));
                OutlineActivityData.set(null);
                int height = h/10*2;
                Bitmap bm = pdfthum.thumbOfFirstPage(1,height);

                File dest = new File(Environment.getExternalStorageDirectory().getPath() + "/aaaa/");
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                File file = new File(dest, i + ".png");
                if (!file.exists()) {
                    file.createNewFile();
                }

                SaveBitmap.saveMyBitmap(file,bm);


                bmpList.add(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void getMyplvaData() {
        Intent intent = getIntent();
        indexNumber = intent.getIntExtra("abc", 0);

    }

    public void addView() {
        head_inc = (RelativeLayout) findViewById(R.id.head_inc);
        imgView = (RelativeLayout) findViewById(R.id.imgView);
        mPreview = (RecyclerView) findViewById(R.id.rv);

        backTv = (TextView) findViewById(R.id.back);
        numberTv = (TextView) findViewById(R.id.number);
        rightTv = (TextView) findViewById(R.id.right);

        //mPreview.getBackground().setAlpha(125);//0~255透明度值

        //rl.setVisibility(View.INVISIBLE);

        numberTv.setText(1 + "/" + pdfList.size());
        backTv.setText(1 + ".png");
        //wcb
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyActivity.this, "呵呵", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MyActivity.this, ParticularsListViewActivity.class);
                i.putExtra("name", pdfList.size());
                startActivity(i);
            }
        });
    }

    private void initdata() {
        readerViews = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getPath() + "/myPdf/";

        for (int i = 1; i < 15; i++) {
            pdfList.add(path + i + ".pdf");
        }
        readerViewSparseArray = new SparseArray<>(pdfList.size());
    }

    /**
     * 显示布局
     */
    private void showRecyclerView() {
        Animation anim = new TranslateAnimation(0, 0, mPreview.getHeight(), 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mPreview.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

            }
        });
        mPreview.startAnimation(anim);

        anim = new TranslateAnimation(0, 0, -head_inc.getHeight(), 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                head_inc.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

            }
        });
        head_inc.startAnimation(anim);
    }

    /**
     * 影藏布局
     */
    private void hideRecyclerView() {
        Animation anim = new TranslateAnimation(0, 0, 0, this.mPreview.getHeight());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mPreview.setVisibility(View.INVISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

            }
        });
        mPreview.startAnimation(anim);

        anim = new TranslateAnimation(0, 0, 0, -this.head_inc.getHeight());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                head_inc.setVisibility(View.INVISIBLE);
            }
        });
        head_inc.startAnimation(anim);
    }


    private void test() {
        crypto = new Crypto(new MyKeyChain(this, "RXB1YmNoaW5hRmVpbWVpSHo="),
                new SystemNativeCryptoLibrary());
        try {
            for (int i = 1; i <= pdfList.size(); i++) {
                byte buffer[] = null;
                FileInputStream fin = new FileInputStream(pdfList.get(i-1));
                int length = fin.available();
                buffer = new byte[length];
                fin.read(buffer);
                fin.close();

                File dest = new File(Environment.getExternalStorageDirectory().getPath() + "/bbbb/");
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                File file = new File(dest, i + ".pdf");
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(file));
                OutputStream ouStream = crypto.getCipherOutputStream(fileStream, new Entity( i + ".pdf"));
                ouStream.write(buffer);
                pdfBoffer.add(file.getPath());
                ouStream.flush();
                ouStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }
    }

    /**
     * path转换，MuPDFCore
     *
     * @return
     */
    private List<MuPDFCore> openFile() {
        List<MuPDFCore> list = new ArrayList<>();
        for (String path : pdfList) {
            int lastSlashPos = path.lastIndexOf('/');
            fileName = new String(lastSlashPos == -1
                    ? path
                    : path.substring(lastSlashPos + 1));
            PDFParser linkGetter = new PDFParser(this, path);
            linkOfDocument = linkGetter.getLinkInfo();

            MuPDFCore core;
            try {
                core = new MuPDFCore(this,path);
                // New file: drop the old outline data
                OutlineActivityData.set(null);
            } catch (Exception e) {
                return null;
            }
            list.add(core);
        }
        return list;
    }

    /**
     * Buffer转换，MuPDFCore
     *
     * @return
     */
    private List<MuPDFCore> openBuffer() {
        crypto = new Crypto(new MyKeyChain(this, "RXB1YmNoaW5hRmVpbWVpSHo="),
                new SystemNativeCryptoLibrary());
        List<MuPDFCore> list = new ArrayList<>();
        for (String path :pdfBoffer ) {
            MuPDFCore core;
            byte buffer[] = null;
            try {
                File dest1 = new File(path);
                int lastSlashPos = path.lastIndexOf('/');
                String fileName = new String(lastSlashPos == -1
                        ? path
                        : path.substring(lastSlashPos + 1));
                FileInputStream fileStream1 = new FileInputStream(dest1);
                //解密
                InputStream inputStream = crypto.getCipherInputStream(fileStream1, new Entity(fileName));
                int length = fileStream1.available();
                buffer = new byte[length];
                //inputStream
                inputStream.read(buffer);
                inputStream.close();
                /*  FileInputStream fin = new FileInputStream(path);
                  int length = fin.available();
                  buffer = new byte[length];
                   fin.read(buffer);
                   fin.close();*/
                core = new MuPDFCore(this, buffer, null);
                // New file: drop the old outline data
                OutlineActivityData.set(null);
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
            list.add(core);
        }
        return list;

//        try {
//            for (int i = 1; i <= PdfBoffer.size(); i++) {
//                byte buffer[] = null;
//                File dest1 = new File(PdfBoffer.get(0));
//                int lastSlashPos = PdfBoffer.get(0).lastIndexOf('/');
//                String fileName = new String(lastSlashPos == -1
//                        ? PdfBoffer.get(0)
//                        : PdfBoffer.get(0).substring(lastSlashPos + 1));
//                FileInputStream fin = new FileInputStream(dest1);
//                int length = fin.available();
//                buffer = new byte[length];
//                fin.read(buffer);
//
//                fin.close();
//                File dest = new File(Environment.getExternalStorageDirectory().getPath() + "/aaaa/");
//                if (!dest.exists()) {
//                    dest.mkdirs();
//                }
//                File file = new File(dest, "1.pdf");
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                Log.i("fin",fin.toString());
//                InputStream inputStream = crypto.getCipherInputStream(fin, new Entity(fileName));
//                int length1 = inputStream.available();
//                buffer = new byte[length1];
//                inputStream.read(buffer);
//
//                int read;
//                OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(file));
//                while ((read = inputStream.read(buffer)) != -1) {
//                    fileStream.write(buffer, 0, read);
//                }
//                inputStream.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CryptoInitializationException e) {
//            e.printStackTrace();
//        } catch (KeyChainException e) {
//            e.printStackTrace();
//        }
//        return null;




    }

    /**
     * 获取手机SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    private SparseArray<LinkInfoExternal[]> getLinkOfDocument(String path) {
        SparseArray<LinkInfoExternal[]> linkOfDocument;
        PDFParser linkGetter = new PDFParser(this, path);
        linkOfDocument = linkGetter.getLinkInfo();
        return linkOfDocument;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PdfRead Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.fumei.pdfread/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PdfRead Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.fumei.pdfread/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void performPickFor(FilePicker picker) {

    }
}
