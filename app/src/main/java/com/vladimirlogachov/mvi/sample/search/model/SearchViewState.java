package com.vladimirlogachov.mvi.sample.search.model;

import java.util.List;

public class SearchViewState {

    private final boolean loading;
    private final boolean searchNotStartedYet;
    private final List<User> result;
    private final Throwable error;
    private final String searchQueryText;

    private SearchViewState(boolean loading, boolean searchNotStartedYet, List<User> result, Throwable error, String searchQueryText) {
        this.loading = loading;
        this.searchNotStartedYet = searchNotStartedYet;
        this.result = result;
        this.error = error;
        this.searchQueryText = searchQueryText;
    }

    public static SearchViewState Loading() {
        return new SearchViewState(true, false, null, null, null);
    }

    public static SearchViewState SearchNotStartedYet() {
        return new SearchViewState(false, true, null, null, null);
    }

    public static SearchViewState EmptyResult(String searchQueryText) {
        return new SearchViewState(false, false, null, null, searchQueryText);
    }

    public static SearchViewState SearchResult(String searchQueryText, List<User> result) {
        return new SearchViewState(false, false, result, null, searchQueryText);
    }

    public static SearchViewState Error(String searchQueryText, Throwable error) {
        return new SearchViewState(false, false, null, error, searchQueryText);
    }

    public boolean isLoadingState() {
        return loading;
    }

    public boolean isErrorState() {
        return error != null;
    }

    public boolean isEmptyState() {
        return result == null;
    }

    public boolean isDefaultState() {
        return searchNotStartedYet;
    }

    public boolean isResultState() {
        return result != null;
    }

    public List<User> getResult() {
        return result;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "SearchViewState{" +
                "loading=" + loading +
                ", searchNotStartedYet=" + searchNotStartedYet +
                ", result=" + result +
                ", error=" + error +
                ", searchQueryText='" + searchQueryText + '\'' +
                '}';
    }
}
