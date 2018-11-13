package org.cgnetswara.mobilesatyagraha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String REQUESTTAG = "requesttag";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sp;
    public static final String MyPREFERENCES = "MyPrefs" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sp.contains("username_local_save")){
            String username=sp.getString("username_local_save","DNE");
            String password=sp.getString("password_local_save","DNE");
            loginAccount(username,password);
        }
    }


    public void loginAccount(View view) {

        EditText inputUser = findViewById(R.id.inputUser);
        EditText inputPass = findViewById(R.id.inputPass);

        String username = inputUser.getText().toString();
        String password = inputPass.getText().toString();
        if(!validate() && !username.equals("1234")){
            Toast.makeText(getBaseContext(), "Invalid Entries", Toast.LENGTH_LONG).show();
        }
        else {
            loginAccount(username, password);
        }
    }

    public void loginAccount(final String username,final String password){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        requestQueue = Volley.newRequestQueue(this);

        String url = getString(R.string.base_url) + "login";

        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String reply = jsonResponse.getString("reply");
                            Log.d("aaa", reply);
                            switch (reply) {
                                case "User does not exist":
                                    progressDialog.dismiss();
                                    onNoUser();
                                    break;
                                case "Login Unsuccessful":
                                    progressDialog.dismiss();
                                    onUnsuccessfulLogin();
                                    break;
                                case "Successful Login":
                                    progressDialog.dismiss();
                                    String name = jsonResponse.getString("name");
                                    onSuccessfulLogin(username, name, password);
                                    break;
                                default:
                                    progressDialog.dismiss();
                                    onUnknownResponse();
                                    break;
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };



        stringRequest.setTag(REQUESTTAG);
        requestQueue.add(stringRequest);

    }



    public void onSuccessfulLogin(String username,String name, String password){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username_local_save", username);
        editor.putString("password_local_save", password);
        editor.apply();
        Toast.makeText(getBaseContext(), "Authentication Successful", Toast.LENGTH_LONG).show();
        Intent afterLogin=new Intent(this,ActionPage.class);
        afterLogin.putExtra(USERNAME,username);
        afterLogin.putExtra(NAME,name);
        startActivity(afterLogin);
    }


    public void onNoUser(){
        Toast.makeText(getBaseContext(), "User does not exist", Toast.LENGTH_LONG).show();
    }

    public void onUnsuccessfulLogin(){
        Toast.makeText(getBaseContext(), "Password Incorrect", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void openSignupPage(View view) {
        Intent su=new Intent(this,SignupActivity.class);
        startActivity(su);
    }

    public boolean validate() {
        boolean valid = true;
        EditText inputUser=findViewById(R.id.inputUser);
        EditText inputPass=findViewById(R.id.inputPass);

        final String username=inputUser.getText().toString();
        final String password=inputPass.getText().toString();

        if (username.isEmpty() || username.length() != 10) {
            inputUser.setError("at least 10 characters");
            valid = false;
        } else {
            inputUser.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPass.setError(null);
        }

        return valid;
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG);
        }
    }

    private void onUnknownResponse() {
        Toast.makeText(getBaseContext(), "Unknown Response", Toast.LENGTH_LONG).show();
    }
}
