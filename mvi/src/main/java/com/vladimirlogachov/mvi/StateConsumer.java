package com.vladimirlogachov.mvi;

import android.support.annotation.NonNull;

/**
 * Allows to apply last emitted view state to the view.
 * @param <V> the type of the view.
 * @param <VS> the type of the view state.
 */
interface StateConsumer<V, VS> {
    /**
     * Called to apply last emitted view state to the view.
     * @param view the view to apply a view state.
     * @param viewState the view state to apply.
     */
    void apply(@NonNull V view, @NonNull VS viewState);
}