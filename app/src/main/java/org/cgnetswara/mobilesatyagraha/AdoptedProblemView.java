package org.cgnetswara.mobilesatyagraha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdoptedProblemView extends AppCompatActivity {

    public static final String REQUESTTAG3 = "requesttag3";
    public static final String REQUESTTAG2 = "requesttag2";
    public static final String REQUESTTAG1 = "requesttag1";
    StringRequest stringRequest1;
    StringRequest stringRequest2;
    StringRequest stringRequest3;
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<MessageModel> commentList=new ArrayList<>();
    Typeface Hindi;
    String username;
    String name;
    String problem_id;
    public static final String PROBLEM_ID = "problem_id";
    public static final String USERNAME="username";
    public static final String NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopted_problem_view);
        requestQueue=Volley.newRequestQueue(this);

        Intent endpoint = getIntent();
        problem_id = endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_ID);
        username = endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.ACCESSINGUSER);
        name=endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.NAMEOFACCESSINGUSER);
        Log.d("problem desc",endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_DESC));
        final TextView problem_desc = (TextView) findViewById(R.id.apvDesc);
        final TextView problem_text = (TextView) findViewById(R.id.apvText);
        final TextView problem_dt = (TextView) findViewById(R.id.apvDatetime);
        final TextView problem_count = (TextView) findViewById(R.id.apvCount);
        try {

            problem_desc.setText(endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_DESC));
            problem_text.setText(endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_TEXT));
            Linkify.addLinks(problem_text,Linkify.ALL);
            problem_dt.setText(endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_DATETIME));
            problem_count.setText(endpoint.getStringExtra(ProblemAdapter2.ProblemViewHolder.PROBLEM_COUNT));

            Hindi = Typeface.createFromAsset(getAssets(), "fonts/MANGAL.TTF");
            problem_desc.setTypeface(Hindi);
            problem_text.setTypeface(Hindi);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.rCommentView);
        commentAdapter = new CommentAdapter(this,commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentAdapter);
        loadComments();
    }

    public void loadComments(){
        commentList.clear();
        final ProgressDialog progressDialog2 = new ProgressDialog(AdoptedProblemView.this,
                R.style.AppTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Loading your Problems.");
        progressDialog2.show();

        String url = getString(R.string.base_url)+"fetchComments/"+problem_id;


        stringRequest3 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            Log.e("ccc",response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {


                                //getting product object from json array
                                JSONObject jsonProblem = array.getJSONObject(i);
                                MessageModel comment=new MessageModel();
                                comment.setSender(jsonProblem.getString("username"));
                                comment.setMessage(jsonProblem.getString("comments"));
                                comment.setDatetime(jsonProblem.getString("datetime").substring(0,4)+"/"+
                                        jsonProblem.getString("datetime").substring(4,6)+"/"+
                                        jsonProblem.getString("datetime").substring(6,8));

                                commentList.add(comment);
                                commentAdapter.notifyDataSetChanged();

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
        stringRequest3.setTag(REQUESTTAG3);
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        stringRequest3.setShouldCache(false);
        requestQueue.add(stringRequest3);
        commentAdapter.notifyDataSetChanged();
    }

    public void unAdoptProblem(View view) {
        final ProgressDialog progressDialog2 = new ProgressDialog(AdoptedProblemView.this,
                R.style.AppTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Please Wait.");
        progressDialog2.show();
        String url= getString(R.string.base_url)+"unAdoptProblem/"+username+"/"+problem_id;

        stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("UnAdopted")){
                            progressDialog2.dismiss();
                            onUnAdoption();
                        }
                        else {
                            progressDialog2.dismiss();
                            onUnknownResponse();
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
        requestQueue.add(stringRequest2);
    }

    private void onUnknownResponse() {
        Toast.makeText(getBaseContext(), "Unknown Response", Toast.LENGTH_LONG).show();
    }

    private void onUnAdoption() {
        Toast.makeText(getBaseContext(), "Problem Un-Adopted!", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG2);
            requestQueue.cancelAll(REQUESTTAG1);
        }
    }

    public void submitComment(View view) {
        final ProgressDialog progressDialog2 = new ProgressDialog(AdoptedProblemView.this,
                R.style.AppTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Please Wait.");
        progressDialog2.show();
        EditText inputComments=findViewById(R.id.inputComments);
        final String comment=inputComments.getText().toString();
        Log.d("comment",comment);
        String url = getString(R.string.base_url)+"registerComment";

        stringRequest1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Done")){
                            progressDialog2.dismiss();
                            onResponseDone();
                        }
                        else {
                            Log.d("response",response);
                            progressDialog2.dismiss();
                            onUnknownResponse();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog2.dismiss();
                Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map <String,String> params = new HashMap<String,String>();
                params.put("username",username);
                params.put("problem_id",problem_id);
                params.put("comment",comment);
                return params;
            }
        };
        stringRequest1.setTag(REQUESTTAG1);
        requestQueue.add(stringRequest1);
    }

    private void onResponseDone() {
        Toast.makeText(getBaseContext(), "Done!", Toast.LENGTH_LONG).show();
        loadComments();
    }

    public void openChat(View view) {
        Intent startChat=new Intent(this,ChatPage.class);
        startChat.putExtra(USERNAME,username);
        startChat.putExtra(NAME,name);
        startChat.putExtra(PROBLEM_ID,problem_id);
        startActivity(startChat);
    }
}
