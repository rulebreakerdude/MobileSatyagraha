package org.cgnetswara.mobilesatyagraha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionPage extends AppCompatActivity {
    public static final String USERNAME="username";
    public static final String NAME = "name";
    public static final String REQUESTTAG1 = "requesttag1";
    public static final String REQUESTTAG2 = "requesttag2";
    private List<ProblemModel> problemList=new ArrayList<>();
    private RecyclerView recyclerView;
    private ProblemAdapter2 problemAdapter2;
    RequestQueue requestQueue;
    StringRequest stringRequest1;
    StringRequest stringRequest2;
    String username;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_page);
        Intent data = getIntent();
        name = data.getStringExtra("name");//hard coding because we aren't sure from which activity the user will land here
        setTitle("Welcome "+name);
        username=data.getStringExtra("username");
        recyclerView = (RecyclerView) findViewById(R.id.rAdoptedView);
        problemAdapter2 = new ProblemAdapter2(this,problemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(problemAdapter2);
    }

    @Override
    protected void onResume(){
        super.onResume();
        problemList.clear();
        loadProblems();
    }

    public void openProblemList(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(ActionPage.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Opening Problem List. Please wait...");
        progressDialog.show();
        String url = getString(R.string.base_url)+"canAdoptProblem/"+username;

        stringRequest2 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "No":
                                progressDialog.dismiss();
                                onResponseNo();
                                break;
                            case "Yes":
                                progressDialog.dismiss();
                                onResponseYes();
                                break;
                            default:
                                progressDialog.dismiss();
                                onUnknownResponse();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
        stringRequest2.setTag(REQUESTTAG2);
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest2.setShouldCache(false);
        requestQueue.add(stringRequest2);
    }

    private void onResponseYes() {
        Intent problemList=new Intent(this,ProblemListView.class);
        String username=getIntent().getStringExtra("username");
        String name=getIntent().getStringExtra("name");
        problemList.putExtra(USERNAME,username);
        problemList.putExtra(NAME,name);
        startActivity(problemList);
    }

    private void onResponseNo() {
        Toast.makeText(getBaseContext(), "Sorry! You have already adopted a problem. Can't adopt more.", Toast.LENGTH_LONG).show();
    }

    public void loadProblems(){
        final ProgressDialog progressDialog2 = new ProgressDialog(ActionPage.this,
                R.style.AppTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Loading your Problems.");
        progressDialog2.show();

        String url = getString(R.string.base_url)+"problemAgainstUser/"+username;


        stringRequest1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {


                                //getting product object from json array
                                JSONObject jsonProblem = array.getJSONObject(i);
                                ProblemModel problem=new ProblemModel();
                                problem.setId(jsonProblem.getString("problem_id"));
                                problem.setDesc(jsonProblem.getString("problem_desc"));
                                problem.setText(jsonProblem.getString("problem_text"));
                                problem.setCount(jsonProblem.getString("duration"));
                                problem.setDatetime(jsonProblem.getString("datetime"));


                                problem.setAccessingUser(username);
                                problem.setNameOfAccessingUser(name);

                                problemList.add(problem);
                                problemAdapter2.notifyDataSetChanged();

                            }
                            progressDialog2.dismiss();

                        } catch (JSONException e) {
                            progressDialog2.dismiss();
                            e.printStackTrace();
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
        stringRequest1.setTag(REQUESTTAG1);
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest1.setShouldCache(false);
        requestQueue.add(stringRequest1);
        problemAdapter2.notifyDataSetChanged();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG1);
            requestQueue.cancelAll(REQUESTTAG2);
        }
    }
/*
    public void reload(View view) {
        problemList.clear();
        loadProblems();

    }
*/
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void onUnknownResponse() {
        Toast.makeText(getBaseContext(), "Unknown Response", Toast.LENGTH_LONG).show();
    }
}
