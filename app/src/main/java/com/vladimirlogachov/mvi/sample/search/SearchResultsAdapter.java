package com.vladimirlogachov.mvi.sample.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimirlogachov.mvi.sample.R;
import com.vladimirlogachov.mvi.sample.search.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ItemHolder> {

    private List<User> data = new ArrayList<>();

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        User user = data.get(position);
        holder.name.setText(user.getName());
        holder.age.setText(String.valueOf(user.getAge()));
        holder.country.setText(user.getCountry());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void updateData(List<User> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.country)
        TextView country;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
