package org.cgnetswara.mobilesatyagraha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_signup);
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }


    public void createAccount(View view) {
        EditText inputUser=findViewById(R.id.inputUser);
        EditText inputPass=findViewById(R.id.inputPass);
        EditText inviteCode=findViewById(R.id.inviteCode);
        EditText inputName=findViewById(R.id.inputName);
        EditText inputEmail=findViewById(R.id.inputEmail);

        final String username=inputUser.getText().toString();
        final String password=inputPass.getText().toString();
        final String code=inviteCode.getText().toString();
        final String name=inputName.getText().toString();
        final String email=inputEmail.getText().toString();

        if(!validate()){
            onResponseRetry();
        }
        else {
            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing you up. This may take a moment...");
            progressDialog.show();


            requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "signup";

            stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            switch (response) {
                                case "Retry":
                                    progressDialog.dismiss();
                                    onResponseRetry();
                                    break;
                                case "User Exists":
                                    progressDialog.dismiss();
                                    onUserExists();
                                    break;
                                case "Successful Signup":
                                    progressDialog.dismiss();
                                    onSuccessfulSignup(username, name, password);
                                    break;
                                default:
                                    progressDialog.dismiss();
                                    onUnknownResponse();
                                    break;
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
                    params.put("inviteCode", code);
                    params.put("name", name);
                    params.put("email", email);
                    return params;
                }
            };
            stringRequest.setTag(REQUESTTAG);
            requestQueue.add(stringRequest);
        }
    }

    public void onSuccessfulSignup(String username,String name, String password){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username_local_save", username);
        editor.putString("password_local_save", password);
        editor.apply();
        Toast.makeText(getBaseContext(), "Authentication Successful", Toast.LENGTH_LONG).show();
        Intent afterSignup=new Intent(this,ActionPage.class);
        afterSignup.putExtra(USERNAME,username);
        afterSignup.putExtra(NAME,name);
        startActivity(afterSignup);
    }

    public void onResponseRetry(){
        Toast.makeText(getBaseContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
    }

    public void onUserExists(){
        Toast.makeText(getBaseContext(), "Username taken", Toast.LENGTH_LONG).show();
    }

    public void openLoginPage(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        finish();
    }

    public boolean validate() {
        boolean valid = true;
        EditText inputUser=findViewById(R.id.inputUser);
        EditText inputPass=findViewById(R.id.inputPass);

        final String username=inputUser.getText().toString();
        final String password=inputPass.getText().toString();

        if (username.isEmpty() || username.length() != 10) {
            inputUser.setError("at least 10 digits");
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
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG);
        }
    }

    private void onUnknownResponse() {
        Toast.makeText(getBaseContext(), "Unknown Response", Toast.LENGTH_LONG).show();
    }
}
