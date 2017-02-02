package com.vladimirlogachov.mvi.sample.search;

import com.vladimirlogachov.mvi.sample.search.model.User;

import java.util.List;

import io.reactivex.Observable;


public interface SearchView {
    Observable<String> searchIntent();
    void defaultState();
    void loadingState();
    void errorState(Throwable error);
    void emptyState();
    void resultState(List<User> result);
}
