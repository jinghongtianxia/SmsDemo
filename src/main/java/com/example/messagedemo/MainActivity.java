package com.example.messagedemo;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    private String defaultSmsPkg;
    private String mySmsPkg;
    private TextView mMessageView=null;
    private EditText mPhoneNumber=null;
    private EditText mMsg=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNumber=(EditText)findViewById(R.id.editText);
        mMsg=(EditText)findViewById(R.id.editText2);
        mMessageView=(TextView)findViewById(R.id.textView3);
        mMessageView.setMovementMethod(ScrollingMovementMethod.getInstance()); //设置滚动
        defaultSmsPkg= Telephony.Sms.getDefaultSmsPackage(this);
        mySmsPkg= this.getPackageName();

        if(!defaultSmsPkg.equals(mySmsPkg)){
//            如果这个App不是默认的Sms App，则修改成默认的SMS APP
//            因为从Android 4.4开始，只有默认的SMS APP才能对SMS数据库进行处理
            Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,mySmsPkg);
            startActivity(intent);
        }

        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(mySmsPkg.equals(Telephony.Sms.getDefaultSmsPackage(MainActivity.this))){
                    if(mPhoneNumber.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"Phone number cannot be empty！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(mMsg.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"Message cannot be empty！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    System.out.println("My App is default SMS App.");
                    //        对短信数据库进行处理
                    ContentResolver resolver=getContentResolver();

                    ContentValues values=new ContentValues();
                    values.put(Telephony.Sms.ADDRESS,mPhoneNumber.getText().toString());
                    values.put(Telephony.Sms.DATE, System.currentTimeMillis());
                    long dateSent=System.currentTimeMillis()-5000;
                    values.put(Telephony.Sms.DATE_SENT,dateSent);
                    values.put(Telephony.Sms.READ,false);
                    values.put(Telephony.Sms.SEEN,false);
                    values.put(Telephony.Sms.STATUS, Telephony.Sms.STATUS_COMPLETE);
                    values.put(Telephony.Sms.BODY, mMsg.getText().toString());
                    values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);

                    Uri uri=resolver.insert(Telephony.Sms.CONTENT_URI,values);
                    if(uri!=null){
                        long uriId= ContentUris.parseId(uri);
                        System.out.println("uriId "+uriId);
                    }

                    Toast.makeText(MainActivity.this, "Insert a short Message.",
                            Toast.LENGTH_LONG).show();

//            对短信数据库处理结束后，恢复原来的默认SMS APP
                    Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,defaultSmsPkg);
                    startActivity(intent);
                    System.out.println("Recover default SMS App");

//                    打印出收件箱里的最新5条短信
                    Cursor cursor=getContentResolver().query(Telephony.Sms.CONTENT_URI,null,null,null,null);
                    String msg="";
                    while ((cursor.moveToNext()) &&
                            (cursor.getPosition()<5)){
                        int dateColumn=cursor.getColumnIndex("date");
                        int phoneColumn=cursor.getColumnIndex("address");
                        int smsColumn=cursor.getColumnIndex("body");

                        System.out.println("count "+cursor.getCount()+" position "+cursor.getPosition());
//                        把从短信中获取的时间戳换成一定格式的时间
                        SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date date=new Date(Long.parseLong(cursor.getString(dateColumn)));
                        String time=sfd.format(date);
                        msg=msg+time+" "+cursor.getString(phoneColumn)+":"+cursor.getString(smsColumn)+"\n";
                        mMessageView.setText(msg);
                    }

                }
                else{
                    Toast.makeText(MainActivity.this,"Sorry,the App is not default Sms App.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
