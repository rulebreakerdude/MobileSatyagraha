package org.cgnetswara.mobilesatyagraha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SearchPage extends AppCompatActivity {
    private String username;
    public static final String USERNAME = "username";
    public static final String QUERY = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Intent data=getIntent();
        username=data.getStringExtra(ProblemListView.USERNAME);
    }

    public void loadResults(View view) {
        Intent searchButtonIntent=new Intent(this,SearchProblemListView.class);
        searchButtonIntent.putExtra(USERNAME,username);
        EditText inputQuery = findViewById(R.id.inputQuery);
        String query=inputQuery.getText().toString();
        searchButtonIntent.putExtra(QUERY,query);
        startActivity(searchButtonIntent);
    }
}
