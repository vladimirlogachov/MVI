package com.vladimirlogachov.mvi;


import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Allows to manage subscriptions to the view and its state.
 * @param <V> the type of the view.
 * @param <VS> the type of the view state.
 */
final class ViewStateSubscriber<V, VS> {

    /**
     * Internal wrapper, which is used to save last emitted view state,
     * during the changes of view attachment state.
     */
    private final class DisposableViewStateObserver extends DisposableObserver<VS> {

        /**
         * Holds last emitted view state.
         */
        private final BehaviorSubject<VS> viewStateSubject;

        DisposableViewStateObserver(BehaviorSubject<VS> viewStateSubject) {
            this.viewStateSubject = viewStateSubject;
        }

        @Override
        public void onNext(VS vs) {
            viewStateSubject.onNext(vs);
        }

        @Override
        public void onError(Throwable e) {
            throw new IllegalStateException(
                    "MviViewState observable must not reach error state - onError()", e);
        }

        @Override
        public void onComplete() {
            // never reached
        }
    }

    /**
     * Last emitted view state.
     * Whenever the view is attached to view state,
     * latest value will be emitted again.
     */
    private final BehaviorSubject<VS> viewState;

    /**
     * State consumer, which applies view state to the attached view.
     */
    private StateConsumer<V, VS> viewStateConsumer;

    /**
     * Subscription to the view state changes.
     */
    private Disposable viewStateDisposable;

    /**
     * Subscription to the view.
     */
    private Disposable consumerDisposable;

    ViewStateSubscriber(BehaviorSubject<VS> viewStateSubject) {
        this.viewState = viewStateSubject;
    }

    /**
     * Subscribes to the view state changes.
     * @param viewStateObservable the view state observable.
     * @param viewStateConsumer the state consumer, which applies new state to the view.
     */
    void subscribeToViewStateChanges(Observable<VS> viewStateObservable, StateConsumer<V, VS> viewStateConsumer) {
        this.viewStateConsumer = viewStateConsumer;
        viewStateDisposable = viewStateObservable.subscribeWith(new DisposableViewStateObserver(viewState));
    }

    /**
     * Cancels views state changes subscription.
     */
    void cancelViewStateChangesSubscription() {
        if (viewStateDisposable == null) return;

        viewStateDisposable.dispose();
        viewStateDisposable = null;
    }

    /**
     * Subscribes to the view.
     * @param view the view to subscribe.
     */
    void subscribeToView(@NonNull final V view) {
        consumerDisposable = viewState.subscribe(new Consumer<VS>() {
            @Override
            public void accept(VS vs) throws Exception {
                viewStateConsumer.apply(view, vs);
            }
        });
    }

    /**
     * Cancels view subscription.
     */
    void cancelViewSubscription() {
        if (consumerDisposable == null) return;

        consumerDisposable.dispose();
        consumerDisposable = null;
    }

}
