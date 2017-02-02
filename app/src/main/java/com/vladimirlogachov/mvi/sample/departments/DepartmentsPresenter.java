package com.vladimirlogachov.mvi.sample.departments;

import android.support.annotation.NonNull;
import android.util.Log;

import com.vladimirlogachov.mvi.MviBasePresenter;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentsInteractor;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentsViewState;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class DepartmentsPresenter extends MviBasePresenter<DepartmentsView, DepartmentsViewState> {
    public static final String TAG = DepartmentsPresenter.class.getSimpleName();

    private final DepartmentsInteractor interactor;

    public DepartmentsPresenter(DepartmentsInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    protected Observable<DepartmentsViewState> provideViewStateObservable() {
        Log.d(TAG, "provideViewStateObservable: ");
        Observable<DepartmentsViewState> loadDepartmentsIntent = createIntentObservable(new ActionIntentBinder<DepartmentsView, Boolean>() {
                    @NonNull
                    @Override
                    public Observable<Boolean> bind(@NonNull DepartmentsView view) {
                        return view.loadDepartmentsIntent();
                    }
                })
                .switchMap(new Function<Boolean, ObservableSource<? extends DepartmentsViewState>>() {
                    @Override
                    public ObservableSource<? extends DepartmentsViewState> apply(Boolean aBoolean) throws Exception {
                        return interactor.loadDepartments();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        Observable<DepartmentsViewState> pullToRefreshIntent = createIntentObservable(new ActionIntentBinder<DepartmentsView, Boolean>() {
                    @NonNull
                    @Override
                    public Observable<Boolean> bind(@NonNull DepartmentsView view) {
                        return view.pullToRefreshIntent();
                    }
                })
                .switchMap(new Function<Boolean, ObservableSource<? extends DepartmentsViewState>>() {
                    @Override
                    public ObservableSource<? extends DepartmentsViewState> apply(Boolean aBoolean) throws Exception {
                        return interactor.refreshData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        Observable<DepartmentsViewState> allIntents = Observable.merge(loadDepartmentsIntent, pullToRefreshIntent);

        DepartmentsViewState initialState = DepartmentsViewState.LoadingDepartmentState();

        return allIntents.scan(initialState, createAccumulator());
    }

    @Override
    protected ViewStateConsumer<DepartmentsView, DepartmentsViewState> provideViewStateConsumer() {
        return new ViewStateConsumer<DepartmentsView, DepartmentsViewState>() {
            @Override
            public void apply(@NonNull DepartmentsView view, @NonNull DepartmentsViewState viewState) {
                view.render(viewState);
            }
        };
    }

    @Override
    protected void releaseData() {
        super.releaseData();
        Log.d(TAG, "releaseData: ");
    }

    private BiFunction<DepartmentsViewState, DepartmentsViewState, DepartmentsViewState> createAccumulator() {
        return new BiFunction<DepartmentsViewState, DepartmentsViewState, DepartmentsViewState>() {
            @Override
            public DepartmentsViewState apply(DepartmentsViewState previousState, DepartmentsViewState newState) throws Exception {
                DepartmentsViewState result = reduceViewState(previousState, newState);
                Log.d(TAG, "apply() returned: " + result);
                return result;
            }
        };
    }

    private DepartmentsViewState reduceViewState(DepartmentsViewState previousState, DepartmentsViewState newState) {
        Log.d(TAG, "reduceViewState() called with: previousState = [" + previousState + "], newState = [" + newState + "]");
        if (newState.isLoadingDepartmentsState())
            return previousState.builder()
                    .loadingDepartments(true)
                    .loadingDepartmentsError(null)
                    .build();
        else if (newState.isLoadingDepartmentsErrorState())
            return previousState.builder()
                    .loadingDepartments(false)
                    .loadingDepartmentsError(newState.getLoadingDepartmentsError())
                    .build();
        else if (newState.isLoadingPullToRefreshState())
            return previousState.builder()
                    .loadingPullToRefresh(true)
                    .pullToRefreshError(null)
                    .build();
        else if (newState.isPullToRefreshErrorState())
            return previousState.builder()
                    .loadingPullToRefresh(false)
                    .pullToRefreshError(newState.getPullToRefreshError())
                    .build();
        else if (newState.isResultState())
            return previousState.builder()
                    .loadingDepartments(false)
                    .loadingDepartmentsError(null)
                    .loadingPullToRefresh(false)
                    .pullToRefreshError(null)
                    .data(newState.getResult())
                    .build();
        else
            return previousState;
    }
}
