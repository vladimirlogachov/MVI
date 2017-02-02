package com.vladimirlogachov.mvi.sample.departments.model;

import android.util.Log;

import com.vladimirlogachov.mvi.sample.departments.model.firebase.DepartmentsLoadEngine;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class DepartmentsInteractor {
    public static final String TAG = DepartmentsInteractor.class.getSimpleName();
    private final DepartmentsLoadEngine departmentsLoadEngine;

    public DepartmentsInteractor(DepartmentsLoadEngine departmentsLoadEngine) {
        this.departmentsLoadEngine = departmentsLoadEngine;
    }

    public Observable<DepartmentsViewState> loadDepartments() {
        Log.d(TAG, "loadDepartments: ");
        return departmentsLoadEngine.loadDepartments()
//                .mergeWith(departmentsLoadEngine.getDepartmentEmployees())
                .zipWith(departmentsLoadEngine.getDepartmentEmployees(), new BiFunction<List<Department>, List<DepartmentEmployee>, List<PayloadItem>>() {
                    @Override
                    public  List<PayloadItem> apply(List<Department> departments, List<DepartmentEmployee> employees) throws Exception {
                        List<PayloadItem> zipResult = new ArrayList<>(departments.size() + employees.size());
                        for (Department department : departments) {
                            zipResult.add(department);
                            for (DepartmentEmployee employee : employees)
                                if (employee.getWorkplace().equals(department.getId()))
                                    zipResult.add(employee);
                        }
                        return zipResult;
                    }
                })
                .map(new Function<List<PayloadItem>, DepartmentsViewState>() {
                    @Override
                    public DepartmentsViewState apply(List<PayloadItem> data) throws Exception {
                        return DepartmentsViewState.ResultState(data);
                    }
                })
                .startWith(DepartmentsViewState.LoadingDepartmentState())
                .onErrorReturn(new Function<Throwable, DepartmentsViewState>() {
                    @Override
                    public DepartmentsViewState apply(Throwable throwable) throws Exception {
                        return DepartmentsViewState.LoadingDepartmentErrorState(throwable);
                    }
                });
    }

    public Observable<DepartmentsViewState> refreshData() {
        Log.d(TAG, "refreshData: ");
        return departmentsLoadEngine.loadDepartments()
                .zipWith(departmentsLoadEngine.getDepartmentEmployees(), new BiFunction<List<Department>, List<DepartmentEmployee>, List<PayloadItem>>() {
                    @Override
                    public  List<PayloadItem> apply(List<Department> departments, List<DepartmentEmployee> employees) throws Exception {
                        List<PayloadItem> zipResult = new ArrayList<>(departments.size() + employees.size());
                        for (Department department : departments) {
                            zipResult.add(department);
                            for (DepartmentEmployee employee : employees)
                                if (employee.getWorkplace().equals(department.getId()))
                                    zipResult.add(employee);
                        }
                        return zipResult;
                    }
                })
                .map(new Function<List<PayloadItem>, DepartmentsViewState>() {
                    @Override
                    public DepartmentsViewState apply(List<PayloadItem> data) throws Exception {
                        Log.d(TAG, "apply() called with: data = [" + data + "]");
                        return DepartmentsViewState.ResultState(data);
                    }
                })
                .startWith(DepartmentsViewState.LoadingPullToRefreshState())
                .onErrorReturn(new Function<Throwable, DepartmentsViewState>() {
                    @Override
                    public DepartmentsViewState apply(Throwable throwable) throws Exception {
                        return DepartmentsViewState.LoadingPullToRefreshErrorState(throwable);
                    }
                });
    }
}
