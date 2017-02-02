package com.vladimirlogachov.mvi.sample.search.model;

import android.util.Log;

import com.vladimirlogachov.mvi.sample.search.model.firebase.SearchEngine;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SearchInteractor {
    private static final String TAG = SearchInteractor.class.getSimpleName();

    private final SearchEngine searchEngine;

    public SearchInteractor(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public Observable<SearchViewState> search(final String searchQueryString) {
        Log.d(TAG, "search() called with: searchQueryString = [" + searchQueryString + "]");
        if (searchQueryString.isEmpty())
            return Observable.just(getSearchNotStartedState());
        else
            return searchEngine.searchFor(searchQueryString)
                .map(getMapper(searchQueryString))
                .startWith(SearchViewState.Loading())
                .onErrorReturn(getErrorMapper(searchQueryString));
    }

    private Function<List<User>, SearchViewState> getMapper(final String searchQueryString) {
        return new Function<List<User>, SearchViewState>() {
            @Override
            public SearchViewState apply(List<User> list) throws Exception {
                if (list.isEmpty())
                    return SearchViewState.EmptyResult(searchQueryString);

                return SearchViewState.SearchResult(searchQueryString, list);
            }
        };
    }

    private Function<Throwable, SearchViewState> getErrorMapper(final String searchQueryString) {
        return new Function<Throwable, SearchViewState>() {
            @Override
            public SearchViewState apply(Throwable throwable) throws Exception {
                return SearchViewState.Error(searchQueryString, throwable);
            }
        };
    }

    private SearchViewState getSearchNotStartedState() {
        return SearchViewState.SearchNotStartedYet();
    }
}
