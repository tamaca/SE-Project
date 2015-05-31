package com.example.team.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.team.myapplication.util.GeneralActivity;


public class ChangePasswordActivity extends GeneralActivity {
    private ProgressBar progressBar;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        scrollView = (ScrollView)findViewById(R.id.scrollView2);

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
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isPasswordValid(String password) {

        return password.length() > 5 && password.length() <= 15;
    }
    public void showProgress(boolean show){
        progressBar.setVisibility(show?View.VISIBLE:View.GONE);
        scrollView.setVisibility(show?View.GONE:View.VISIBLE);

    }
    public void changePassword(View view){
        String oldPassword;
        String newPassword;
        EditText text1 = (EditText)findViewById(R.id.editText2);
        EditText text2 = (EditText)findViewById(R.id.editText3);
        oldPassword = text1.getText().toString();
        newPassword = text2.getText().toString();


        String password = "11111111";//TODO 在这里获得原密码 此处作为示范
        if(oldPassword.isEmpty()){
            text1.setError(getString(R.string.error_field_required));
            return;
        }
        if(oldPassword.equals(password)){
            if(newPassword.isEmpty()){
                text2.setError(getString(R.string.error_field_required));
                return;
            }
            if(oldPassword.equals(newPassword)){
                text2.setError("新旧密码相同");
                return;
            }
            if(isPasswordValid(newPassword)){
                showProgress(true);
                ChangePasswordProgress changePasswordProgress = new ChangePasswordProgress(newPassword);
                changePasswordProgress.execute((Void)null);
            }
            else{
                text2.setError(getString(R.string.error_invalid_password));
            }
        }
        else {
            text1.setError(getString(R.string.password_not_same));
        }



    }
    class ChangePasswordProgress extends AsyncTask<Void,Void,Boolean>{
        private String password;
        public ChangePasswordProgress(String password){
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                //TODO 在这里上传新密码
                Thread.sleep(1000);

            }catch (Exception e){
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success){
            showProgress(false);
            if(success){
                Toast.makeText(getApplicationContext(),"修改成功!",Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();

            }
        }
    }
}
