package com.vladimirlogachov.mvi.sample.departments;

import android.content.Context;

import com.vladimirlogachov.mvi.MviPresenterLoader;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentsInteractor;
import com.vladimirlogachov.mvi.sample.departments.model.firebase.DepartmentsLoadEngine;

public class DepartmentsPresenterLoader extends MviPresenterLoader<DepartmentsPresenter> {

    public DepartmentsPresenterLoader(Context context) {
        super(context);
    }

    @Override
    protected DepartmentsPresenter createPresenter() {
        return new DepartmentsPresenter(new DepartmentsInteractor(new DepartmentsLoadEngine()));
    }
}
