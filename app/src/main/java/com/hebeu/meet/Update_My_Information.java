package com.hebeu.meet;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.hebeu.meet.domain.JSONResult;
import com.hebeu.meet.domain.User;
import com.hebeu.meet.fragment.MeFragment;

import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import static com.hebeu.meet.Login.saveLoginInfo;

/*修改个人信息
* Vanilla
* 5-21
* */
public class Update_My_Information extends AppCompatActivity {
    private TextView user_id =null;
    private TextView user_name =null;
    private TextView user_sex =null;
    private TextView user_college =null;
    private TextView user_classname =null;
    private TextView user_qq =null;
    private TextView user_phone =null;
    private TextView user_email =null;
    private Button btn_update_user = null;



    /*---------------------------------------------------------------------------------*/
    private Handler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.update_my_information);

        user_id =findViewById(R.id.user_id);
        user_name = findViewById(R.id.user_name);
        user_sex = findViewById(R.id.user_sex);
        user_college = findViewById(R.id.user_college);
        user_classname = findViewById(R.id.user_classname);
        user_qq = findViewById(R.id.user_qq);
        user_phone = findViewById(R.id.user_phone);
        user_email = findViewById(R.id.user_email);
        handler = new Handler();
        MyThread thread=new MyThread();
        thread.start();
        System.out.println("my_information");
        btn_update_user=findViewById(R.id.btn_update_user);
        btn_update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread_Update_User thread_update_user=new MyThread_Update_User();
                thread_update_user.start();
            }
            class MyThread_Update_User extends Thread{
                @Override
                public void run() {
                    User update_user = new User();
                    /*获取用户信息*/
                    SharedPreferences sharedPre = getSharedPreferences("config", MODE_PRIVATE);
                    String userId=sharedPre.getString("userId", "");
                    update_user.setUserId(userId);
                    update_user.setUserName(String.valueOf(user_name.getText()));
                    update_user.setSex(Integer.parseInt(String.valueOf(user_sex.getText())));
                    update_user.setCollege(String.valueOf(user_college.getText()));
                    update_user.setClassName(String.valueOf(user_classname.getText()));
                    update_user.setQq(String.valueOf(user_qq.getText()));
                    update_user.setPhone(String.valueOf(user_phone.getText()));
                    update_user.setEmail(String.valueOf(user_email.getText()));


                    Map<String,Object> paramMap = BeanUtil.beanToMap(update_user);
                    String res = HttpUtil.post("http://112.74.194.121:8889/user/updateUser",paramMap);
                    final JSONResult jsonResult = JSONUtil.toBean(res,JSONResult.class);

                    System.out.println(jsonResult);
                    Looper.prepare();

                    if (jsonResult.getCode() == 0){
                        //弹出对话框
                        Toast.makeText(Update_My_Information.this,"修改信息成功",Toast.LENGTH_SHORT).show();
                        /*重新保存用户信息到Sharedpreference*/
                        String res_id = HttpUtil.get("http://112.74.194.121:8889/user/getUserById?userId="+userId);
                        final User my_user = JSONUtil.toBean(res_id, User.class);
                        System.out.println("my_user"+my_user.toString());
                        saveLoginInfo(Update_My_Information.this, my_user);
                        /*跳转到个人中心*/
                        Intent intent = new Intent(Update_My_Information.this, My_Information.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(Update_My_Information.this,"修改信息失败",Toast.LENGTH_SHORT).show();
                    }


                    Looper.loop();

                }
            }

        });

    }
    class MyThread extends Thread {
        public void run() {

            SharedPreferences sharedPre = getSharedPreferences("config", MODE_PRIVATE);
            String userId=sharedPre.getString("userId", "");
            String res = HttpUtil.get("http://112.74.194.121:8889/user/getUserById?userId="+userId);
            final User user = JSONUtil.toBean(res, User.class);

            System.out.println(user.toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    user_id.setText(user.getUserId());
                    user_name.setText(user.getUserName());
                    user_sex.setText(user.getSex().toString());
                    user_college.setText(user.getCollege());
                    user_classname.setText(user.getClassName());
                    user_qq.setText(user.getQq());
                    user_phone.setText(user.getPhone());
                    user_email.setText(user.getEmail());
                }
            });
        }
    }

}
