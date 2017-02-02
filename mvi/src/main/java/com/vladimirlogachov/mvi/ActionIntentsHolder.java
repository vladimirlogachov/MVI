package com.vladimirlogachov.mvi;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Holds view action intents and allows to obtain them from the view.
 * @param <V> the type of the view.
 */

final class ActionIntentsHolder<V> {

    /**
     * Default capacity of the {@link #actionIntents} list.
     */
    private final static int DEFAULT_CAPACITY = 4;

    /**
     * Internal objects, which holds action intent intentSubject and {@link IntentBinder}.
     * @param <I> the type of the intent.
     */
    private final class ActionIntentBinderPair<I> {
        /**
         * Action intent.
         */
        private final PublishSubject<I> actionIntentSubject;

        /**
         * Intent binder
         */
        private final IntentBinder<V, I> intentBinder;

        ActionIntentBinderPair(PublishSubject<I> actionIntentSubject,
                               IntentBinder<V, I> intentBinder) {
            this.actionIntentSubject = actionIntentSubject;
            this.intentBinder = intentBinder;
        }
    }

    /**
     * Internal wrapper, which is used to save action intent,
     * during the changes of view attachment state.
     * @param <I> the type of the intent.
     */
    private final class DisposableIntentObserver<I> extends DisposableObserver<I> {

        /**
         * Holds action intent.
         */
        private final PublishSubject<I> intentSubject;

        DisposableIntentObserver(PublishSubject<I> intentSubject) {
            this.intentSubject = intentSubject;
        }

        @Override
        public void onNext(I i) {
            intentSubject.onNext(i);
        }

        @Override
        public void onError(Throwable e) {
            throw new IllegalStateException("MviView action intents must now throw errors.", e);
        }

        @Override
        public void onComplete() {
            intentSubject.onComplete();
        }
    }

    /**
     * List of {@link ActionIntentBinderPair}.
     */
    private List<ActionIntentBinderPair<?>> actionIntents;

    /**
     * Holds subscriptions to all view action intents.
     */
    private CompositeDisposable disposables;

    ActionIntentsHolder() {
        actionIntents = new ArrayList<>(DEFAULT_CAPACITY);
    }

    /**
     * Creates view action intent observable
     * and add it to {@link #actionIntents} list.
     * @param binder the {view action intent binder.
     * @return the observable, which emits view action intent.
     */
    <I> Observable<I> createActionIntentObservable(IntentBinder<V, I> binder) {
        PublishSubject<I> intentRelay = PublishSubject.create();
        actionIntents.add(new ActionIntentBinderPair<>(intentRelay, binder));
        return intentRelay;
    }

    /**
     * Binds all action intents to view.
     * @param view the view action intents bind to.
     */
    void bindActionIntents(V view) {
        createCompositeDisposableIfNeeded();

        for (ActionIntentBinderPair binderPair : actionIntents)
            disposables.add(bindActionIntent(view, binderPair));
    }

    /**
     * Initializes {@link CompositeDisposable} instance,
     * if it has not been initialized yet.
     */
    private void createCompositeDisposableIfNeeded() {
        if (disposables == null)
            disposables = new CompositeDisposable();
    }

    /**
     * Binds action intent to the view.
     * @param view the view, which bind intent to.
     * @param binderPair the object, which holds action intent and its binder.
     * @param <I> the type of the intent.
     * @return the observer, subscribed to the action intent.
     */
    private <I> Disposable bindActionIntent(V view, ActionIntentBinderPair<I> binderPair) {
        Observable<I> intent = binderPair.intentBinder.bind(view);
        return subscribeToIntent(intent, createObserverForSubject(binderPair.actionIntentSubject));
    }

    /**
     * Subscribes to action intent.
     * @param intent the action intent.
     * @param observer the observer
     * @param <I> the type of the intent.
     * @return the observer, subscribed to the action intent.
     */
    private <I> Disposable subscribeToIntent(@NonNull Observable<I> intent, @NonNull DisposableObserver<I> observer) {
        return intent.subscribeWith(observer);
    }

    /**
     * Creates action intent observer, which is actually a wrapper.
     * @param publishSubject the action intent subject.
     * @param <I> the type of the intent.
     * @return the action intent observer.
     */
    private <I> DisposableObserver<I> createObserverForSubject(PublishSubject<I> publishSubject) {
        return new DisposableIntentObserver<>(publishSubject);
    }

    /**
     * Unbinds all action intents.
     */
    void unbindActions() {
        if (disposables == null) return;

        disposables.dispose();
        disposables = null;
    }
}
