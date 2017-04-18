package com.jd.wly.intercom.users;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jd.wly.intercom.R;

import java.util.List;

/**
 * Created by yanghao1 on 2017/4/13.
 */

public class IntercomAdapter extends RecyclerView.Adapter<IntercomAdapter.ViewHolder> {

    private List<IntercomUserBean> userBeanList;

    public IntercomAdapter(List<IntercomUserBean> userBeanList) {
        this.userBeanList = userBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.intercom_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IntercomUserBean user = userBeanList.get(position);
        holder.userName.setText(user.getAliasName() == null ?
                user.getIpAddress() : user.getAliasName());
    }

    @Override
    public int getItemCount() {
        return userBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View itemView;
        public final TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            userName = (TextView) itemView.findViewById(R.id.intercom_user_name_tv);
        }
    }
}
