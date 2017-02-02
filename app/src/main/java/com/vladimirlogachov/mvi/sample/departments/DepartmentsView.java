package com.vladimirlogachov.mvi.sample.departments;

import com.vladimirlogachov.mvi.sample.departments.model.DepartmentsViewState;
import io.reactivex.Observable;

public interface DepartmentsView {
    Observable<Boolean> loadDepartmentsIntent();
    Observable<Boolean> pullToRefreshIntent();
    void render(DepartmentsViewState viewState);
}
