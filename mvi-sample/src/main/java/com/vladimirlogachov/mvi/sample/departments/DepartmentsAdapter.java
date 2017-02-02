package com.vladimirlogachov.mvi.sample.departments;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimirlogachov.mvi.sample.R;
import com.vladimirlogachov.mvi.sample.departments.model.Department;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentEmployee;
import com.vladimirlogachov.mvi.sample.departments.model.PayloadItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class DepartmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef
    private @interface ItemTypes {
        int DEPARTMENT = 0x10;
        int EMPLOYEE = 0x20;
    }

    private List<PayloadItem> data;

    public DepartmentsAdapter() {
        data = new ArrayList<>();
    }

    void updateData(List<PayloadItem> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolderFactory.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PayloadItem item = data.get(position);
        if (holder instanceof DepartmentViewHolder)
            setupDepartmentHolder((DepartmentViewHolder) holder, (Department) item);
        else if (holder instanceof EmployeeViewHolder)
            setupEmployeeViewHolder((EmployeeViewHolder) holder, (DepartmentEmployee) item);

    }

    private void setupDepartmentHolder(DepartmentViewHolder holder, Department department) {
        holder.department.setText(department.getName());
    }

    private void setupEmployeeViewHolder(EmployeeViewHolder holder, DepartmentEmployee employee) {
        holder.name.setText(employee.getName());
        holder.age.setText(String.valueOf(employee.getAge()));
        holder.country.setText(employee.getCountry());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        PayloadItem item = data.get(position);

        if (item instanceof Department)
            return ItemTypes.DEPARTMENT;
        else if (item instanceof DepartmentEmployee)
            return ItemTypes.EMPLOYEE;

        return super.getItemViewType(position);
    }

    private static class ViewHolderFactory {
        static RecyclerView.ViewHolder createViewHolder(ViewGroup parent, @ItemTypes int viewType) {
            if (viewType == ItemTypes.DEPARTMENT)
                return new DepartmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_department, parent, false));
            else if (viewType == ItemTypes.EMPLOYEE)
                return new EmployeeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));

            return null;
        }
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.age)
        TextView age;
        @BindView(R.id.country)
        TextView country;
        EmployeeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.department)
        TextView department;

        DepartmentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
