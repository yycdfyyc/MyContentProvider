package com.yuyunchao.asus.contentprovidertest;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContentProviderActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
    ListView lv_img;
    TextView tv_img;
    Button btn_search,btn_delete,btn_update,btn_insert,btn_loader;
    EditText et_id;
    //EditText中输入的内容
    String mSearchString;
    //查询的条件
    String mSelectionClause;

    SimpleCursorAdapter mSimpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        initView();
    }
    private void initView(){
        lv_img = (ListView) findViewById(R.id.lv_img);
        tv_img = (TextView) findViewById(R.id.tv_img);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_insert = (Button) findViewById(R.id.btn_insert);
        btn_loader = (Button) findViewById(R.id.btn_loader);
        et_id = (EditText) findViewById(R.id.et_id);
//        queryImg();
//        querySingleImg();
//        insertImg();
//        upDataImg("54");
//        deleteImg("48");
//        batchOperateContacts();
        getLoaderManager().initLoader(0,null,this);
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
        //查询条件的参数
        String[] mSelectionArgs = {""};
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
                mProjection,        //相当于COLUMNS
                mSelectionClause,   //WHERE条件
                mSelectionArgs,     //相当于WHERE ARGS
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
        }
        else{
//            //当游标不为空时,遍历游标
//            if(mCursor != null && mCursor.getCount() > 0){
//                //确定需要的数据的下标，减少IndexOrThrow的调用，提高效率
//                int idIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//                int displayNameIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                //移动游标到第一行
//                mCursor.moveToFirst();
//                //数据的拼接字符串
//                String temp = "";
//                do{
//                    String id = mCursor.getString(idIndex);
//                    String displayName = mCursor.getString(displayNameIndex);
//                    temp += "id = "+id +"\ndisplayName = "+ displayName+"\n";
//                }while (mCursor.moveToNext());
                //构建简易适配器展示数据的控件列表
                int[] mImageListItems = new int[]{
                        R.id.tv_item_id,
                        R.id.tv_item_display
                };
                //初始化SimpleCursorAdapter
                mSimpleCursorAdapter = new SimpleCursorAdapter(
                        this,                   //上下文
                        R.layout.item_cursor,   //子item的布局
                        mCursor,                //存放数据的游标
                        mProjection,
                        mImageListItems,
                        0
                );
                //设置UI
                lv_img.setAdapter(mSimpleCursorAdapter);
            }
//        }


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
    public void insertImg(View view){
        //要插入的图片的数据
        ContentValues mCV = new ContentValues();
        mCV.put(MediaStore.Images.Media.DISPLAY_NAME, "new");
        mCV.put(MediaStore.Images.Media.HEIGHT, 500);
        mCV.put(MediaStore.Images.Media.WIDTH, 500);

        Uri mUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mCV
                );
        tv_img.setText(ContentUris.parseId(mUri)+"");

    }
    /**
     * ContentProvider更改图片
     */
    public void upDataImg(View view){
        ContentValues mCV = new ContentValues();
        mCV.put(MediaStore.Images.Media.DISPLAY_NAME, "已更改");
        mSelectionClause = MediaStore.Images.Media.DISPLAY_NAME + " like ?";
        String[] mSelectionArgs = {"new"};
        int mRowUpdate = getContentResolver().update(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mCV,
                mSelectionClause,
                mSelectionArgs
        );
        tv_img.setText("更改了" + mRowUpdate + "张图片");
    }
    /**
     * ContentProvider删除图片
     */
    public void deleteImg(View view){
        mSelectionClause = MediaStore.Images.Media.DISPLAY_NAME + " like ?";
        String[] mSelectionArgs={"已更改"};
        int mRowDelete= getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mSelectionClause,
                mSelectionArgs
        );
        tv_img.setText("删除了" + mRowDelete + "张图片");
    }

    /**
     * 批量操作通讯录数据
     */
    private void  batchOperateContacts(){
        //定义一个存储批量操作的集合
        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
        //添加一个插入新通讯录数据的操作
        cpo.add(
                ContentProviderOperation
                        .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .build()
        );
        //构建选择语句
        String mSelectionClause = ContactsContract.RawContacts._ID + "=1";
        //添加一个删除某条通讯录数据的操作
        cpo.add(
                ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(mSelectionClause, null)
                .build()
        );
        //添加一个通讯录断点查询的操作
        cpo.add(
                ContentProviderOperation
                .newAssertQuery(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(mSelectionClause, null)
                .withExpectedCount(0)
                .build()
        );
        //通过之前下下标为0的的ContentProviderOperation返回的
        // Uri中的ContactsContract.Data.RAW_CONTACT_ID作为选择条件插入数据
        //data表与raw_contacts是关联的关系
        cpo.add(
                ContentProviderOperation
                        //要插入数据的uri
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        //根据批处理的第一次操作所返回的ContactsContract.Data.CONTACT_ID来进行插入数据操作
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        //DATA1代表了电话号码,给该联系人插入一条电话数据
                        .withValue(ContactsContract.Data.DATA1, "18757338996")
                        //在给Data表插入数据时必须制定该条数据的MIME类型
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .build()
        );

        try {
            //保存批处理的操作结果
            ContentProviderResult[] results =
                    //执行批处理操作
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);
            //保存操作结果的临时变量
            String temp = "";
            //遍历结果
            for (int i = 0; i< results.length; i ++){
                temp += results[i].toString() + "\n\n";
            }
            //更新UI
            tv_img.setText(temp);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void searchContacts(View view){
        //当改变查询条件时
        getLoaderManager().restartLoader(0, null, this);
    }
    //创建一个加载器 返回一个Loader （CursorLoader）
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //要查询的uri
        Uri mUri = ContactsContract.Contacts.CONTENT_URI;
        //要查询的列
        String[] mProjection = new String[]{
                ContactsContract.Contacts._ID,//id
                ContactsContract.Contacts.DISPLAY_NAME//显示的名称
        };
        //用户的选择条件
        String mFilter = et_id.getText().toString();
        //判断用户的选择条件
        if(TextUtils.isEmpty(mFilter)){
            mFilter = "0";
        }
        //判断输入的信息
        if(mFilter.matches("\\d*")){
        }else {
            Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
            mFilter = "0";
        }

        //选择条件,已有ID大于用户输入的ID
        String mSelection = ContactsContract.Contacts._ID + " > " + mFilter;
        return new CursorLoader(
                this,//上下文
                mUri,//要查询的uri
                mProjection,//要查询的列
                mSelection,//查询条件
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"//根据本地语言进行排序
        );
    }
    //创建结束后用查询来的data设置UI
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //要查询的数据index
        int i_id = data.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
        int i_display = data.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
        //临时存储查询数据的字符串
        String temp = "";
        //如若数据条目正确,开始遍历
        while (data.moveToNext()){
            String id = data.getString(i_id);
            String displayName = data.getString(i_display);
            //更新临时字符串
            temp += "id = " + id + "\n" +
                    "displayName = " + displayName + "\n\n";
        }
        //设置UI
        tv_img.setText(temp);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        //释放数据
        tv_img.setText("");
    }
}







