package org.cgnetswara.mobilesatyagraha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ProblemListView extends AppCompatActivity {
    private List<ProblemModel> problemList=new ArrayList<>();
    private RecyclerView recyclerView;
    private ProblemAdapter problemAdapter;
    public static final String REQUESTTAG = "requesttag";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    private String username;
    public static final String USERNAME = "username";
    private int start;
    private int end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_list_view);
        Intent data=getIntent();
        username=data.getStringExtra(ActionPage.USERNAME);
        recyclerView = (RecyclerView) findViewById(R.id.rView);
        //creating adapter object and setting it to recyclerview
        problemAdapter = new ProblemAdapter(this,problemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(problemAdapter);
        start=0;
        end=start+10;
        loadProblems();
    }

    public void loadProblems(){
        final ProgressDialog progressDialog = new ProgressDialog(ProblemListView.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Problems. Please wait...");
        progressDialog.show();

        String url = getString(R.string.base_url)+"pblock/"+start+"/"+end;

            stringRequest = new StringRequest(Request.Method.GET, url,
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

                                problemList.add(problem);
                                problemAdapter.notifyDataSetChanged();

                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
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
        stringRequest.setTag(REQUESTTAG);
        requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        //sample problems
        /*
        ProblemModel problem1=new ProblemModel();
        problem1.setId("123");
        String desc="\u0917";
        problem1.setDesc(desc);
        problem1.setText("problem text");
        problem1.setCount("0/2");
        problem1.setDatetime("29th Aug");
        problem1.setAccessingUser(username);
        problemList.add(problem1);
        ProblemModel problem2=new ProblemModel();
        problem2.setId("123");
        problem2.setDesc("problem alok");
        problem2.setText("problem text the other thing to see is this now.long type list");
        problem2.setCount("1/2");
        problem2.setDatetime("1st Sep");
        problem2.setAccessingUser(username);
        problemList.add(problem2);
        */

        problemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG);
        }
    }

    public void showNewer(View view) {
        if (start-10 >= 0){
            start=start-10;
            end=start+10;
            problemList.clear();
            loadProblems();
        }
    }

    public void showOlder(View view) {
            start=start+10;
            end=start+10;
            problemList.clear();
            loadProblems();
    }

    public void reload(View view) {
        loadProblems();
    }

    public void search(View view) {
        Intent searchButtonIntent=new Intent(this,SearchPage.class);
        searchButtonIntent.putExtra(USERNAME,username);
        startActivity(searchButtonIntent);
    }
}