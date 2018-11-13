package org.cgnetswara.mobilesatyagraha;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ProblemAdapter2 extends RecyclerView.Adapter<ProblemAdapter2.ProblemViewHolder> {

    private List<ProblemModel> problemList;
    private Typeface Hindi;

    public class ProblemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView textViewTitle, textViewShortDesc, textViewCount, textViewDatetime;
        final ProblemAdapter2 problemAdapter2;
        private final Context context_adapter;
        public static final String PROBLEM_ID = "problem_id";
        public static final String PROBLEM_DESC = "problem_desc";
        public static final String PROBLEM_TEXT = "problem_text";
        public static final String PROBLEM_COUNT = "count";
        public static final String PROBLEM_DATETIME = "datetime";
        public static final String ACCESSINGUSER = "accessing_user";
        public static final String NAMEOFACCESSINGUSER = "name_of_accessing_user";

        ProblemViewHolder(View itemView, ProblemAdapter2 adapter) {
            super(itemView);
            context_adapter=itemView.getContext();
            textViewTitle = (TextView)itemView.findViewById(R.id.piDesc);
            textViewShortDesc = (TextView)itemView.findViewById(R.id.piText);
            textViewCount = (TextView)itemView.findViewById(R.id.piCount);
            textViewDatetime = (TextView)itemView.findViewById(R.id.piDatetime);
            itemView.setOnClickListener(this);
            this.problemAdapter2=adapter;
        }

        @Override
        public void onClick(View view) {
            int position=getLayoutPosition();
            Intent pv = new Intent(context_adapter,AdoptedProblemView.class);

            ProblemModel problem=problemList.get(position);
            pv.putExtra(PROBLEM_ID,problem.getId());
            pv.putExtra(PROBLEM_DESC,problem.getDesc());
            pv.putExtra(PROBLEM_TEXT,problem.getText());
            pv.putExtra(PROBLEM_COUNT,problem.getCount());
            pv.putExtra(PROBLEM_DATETIME,problem.getDatetime());
            pv.putExtra(ACCESSINGUSER,problem.getAccessingUser());
            pv.putExtra(NAMEOFACCESSINGUSER,problem.getNameOfAccessingUser());

            context_adapter.startActivity(pv);
        }
    }

    ProblemAdapter2(Context context, List<ProblemModel> problemList) {
        this.problemList = problemList;
        Hindi=Typeface.createFromAsset(context.getAssets(), "fonts/MANGAL.TTF");
    }


    @Override
    public ProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.problem_item, parent,false);
        return new ProblemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ProblemViewHolder holder, int position) {
        ProblemModel problem = problemList.get(position);
        holder.textViewTitle.setTypeface(Hindi);
        holder.textViewShortDesc.setTypeface(Hindi);
        String desc, text;

        if(problem.getDesc().length()>40){
            desc=problem.getDesc().substring(0,36)+"...";
        }
        else desc=problem.getDesc();
        holder.textViewTitle.setText(desc);

        if(problem.getText().length()>100){
            text=problem.getText().substring(0,96)+"...";
        }
        else text=problem.getText();
        holder.textViewShortDesc.setText(text);

        holder.textViewCount.setText(problem.getCount());
        holder.textViewDatetime.setText(problem.getDatetime());
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }
}
