package com.vladimirlogachov.mvi.sample.search;

import android.content.Context;

import com.vladimirlogachov.mvi.MviPresenterLoader;
import com.vladimirlogachov.mvi.sample.search.model.SearchInteractor;
import com.vladimirlogachov.mvi.sample.search.model.firebase.SearchEngine;

class SearchPresenterLoader extends MviPresenterLoader<SearchPresenter> {
    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    SearchPresenterLoader(Context context) {
        super(context);
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(new SearchInteractor(new SearchEngine()));
    }
}
