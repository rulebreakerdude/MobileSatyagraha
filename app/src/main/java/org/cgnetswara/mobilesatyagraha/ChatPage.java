package org.cgnetswara.mobilesatyagraha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatPage extends AppCompatActivity {

    private List<MessageModel> messageList=new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private Socket mSocket;
    public static final String REQUESTTAG = "requesttag";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    private String problemId;
    private String username;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent data = getIntent();
        problemId = data.getStringExtra(AdoptedProblemView.PROBLEM_ID);
        username = data.getStringExtra(AdoptedProblemView.USERNAME);
        name = data.getStringExtra(AdoptedProblemView.NAME);

        recyclerView=findViewById(R.id.rView);
        messageAdapter = new MessageAdapter(this,messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(messageAdapter);
        loadMessages();
        mSocket=ChatApplication.getSocket();
        mSocket.connect();

        JSONObject socketJoinRoom=new JSONObject();
        try {
            socketJoinRoom.put("username",username);
            socketJoinRoom.put("room", problemId);
        }catch(JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("join", socketJoinRoom);

        mSocket.on("my response", onNewMessage);
    }

    public void loadMessages(){
        final ProgressDialog progressDialog = new ProgressDialog(ChatPage.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Previous Messages. Just a sec!");
        progressDialog.show();

        String url = getString(R.string.base_url)+"loadChat/"+problemId;

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
                                JSONObject jsonMessageItem = array.getJSONObject(i);
                                MessageModel message=new MessageModel();
                                message.setId(Integer.parseInt(jsonMessageItem.getString("id")));
                                message.setProblem_id(jsonMessageItem.getString("problem_id"));
                                message.setSender(jsonMessageItem.getString("sender"));
                                message.setMessage(jsonMessageItem.getString("message"));
                                message.setDatetime(jsonMessageItem.getString("datetime"));

                                messageList.add(message);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        } catch (NullPointerException e) {
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
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        messageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUESTTAG);
        }
    }

    public void attemptSend(View view) {
        EditText inputMessage=findViewById(R.id.inputMessage);
        String message = inputMessage.getText().toString();
        inputMessage.setText("");
        JSONObject jsonMessageItem=new JSONObject();
        try {
            jsonMessageItem.put("problem_id",problemId);
            jsonMessageItem.put("username", name);
            jsonMessageItem.put("message",message);
        }catch(JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("my event", jsonMessageItem);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                        MessageModel messageItem = new MessageModel();
                        try {
                            if (data.getString("problem_id").equals(problemId)) {
                                messageItem.setSender(data.getString("username"));
                                messageItem.setMessage(data.getString("message"));
                                messageItem.setDatetime(data.getString("datetime"));
                                messageList.add(messageItem);
                                messageAdapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

            });
        }
    };
}
