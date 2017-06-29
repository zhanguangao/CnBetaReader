package org.zreo.cnbetareader.Activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.zreo.cnbetareader.Adapters.CommentAdapter;
import org.zreo.cnbetareader.Entitys.CheckCode;
import org.zreo.cnbetareader.Entitys.CnComment;
import org.zreo.cnbetareader.R;
import org.zreo.cnbetareader.Views.XListView;

import java.util.ArrayList;
import java.util.List;

public class PostCommentActivity extends AppCompatActivity {
    EditText commentTest;
    ImageView codeImage;
    Button refresh;
    String getCode=null;
    EditText editCode;
    public Button send;
    private Toolbar mToolbar;
    SharedPreferences pref;

    public PostCommentActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_comment);
        commentTest = (EditText)findViewById(R.id.commentTest);
        codeImage =(ImageView)findViewById(R.id.codeImage);
        codeImage.setImageBitmap(CheckCode.getInstance().getBitmap());
        editCode =(EditText) findViewById(R.id.mEditPass);
        getCode=CheckCode.getInstance().getCode(); //获取显示的验证码
        refresh =(Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                codeImage.setImageBitmap(CheckCode.getInstance().getBitmap());
                getCode = CheckCode.getInstance().getCode();
            }
        });
        send = (Button)findViewById(R.id.send_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String v_code = editCode.getText().toString().trim();
                if (v_code == null || v_code.equals("")) {
                    Toast.makeText(PostCommentActivity.this, "没有填写验证码", Toast.LENGTH_SHORT).show();
                } else if (!v_code.equals(getCode)) {
                    Toast.makeText(PostCommentActivity.this, "验证码填写不正确", Toast.LENGTH_SHORT).show();
                } else {
                    String content = commentTest.getText().toString();
                    if(!"".equals(content)){
                        Intent intent = new Intent();
                        intent.putExtra("content", content);
                        setResult(RESULT_OK, intent);
                        finish();
                        Toast.makeText(PostCommentActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PostCommentActivity.this, "请输入评论内容",Toast.LENGTH_SHORT).show();
                    }
                }
           }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);   //ToolBar布局
        mToolbar.setTitle("发表评论");   // 标题的文字需在setSupportActionBar之前，不然会无效
        mToolbar.setTitleTextColor(Color.WHITE);  //设置ToolBar字体颜色为白色
        setSupportActionBar(mToolbar);  //将ToolBar设置为ActionBAr
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //在ToolBar左边，即当前标题前添加图标
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //读取设置文件的值
        pref = getSharedPreferences("org.zreo.cnbetareader_preferences", Context.MODE_PRIVATE);
        setThemeColor(pref.getInt("theme", 0));    //设置文件里主题的值
    }

    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(color); //状态栏颜色
        }
    }
    /**更改主题颜色*/
    @SuppressLint("NewApi")
    public void setThemeColor(int index){
        switch (index){
            case 0:  //蓝色（默认）
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));  //ActionBar颜色
                setStatusColor(getResources().getColor(R.color.mainColor));
                break;
            case 1:  //棕色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.brown));
                setStatusColor(getResources().getColor(R.color.brown));
                break;
            case 2:  //橙色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.orange));
                setStatusColor(getResources().getColor(R.color.orange));
                break;
            case 3:  //紫色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                setStatusColor(getResources().getColor(R.color.purple));
                break;
            case 4:  //绿色
                mToolbar.setBackgroundColor(getResources().getColor(R.color.green));
                setStatusColor(getResources().getColor(R.color.green));
                break;
            default:  //默认
                mToolbar.setBackgroundColor(getResources().getColor(R.color.mainColor));
                setStatusColor(getResources().getColor(R.color.mainColor));
                break;
        }

    }
}
