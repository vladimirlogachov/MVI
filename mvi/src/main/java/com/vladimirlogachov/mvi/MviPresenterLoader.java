package com.vladimirlogachov.mvi;

import android.content.Context;
import android.support.v4.content.Loader;

/**
 * Store and loads valid {@link MviPresenter} instance.
 * Helps to avoid destroying of presenter during configuration change events and
 * save presenter instance when you work with back stack.
 * @param <P> the type of {@link MviPresenter} instance.
 */
public abstract class MviPresenterLoader<P extends MviPresenter> extends Loader<P> {

    /**
     * Presenter instance.
     */
    private P presenter;

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
    public MviPresenterLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (presenter != null)
            deliverResult(presenter);
        else
            forceLoad();
    }

    @Override
    protected void onForceLoad() {
        presenter = createPresenter();
        deliverResult(presenter);
    }

    @Override
    protected void onReset() {
        presenter.onDestroyed();
        presenter = null;
    }

    /**
     * Creates presenter instance to load.
     * @return the presenter instance.
     */
    protected abstract P createPresenter();
}
