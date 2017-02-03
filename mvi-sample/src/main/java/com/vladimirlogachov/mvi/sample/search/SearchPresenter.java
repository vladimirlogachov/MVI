package com.vladimirlogachov.mvi.sample.search;

import android.support.annotation.NonNull;

import com.vladimirlogachov.mvi.MviBasePresenter;
import com.vladimirlogachov.mvi.sample.search.model.SearchInteractor;
import com.vladimirlogachov.mvi.sample.search.model.SearchViewState;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;


class SearchPresenter extends MviBasePresenter<SearchView, SearchViewState> {
    private static final String TAG = SearchPresenter.class.getSimpleName();

    private final SearchInteractor interactor;

    SearchPresenter(SearchInteractor interactor) {
        super(SearchViewState.SearchNotStartedYet());
        this.interactor = interactor;
    }

    @Override
    protected Observable<SearchViewState> provideViewStateObservable() {

        return createIntentObservable(new ActionIntentBinder<SearchView, String>() {
                    @NonNull
                    @Override
                    public Observable<String> bind(@NonNull SearchView view) {
                        return view.searchIntent();
                    }
                })
                .switchMap(new Function<String, ObservableSource<? extends SearchViewState>>() {
                    @Override
                    public ObservableSource<? extends SearchViewState> apply(String s) throws Exception {
                        return interactor.search(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void releaseData() {
        super.releaseData();
    }

    @Override
    public void apply(@NonNull SearchView view, @NonNull SearchViewState viewState) {
        if (viewState.isDefaultState())
            view.defaultState();
        else if (viewState.isLoadingState())
            view.loadingState();
        else if (viewState.isErrorState())
            view.errorState(viewState.getError());
        else if (viewState.isEmptyState())
            view.emptyState();
        else if (viewState.isResultState())
            view.resultState(viewState.getResult());
    }
}
