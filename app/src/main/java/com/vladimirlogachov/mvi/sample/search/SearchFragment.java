package com.vladimirlogachov.mvi.sample.search;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimirlogachov.mvi.sample.R;
import com.vladimirlogachov.mvi.sample.search.model.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Predicate;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<SearchPresenter>, com.vladimirlogachov.mvi.sample.search.SearchView {
    public final String TAG = this.toString();

    private static final int LOADER_ID = 0x10;

    @BindView(R.id.sv_search)
    SearchView searchView;
    @BindView(R.id.rv_people_list)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    View progress;
    @BindView(R.id.empty_text)
    TextView textView;

    private Unbinder unbinder;
    private SearchPresenter presenter;
    private SearchResultsAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: presenter - " + presenter);
        presenter.onViewAttached(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onViewDetached();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return setupView(inflater.inflate(R.layout.fragment_people_search, container, false));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    public Observable<String> searchIntent() {
        Log.d(TAG, "searchIntent: ");
        return Observable.create(createTextChangeObservableOnSubscribe())
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() >= 2 || s.isEmpty();
                    }
                });
    }

    @Override
    public void resultState(List<User> result) {
        progress.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.updateData(result);
    }

    @Override
    public void errorState(Throwable error) {
        progress.setVisibility(View.GONE);
        textView.setText(error.getLocalizedMessage());
        textView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void emptyState() {
        progress.setVisibility(View.GONE);
        textView.setText("No matches found.");
        textView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
    @Override
    public void defaultState() {
        progress.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void loadingState() {
        progress.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private View setupView(View view) {
        unbinder = ButterKnife.bind(this, view);
        adapter = new SearchResultsAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    private ObservableOnSubscribe<String> createTextChangeObservableOnSubscribe() {
        return new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (e.isDisposed()) return;

                searchView.setOnQueryTextListener(createQueryTextListener(e));
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        searchView.setOnQueryTextListener(null);
                    }
                });
            }
        };
    }

    private SearchView.OnQueryTextListener createQueryTextListener(final ObservableEmitter<String> e) {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                e.onNext(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                e.onNext(newText);
                return true;
            }
        };
    }

    @Override
    public Loader<SearchPresenter> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        return new SearchPresenterLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<SearchPresenter> loader, SearchPresenter data) {
        Log.d(TAG, "onLoadFinished: ");
        this.presenter = data;
    }

    @Override
    public void onLoaderReset(Loader<SearchPresenter> loader) {
        Log.d(TAG, "onLoaderReset: ");
        presenter = null;
    }
}
