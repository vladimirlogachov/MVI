package com.vladimirlogachov.mvi.sample.departments.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepartmentsViewState {
    private boolean loadingDepartments;
    private Throwable loadingDepartmentsError;
    private List<PayloadItem> data;
    private boolean loadingPullToRefresh;
    private Throwable pullToRefreshError;

    private DepartmentsViewState(boolean loadingDepartments, Throwable loadingDepartmentsError, List<PayloadItem> data,
                                 boolean loadingPullToRefresh, Throwable pullToRefreshError) {
        this.loadingDepartments = loadingDepartments;
        this.loadingDepartmentsError = loadingDepartmentsError;
        this.data = data;
        this.loadingPullToRefresh = loadingPullToRefresh;
        this.pullToRefreshError = pullToRefreshError;
    }

    public static DepartmentsViewState LoadingDepartmentState() {
        return new DepartmentsViewState.Builder()
                .loadingDepartments(true)
                .build();
    }

    public static DepartmentsViewState LoadingDepartmentErrorState(Throwable error) {
        return new DepartmentsViewState.Builder()
                .loadingDepartmentsError(error)
                .build();
    }

    public static DepartmentsViewState ResultState(List<PayloadItem> data) {
        return new DepartmentsViewState.Builder()
                .data(data)
                .build();
    }

    public static DepartmentsViewState LoadingPullToRefreshState() {
        return new DepartmentsViewState.Builder()
                .loadingPullToRefresh(true)
                .build();
    }

    public static DepartmentsViewState LoadingPullToRefreshErrorState(Throwable error) {
        return new DepartmentsViewState.Builder()
                .pullToRefreshError(error)
                .build();
    }

    public boolean isLoadingDepartmentsState() {
        return loadingDepartments;
    }

    public boolean isLoadingPullToRefreshState() {
        return loadingPullToRefresh;
    }

    public boolean isResultState() {
        return !data.isEmpty();
    }

    public List<PayloadItem> getResult() {
        return data;
    }

    public boolean isLoadingDepartmentsErrorState() {
        return loadingDepartmentsError != null;
    }

    public Throwable getLoadingDepartmentsError() {
        return loadingDepartmentsError;
    }

    public boolean isPullToRefreshErrorState() {
        return pullToRefreshError != null;
    }

    public Throwable getPullToRefreshError() {
        return pullToRefreshError;
    }

    public Builder builder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "DepartmentsViewState{" +
                "loadingDepartments=" + loadingDepartments +
                ", loadingDepartmentsError=" + loadingDepartmentsError +
                ", data=" + data +
                ", loadingPullToRefresh=" + loadingPullToRefresh +
                ", pullToRefreshError=" + pullToRefreshError +
                '}';
    }

    public static final class Builder {
        private boolean loadingDepartments;
        private Throwable loadingDepartmentsError;
        private List<PayloadItem> data;
        private boolean loadingPullToRefresh;
        private Throwable pullToRefreshError;

        public Builder() {
            data = Collections.emptyList();
        }

        public Builder (DepartmentsViewState copySource) {
            this.data = new ArrayList<>(copySource.getResult().size());
            this.data.addAll(copySource.getResult());
            this.loadingDepartments = copySource.isLoadingDepartmentsState();
            this.loadingDepartmentsError = copySource.getLoadingDepartmentsError();
            this.loadingPullToRefresh = copySource.isLoadingPullToRefreshState();
            this.pullToRefreshError = copySource.getPullToRefreshError();
        }

        public Builder data(List<PayloadItem> data) {
            this.data = data;
            return this;
        }

        public Builder loadingDepartments(boolean loadingDepartments) {
            this.loadingDepartments = loadingDepartments;
            return this;
        }

        public Builder loadingDepartmentsError(Throwable loadingDepartmentsError) {
            this.loadingDepartmentsError = loadingDepartmentsError;
            return this;
        }


        public Builder loadingPullToRefresh(boolean loadingPullToRefresh) {
            this.loadingPullToRefresh = loadingPullToRefresh;
            return this;
        }

        public Builder pullToRefreshError(Throwable pullToRefreshError) {
            this.pullToRefreshError = pullToRefreshError;
            return this;
        }

        public DepartmentsViewState build() {
            return new DepartmentsViewState(loadingDepartments, loadingDepartmentsError, data, loadingPullToRefresh, pullToRefreshError);
        }
    }
}
