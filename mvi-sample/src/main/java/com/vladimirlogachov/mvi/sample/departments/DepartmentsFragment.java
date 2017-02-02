package com.vladimirlogachov.mvi.sample.departments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vladimirlogachov.mvi.sample.R;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentsViewState;
import com.vladimirlogachov.mvi.sample.departments.model.PayloadItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;


/**
 * A simple {@link Fragment} subclass.
 */
public class DepartmentsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<DepartmentsPresenter>, DepartmentsView {
    private final String TAG = this.toString();

    private static final int LOADER_ID = 0x20;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.departments)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.error_text)
    TextView errorText;

    private Unbinder unbinder;
    private DepartmentsAdapter adapter;
    private DepartmentsPresenter presenter;

    public DepartmentsFragment() {
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
        terminateRefreshing();
    }

    private void terminateRefreshing() {
        if (swipeRefreshLayout == null) return;

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.destroyDrawingCache();
        swipeRefreshLayout.clearAnimation();
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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return setupView(inflater.inflate(R.layout.fragment_departments, container, false));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private View setupView(View view) {
        unbinder = ButterKnife.bind(this, view);
        adapter = new DepartmentsAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public Observable<Boolean> loadDepartmentsIntent() {
        Log.d(TAG, "loadDepartmentsIntent: ");
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> pullToRefreshIntent() {
        Log.d(TAG, "pullToRefreshIntent: ");
        return Observable.create(createSwipeRefreshObservableOnSubscribe());
    }

    private ObservableOnSubscribe<Boolean> createSwipeRefreshObservableOnSubscribe() {
        return new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                if (e.isDisposed()) return;

                swipeRefreshLayout.setOnRefreshListener(createOnRefreshListener(e));
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        swipeRefreshLayout.setOnRefreshListener(null);
                    }
                });
            }
        };
    }

    private SwipeRefreshLayout.OnRefreshListener createOnRefreshListener(final ObservableEmitter<Boolean> e) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                e.onNext(true);
            }
        };
    }

    @Override
    public void render(DepartmentsViewState viewState) {
        Log.d(TAG, "render() called with: viewState = [" + viewState + "]");
        if (viewState.isLoadingDepartmentsState())
            loadingDepartmentsState();
        else if (viewState.isLoadingDepartmentsErrorState())
            loadingDepartmentsErrorState(viewState.getLoadingDepartmentsError());
        else if (viewState.isLoadingPullToRefreshState())
            pullToRefreshState(viewState.getResult());
        else if (viewState.isPullToRefreshErrorState())
            pullToRefreshErrorState(viewState.getPullToRefreshError());
        else if (viewState.isResultState())
            resultState(viewState.getResult());
    }

    private void resultState(List<PayloadItem> result) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        adapter.updateData(result);
    }

    private void pullToRefreshErrorState(Throwable pullToRefreshError) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        Toast.makeText(getContext(), pullToRefreshError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void pullToRefreshState(List<PayloadItem> result) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        adapter.updateData(result);
    }

    private void loadingDepartmentsErrorState(Throwable loadingDepartmentsError) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.GONE);
        errorText.setText(loadingDepartmentsError.getLocalizedMessage());
    }

    private void loadingDepartmentsState() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }

    @Override
    public Loader<DepartmentsPresenter> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        return new DepartmentsPresenterLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<DepartmentsPresenter> loader, DepartmentsPresenter data) {
        Log.d(TAG, "onLoadFinished: ");
        presenter = data;
    }

    @Override
    public void onLoaderReset(Loader<DepartmentsPresenter> loader) {
        Log.d(TAG, "onLoaderReset: ");
        presenter = null;
    }
}
