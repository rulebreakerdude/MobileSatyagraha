package org.cgnetswara.mobilesatyagraha;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<MessageModel> commentList;

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        final TextView textViewSender, textViewMessage, textViewDatetime;
        final CommentAdapter commentAdapter;
        private final Context context_adapter;
        public static final String ID = "id";
        public static final String PROBLEM_ID = "problem_id";
        public static final String MESSAGE = "message";
        public static final String SENDER = "sender";
        public static final String DATETIME = "datetime";

        CommentViewHolder(View itemView, CommentAdapter adapter) {
            super(itemView);
            context_adapter=itemView.getContext();
            textViewSender = (TextView)itemView.findViewById(R.id.piDesc);
            textViewMessage = (TextView)itemView.findViewById(R.id.piText);
            textViewDatetime = (TextView)itemView.findViewById(R.id.piDatetime);
            itemView.setOnLongClickListener(this);
            this.commentAdapter=adapter;
        }


        @Override
        public boolean onLongClick(View view) {
            int position=getLayoutPosition();
            commentList.remove(position);
            return false;
        }
    }

    CommentAdapter(Context context, List<MessageModel> commentList) {
        this.commentList = commentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.problem_item, parent,false);
        return new CommentViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        MessageModel comment = commentList.get(position);
        holder.textViewSender.setText(comment.getSender());
        holder.textViewMessage.setText(comment.getMessage());
        holder.textViewDatetime.setText(comment.getDatetime());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}