package com.vladimirlogachov.mvi;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


public abstract class MviBasePresenter<V, VS> implements MviPresenter<V>, StateConsumer<V, VS> {

    /**
     * Binds a single action intent to view.
     */
    protected interface ActionIntentBinder<V, I> extends IntentBinder<V, I> {
    }

    /**
     * Holds action intents and allows to obtain them form the view.
     */
    private ActionIntentsHolder<V> actionIntentsHolder = new ActionIntentsHolder<>();

    /**
     * Holds subscriptions to the view and its state and allows to manage them.
     */
    private ViewStateSubscriber<V, VS> viewStateSubscriber;

    /**
     * Indicator of the {@link MviPresenter#onViewAttached(Object)} method call.
     */
    private boolean isFirstAttachment = true;

    public MviBasePresenter() {
        viewStateSubscriber = new ViewStateSubscriber<>(BehaviorSubject.<VS>create());
    }

    public MviBasePresenter(@NonNull VS viewState) {
        viewStateSubscriber = new ViewStateSubscriber<>(BehaviorSubject.createDefault(viewState));
    }

    /**
     * Called once to provide view state observable.
     * <br>
     * To construct view state observable, you <b>must</b> use {@link #createIntentObservable(ActionIntentBinder)}.
     <pre>
     {@code

     protected Observable<ViewState> provideViewStateObservable() {
        Observable<String> searchIntent = createIntentObservable(new IntentBinder<SearchView, String>() {
            public Observable<String> bind(SearchView view) {
                return view.searchIntent();
            }
        });

        return searchIntent.switchMap(query -> interactor.searchFor(query))
                        .observeOn(AndroidSchedulers.mainThread();
     }
     }
     </pre>
     * @return the view state observable.
     */
    protected abstract Observable<VS> provideViewStateObservable();

    /**
     * Called once to clean data, when presenter will be destroyed.
     */
    @CallSuper
    protected void releaseData() {
        actionIntentsHolder = null;
        viewStateSubscriber = null;
    }

    @Override
    public void onViewAttached(V view) {
        if (isFirstAttachment)
            subscribeToViewStateChanges(provideViewStateObservable());

        actionIntentsHolder.bindActionIntents(view);
        viewStateSubscriber.subscribeToView(view);

        isFirstAttachment = false;
    }

    @Override
    public void onViewDetached() {
        actionIntentsHolder.unbindActions();
        viewStateSubscriber.cancelViewSubscription();
    }

    @Override
    public void onDestroyed() {
        viewStateSubscriber.cancelViewStateChangesSubscription();
        releaseData();
    }

    /**
     * Creates action intent observable.
     * @param binder the action intent binder.
     * @param <I> the type of the action intent.
     * @return the action intent observable.
     */
    protected final <I> Observable<I> createIntentObservable(ActionIntentBinder<V, I> binder) {
        return actionIntentsHolder.createActionIntentObservable(binder);
    }

    /**
     * Subscribes to the view state changes.
     * @param viewStateObservable the view state observable.
     */
    private void subscribeToViewStateChanges(@NonNull Observable<VS> viewStateObservable) {
        viewStateSubscriber.subscribeToViewStateChanges(viewStateObservable, this);
    }
}
