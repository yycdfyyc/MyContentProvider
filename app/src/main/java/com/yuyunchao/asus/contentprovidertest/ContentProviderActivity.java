package com.yuyunchao.asus.contentprovidertest;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContentProviderActivity extends Activity {
    TextView tv_img;
    Button btn_search,btn_delete,btn_update;
    EditText et_id;
    //EditText中输入的内容
    String mSearchString;
    //查询的条件
    String mSelectionClause;
    //查询条件的参数
    String[] mSelectionArgs = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        initView();
    }
    private void initView(){
        tv_img = (TextView) findViewById(R.id.tv_img);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_update = (Button) findViewById(R.id.btn_update);
        et_id = (EditText) findViewById(R.id.et_id);
//        queryImg();
//        querySingleImg();
//        insertImg();
//        upDataImg("54");
//        deleteImg("48");
    }

    /**
     * ContentProvider查询图片信息
     */
    public void queryImg(View view){
        //构建查询的投影
        String[] mProjection =
                {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME
                };
        //输入的查询参数（为ID）
        mSearchString = et_id.getText().toString();
        //当查询条件为空时，查询所有
        if(TextUtils.isEmpty(mSearchString)){
            mSelectionClause = null;
            mSelectionArgs = null;
        }
        //查询相应ID的图片信息
        else{
            //构建查询条件
            mSelectionClause = MediaStore.Images.Media._ID + "=?";
            //构建查询条件的参数
            mSelectionArgs[0] = mSearchString;
        }

        Cursor mCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//URI
                mProjection,//相当于COLUMNS
                mSelectionClause,//WHERE条件
                mSelectionArgs,//相当于WHERE ARGS
                null
        );
        //当游标没有被正确实例化时
        if(mCursor == null){
            //打印错误日志
            Log.i("yyc","游标没有正确实例化");
        }
        //当游标中没有相应的数据时
        else if(mCursor.getCount()<1){
            Toast.makeText(this, "没有相应的图片信息，请重新输入！", Toast.LENGTH_SHORT).show();
        }else{
            //当游标不为空时,遍历游标
            if(mCursor != null && mCursor.getCount() > 0){
                //确定需要的数据的下标，减少IndexOrThrow的调用，提高效率
                int idIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int displayNameIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                //移动游标到第一行
                mCursor.moveToFirst();
                //数据的拼接字符串
                String temp = "";
                do{
                    String id = mCursor.getString(idIndex);
                    String displayName = mCursor.getString(displayNameIndex);
                    temp += "id = "+id +"\ndisplayName = "+ displayName+"\n";
                }while (mCursor.moveToNext());
                //关闭游标
                mCursor.close();
                //设置UI
                tv_img.setText(temp);
            }
        }


    }
    /**
     * ContentProvider查询单个图片信息
     */
    private void querySingleImg(){
        Uri singleUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,48);
        Cursor mCursor = getContentResolver().query(
                singleUri,//URI
                null,//相当于COLUMNS,这里null指的是返回整张表，使用"*"可能会报错
                null,//WHERE条件
                null,//相当于WHERE ARGS
                null
        );
        //当游标不为空时,遍历游标
        if(mCursor != null && mCursor.getCount() > 0){
            //确定需要的数据的下标，减少IndexOrThrow的调用，提高效率
            int idIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int widthIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
            //移动游标到第一行
            mCursor.moveToFirst();
            //数据的拼接字符串
            String temp = "";
            do{
                String id = mCursor.getString(idIndex);
                String width = mCursor.getString(widthIndex);
                temp += "id = "+id +"\nwidth = "+ width+"\n";
            }while (mCursor.moveToNext());
            //关闭游标
            mCursor.close();
            //设置UI
            tv_img.setText(temp);
        }
    }
    /**
     * ContentProvider插入图片
     */
    private void insertImg(){
        //要插入的图片的数据
        ContentValues mCV = new ContentValues();
        mCV.put(MediaStore.Images.Media.HEIGHT, 500);
        mCV.put(MediaStore.Images.Media.WIDTH, 500);

        Uri mUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mCV
                );
    }
    /**
     * ContentProvider更改图片
     */
    private void upDataImg(String id){
        ContentValues mCV = new ContentValues();
        mCV.put(MediaStore.Images.Media.WIDTH, 200);
        getContentResolver().update(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mCV,
                MediaStore.Images.Media._ID + "="+id,
                null
        );
    }
    /**
     * ContentProvider删除图片n
     */
    private void deleteImg(String id){
        getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media._ID + "=" + id,
                null
        );
    }
}
