package org.cgnetswara.mobilesatyagraha;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageModel> messageList;

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        final TextView textViewSender, textViewMessage, textViewDatetime;
        final MessageAdapter messageAdapter;
        private final Context context_adapter;
        public static final String ID = "id";
        public static final String PROBLEM_ID = "problem_id";
        public static final String MESSAGE = "message";
        public static final String SENDER = "sender";
        public static final String DATETIME = "datetime";

        MessageViewHolder(View itemView, MessageAdapter adapter) {
            super(itemView);
            context_adapter=itemView.getContext();
            textViewSender = (TextView)itemView.findViewById(R.id.miSender);
            textViewMessage = (TextView)itemView.findViewById(R.id.miMessage);
            textViewDatetime = (TextView)itemView.findViewById(R.id.miDatetime);
            itemView.setOnLongClickListener(this);
            this.messageAdapter=adapter;
        }


        @Override
        public boolean onLongClick(View view) {
            int position=getLayoutPosition();
            messageList.remove(position);
            return false;
        }
    }

    MessageAdapter(Context context, List<MessageModel> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_item, parent,false);
        return new MessageViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        holder.textViewSender.setText(message.getSender());
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewDatetime.setText(message.getDatetime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}