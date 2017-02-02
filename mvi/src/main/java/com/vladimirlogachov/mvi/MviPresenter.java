package com.vladimirlogachov.mvi;

/**
 * MVI presenter.
 * @param <V> the type of the MVI view.
 */
interface MviPresenter<V> {
    /**
     * Call each time, when it is needed to attach view to its presenter.
     * @param view the view to attach.
     */
    void onViewAttached(V view);

    /**
     * Call each time, when it is needed to detach view form presenter.
     */
    void onViewDetached();

    /**
     * Call each time presenter is destroyed.
     */
    void onDestroyed();
}
