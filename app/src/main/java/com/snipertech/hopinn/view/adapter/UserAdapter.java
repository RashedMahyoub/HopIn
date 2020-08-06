package com.snipertech.hopinn.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.snipertech.hopinn.R;
import com.snipertech.hopinn.model.Requests;
import com.snipertech.hopinn.model.User;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> listdata;
    private Context mContext;
    private OnUserClickListener mListener;

    public UserAdapter(Context context, List<User> usersList) {
        this.mContext = context;
        if (usersList != null) {
            listdata = usersList;
        }
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = listdata.get(position);
        holder.name.setText(user.getUsername());
        holder.lastSpoken.setText(user.getLastSpoken());
        Glide.with(mContext)
                .applyDefaultRequestOptions(
                        new RequestOptions()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                )
                .load(user.getProfileUri())
                .centerCrop()
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        if(listdata != null){
            return listdata.size();
        }else{
            return 0;
        }
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public User getUserAt(int position) {
        return listdata.get(position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        mListener = listener;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView lastSpoken;
        private CircleImageView profileImage;
        UserViewHolder(View view, final OnUserClickListener listener) {
            super(view);
            name = itemView.findViewById(R.id.user_name);
            lastSpoken = itemView.findViewById(R.id.last_spoken);
            profileImage = itemView.findViewById(R.id.profile_pic);


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