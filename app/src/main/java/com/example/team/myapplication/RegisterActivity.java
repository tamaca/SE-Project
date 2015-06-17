package com.example.team.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team.myapplication.Database.DB;
import com.example.team.myapplication.Network.JsonPost;
import com.example.team.myapplication.util.CheckValid;
import com.example.team.myapplication.util.GeneralActivity;
import com.example.team.myapplication.util.MyException;
import com.example.team.myapplication.util.MyToast;

import java.util.HashMap;


public class RegisterActivity extends GeneralActivity {
    private UserRegisterTask mAuthTask = null;
    private Button okButton;
    private TextView userName;
    private TextView eMail;
    private EditText firstPassword;
    private EditText secondPassword;
    private View progressView;
    private View registerView;
    private MyToast myToast;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        setContentView(R.layout.activity_register);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 初始化变量
         */
        db = new DB(this);
        okButton = (Button) findViewById(R.id.register_ok);
        userName = (TextView) findViewById(R.id.register_user_name);
        eMail = (TextView) findViewById(R.id.register_email);
        firstPassword = (EditText) findViewById(R.id.register_password);
        secondPassword = (EditText) findViewById(R.id.register_password_again);
        progressView = findViewById(R.id.login_progress);
        registerView = findViewById(R.id.register_view);
        myToast = new MyToast(this);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isUserNameValid(String userName) {
        //还要检查用户名是否唯一
        return userName.length() >= 2 && userName.length() <= 20;
    }

    public boolean isPasswordValid(String password) {
        //检查密码复杂度？？
        return password.length() > 5 && password.length() <= 15;
    }

    private boolean isEmailValid(String email) {
        return email.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
    }

    public boolean isPasswordSame(String password1, String password2) {

        return password1.equals(password2);
    }

    public void attemptRegister() {

        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;
        userName.setError(null);
        eMail.setError(null);
        firstPassword.setError(null);
        secondPassword.setError(null);

        String user_name = userName.getText().toString();
        String email = eMail.getText().toString();
        String password_1 = firstPassword.getText().toString();
        String password_2 = secondPassword.getText().toString();

        if (TextUtils.isEmpty(user_name)) {
            userName.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = userName;
        } else if (!CheckValid.isUserNameValid(user_name)) {
            userName.setError(getString(R.string.user_name_invalid));
            cancel = true;
            focusView = userName;
        }

        if (TextUtils.isEmpty(email)) {
            eMail.setError(getString(R.string.error_field_required));
            focusView = eMail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            eMail.setError(getString(R.string.error_invalid_email));
            focusView = eMail;
            cancel = true;
        }

        if (TextUtils.isEmpty(password_1)) {
            firstPassword.setError(getString(R.string.error_field_required));
            focusView = firstPassword;
            cancel = true;
        } else if (!isPasswordValid(password_1)) {
            firstPassword.setError(getString(R.string.error_invalid_password));
            focusView = firstPassword;
            cancel = true;
        } else {
            if (TextUtils.isEmpty(password_2)) {
                secondPassword.setError(getString(R.string.error_field_required));
                focusView = secondPassword;
                cancel = true;

            }
            if (!isPasswordSame(password_1, password_2)) {
                secondPassword.setError(getString(R.string.password_not_same));
                focusView = secondPassword;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(user_name, email, password_1);
            mAuthTask.execute((Void) null);
        }


    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {
        private final String mUserName;
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String userName, String email, String password) {
            mUserName = userName;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Integer rettype = 0;

            try {
                String username = mUserName;
                String Email = mEmail;
                String Password = mPassword;
                // String encrptname = AES.encrypt(username);
                // String encrptEmail = AES.encrypt(Email);
                // String encrptPassword = AES.encrypt(Password);
                String url = "http://192.168.253.1/register/";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("username", username);
                map.put("email", Email);
                map.put("password", Password);
                //map.put("email", encrptEmail);
                // map.put("password", encrptPassword);
                //  map.put("username", encrptname);
                new JsonPost(map, url, "register", db);
                Thread.sleep(1000);
            } catch (MyException.emailInvalidException e) {
                rettype = 1;
            } catch (Exception e) {
                rettype = 2;
            }
            return rettype;
        }

        @Override
        protected void onPostExecute(final Integer rettype) {
            mAuthTask = null;
            showProgress(false);

            if (0 == rettype) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                Intent intent = getIntent();

                intent.putExtra("Email", mEmail);
                intent.putExtra("UserName", mUserName);
                intent.putExtra("Password", mPassword);

                RegisterActivity.this.setResult(RESULT_OK, intent);
                RegisterActivity.this.finish();

            } else if (1 == rettype) {
                eMail.setError("邮箱格式错误");
                myToast.show(getString(R.string.error_invalid_email));
            } else {
                myToast.show(getString(R.string.toast_register_failed));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}
