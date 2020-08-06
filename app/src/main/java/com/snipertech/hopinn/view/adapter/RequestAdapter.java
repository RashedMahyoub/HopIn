package com.snipertech.hopinn.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context mContext;
    private List<Requests> requestList;
    private OnItemClickListener mListener;

    public RequestAdapter(Context context, List<Requests> requestsList) {
        this.mContext = context;
        if (requestsList!= null) {
            requestList = requestsList;
        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.request_item, parent, false);

        return new RequestViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Requests request = requestList.get(position);
        holder.nameText.setText(request.getName());
        holder.messageText.setText(request.getMessage());
        holder.requestTime.setText(request.getRequestTime());
        if(request.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.nameText.setText(mContext.getResources().getString(R.string.you));
        }
    }


    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView messageText;
        private TextView requestTime;

        RequestViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameText = itemView.findViewById(R.id.name_edit_text);
            messageText = itemView.findViewById(R.id.name_text_input);
            requestTime = itemView.findViewById(R.id.request_time);
            MaterialButton chat = itemView.findViewById(R.id.chat);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            chat.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRequestClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(requestList != null){
            return requestList.size();
        } else{
            return 0;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onRequestClick(int position);
    }

    public Requests getMessageAt(int position) {
        return requestList.get(position);
    }
}
