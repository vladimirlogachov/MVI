package com.vladimirlogachov.mvi;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

/**
 * Allows to bind intents to the view.
 * @param <V> the type of the view.
 * @param <I> the type of the intent.
 */
interface IntentBinder<V, I> {
    /**
     * Called each time, when view is attached to presenter.
     * @param view the view, which provides intents.
     * @return the provided intent.
     */
    @NonNull
    Observable<I> bind(@NonNull V view);
}
