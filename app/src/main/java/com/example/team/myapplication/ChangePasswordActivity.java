package com.example.team.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyException;

import java.util.HashMap;


public class ChangePasswordActivity extends GeneralActivity {
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private DB db = new DB(this);
    public EditText text1;
    public EditText text2;
    public EditText text3;
    public EditText text4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        scrollView = (ScrollView) findViewById(R.id.scrollView2);
        text1 = (EditText) findViewById(R.id.editText2);
        text2 = (EditText) findViewById(R.id.editText3);
        text3 = (EditText) findViewById(R.id.editText4);
        text4 = (EditText) findViewById(R.id.editText5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEmailValid(String email) {
        return email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() <= 15;
    }

    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    public void changePassword(View view) {
        String email;
        String oldPassword;
        String newPassword;
        String newPassword2;
        oldPassword = text1.getText().toString();
        newPassword = text2.getText().toString();
        newPassword2 = text3.getText().toString();
        email = text4.getText().toString();
        if (TextUtils.isEmpty(email)) {
            text4.setError(getString(R.string.error_field_required));
            return;
        } else if (!isEmailValid(email)) {
            text4.setError(getString(R.string.error_field_required));
            return;
        }
        if (oldPassword.isEmpty()) {
            text1.setError(getString(R.string.error_field_required));
            return;
        }
        if (newPassword.isEmpty()) {
            text2.setError(getString(R.string.error_field_required));
            return;
        }
        if (newPassword2.isEmpty()) {
            text3.setError(getString(R.string.error_field_required));
            return;
        }
        if (oldPassword.equals(newPassword)) {
            text2.setError("新旧密码相同");
            return;
        }
        if (!newPassword.equals(newPassword2)) {
            text1.setError(null);
            text2.setError("两次输入的密码不一致");
            text3.setError("两次输入的密码不一致");
            return;
        }
        if (isPasswordValid(newPassword)) {
            showProgress(true);
            ChangePasswordProgress changePasswordProgress = new ChangePasswordProgress(oldPassword, newPassword, email);
            changePasswordProgress.execute((Void) null);
        } else {
            text2.setError(getString(R.string.error_invalid_password));
        }

    }

    class ChangePasswordProgress extends AsyncTask<Void, Void, Integer> {
        private String username = LoginState.username;
        private String oldPassword;
        private String newPassword;
        private String email;

        public ChangePasswordProgress(String oldPassword, String newPassword, String email) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.email = email;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Integer rettype = 0;
            try {
                String url = "http://192.168.253.1/password_change/" + LoginState.username + "/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("email", email);
                map.put("old_password", oldPassword);
                map.put("new_password", newPassword);
                JsonPost post = new JsonPost(map, url, "pwdchange", db);
                Thread.sleep(1000);
            } catch (MyException.emailNotMatchException e) {
                rettype = 1;
            } catch (MyException.oldPasswordNotMatchException e) {
                rettype = 2;
            } catch (MyException.passwordInvalidException e) {
                rettype = 3;
            } catch (Exception e) {
                rettype = 4;
            }
            return rettype;
        }

        @Override
        protected void onPostExecute(final Integer rettype) {
            showProgress(false);
            if (0 == rettype) {
                Toast.makeText(getApplicationContext(), "修改成功!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (1 == rettype) {
                text4.setError("邮箱不匹配");
            } else if (2 == rettype) {
                text1.setError("旧密码不匹配");
            } else if (3 == rettype) {
                text2.setError("新密码格式错误");//TODO:%能当做密码 BUG
            } else {
                Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
