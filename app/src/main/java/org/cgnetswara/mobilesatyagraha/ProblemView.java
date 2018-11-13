package org.cgnetswara.mobilesatyagraha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ProblemView extends AppCompatActivity {

    public static final String REQUESTTAG2 = "requesttag2";
    StringRequest stringRequest2;
    RequestQueue requestQueue;
    Typeface Hindi;
    String username;
    String problem_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_view);

        Intent endpoint = getIntent();
        problem_id = endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.PROBLEM_ID);
        username = endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.ACCESSINGUSER);

        final TextView problem_desc = (TextView) findViewById(R.id.pvDesc);
        final TextView problem_text = (TextView) findViewById(R.id.pvText);
        final TextView problem_dt = (TextView) findViewById(R.id.pvDatetime);
        final TextView problem_count = (TextView) findViewById(R.id.pvCount);

        problem_desc.setText(endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.PROBLEM_DESC));
        problem_text.setText(endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.PROBLEM_TEXT));
        problem_dt.setText(endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.PROBLEM_DATETIME));
        problem_count.setText(endpoint.getStringExtra(ProblemAdapter.ProblemViewHolder.PROBLEM_COUNT));

        Hindi = Typeface.createFromAsset(getAssets(),"fonts/MANGAL.TTF");
        problem_desc.setTypeface(Hindi);
        problem_text.setTypeface(Hindi);
    }

    public void adoptProblem(View view) {
        final ProgressDialog progressDialog2 = new ProgressDialog(ProblemView.this,
                R.style.AppTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Please Wait.");
        progressDialog2.show();
        String url= getString(R.string.base_url)+"adoptProblem/"+username+"/"+problem_id;

        stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "Adopted":
                                progressDialog2.dismiss();
                                onAdoption();
                                break;
                            case "Users Full":
                                progressDialog2.dismiss();
                                onUsersFull();
                                break;
                            default:
                                progressDialog2.dismiss();
                                onUnknownResponse();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog2.dismiss();
                        Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
        stringRequest2.setTag(REQUESTTAG2);
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(stringRequest2);
    }

    private void onUnknownResponse() {
        Toast.makeText(getBaseContext(), "Unknown Response", Toast.LENGTH_LONG).show();
    }

    private void onUsersFull() {
        Toast.makeText(getBaseContext(), "Sorry! Users are full for this problem.", Toast.LENGTH_LONG).show();
    }

    private void onAdoption() {
        Toast.makeText(getBaseContext(), "Problem Adopted!", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG2);
        }
    }

}
