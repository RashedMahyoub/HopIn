package com.snipertech.hopinn.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.model.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private List<Message> listdata;
    private Context mContext;
    private OnUserClickListener mListener;
    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
        if (messageList!= null) {
            listdata = messageList;
        }
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if(viewType == MSG_TYPE_RIGHT){
            View view = layoutInflater.inflate(R.layout.right_message_item,
                    parent, false);
            return new ChatViewHolder(view, mListener);
        }else{
            View view = layoutInflater.inflate(R.layout.left_message_item,
                    parent, false);
            return new ChatViewHolder(view, mListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message msg = listdata.get(position);
        holder.message.setText(msg.getMessage());

        if(position == listdata.size()-1){
            if (msg.getIsSeen().equals("true")){
                holder.check.setText(R.string.is_seen);
            } else {
                holder.check.setText(R.string.delivered);
            }
        } else {
            holder.check.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(listdata.get(position).getSender().equals((firebaseUser.getUid()))){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        mListener = listener;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView message;
        private MaterialTextView check;
        ChatViewHolder(View view, final OnUserClickListener listener) {
            super(view);
            message = itemView.findViewById(R.id.received_message);
            check = itemView.findViewById(R.id.check_seen);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
}