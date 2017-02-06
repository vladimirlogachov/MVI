# Model-View-Intent [ ![Download](https://api.bintray.com/packages/viptakeiteasy/maven/MVI/images/download.svg) ](https://bintray.com/viptakeiteasy/maven/MVI/_latestVersion)
Library which allows to apply MVI architecture in your apps.

## Gratitude
Thanks to [Hannes Dorfmann's articles](http://hannesdorfmann.com/android/mosby3-mvi-1), I was inspired to implement my own library which allows you to crate reactive apps using RxJava and MVI architectural pattern.

## Concept
Comming soon...

## Integration

###Maven:
```xml
<dependency>
  <groupId>com.github.vladimirlogachov</groupId>
  <artifactId>mvi</artifactId>
  <version>0.1.1</version>
  <type>pom</type>
</dependency>
```

###Gradle:
```gradle
compile 'com.github.vladimirlogachov:mvi:0.1.1'
```

## How to use?

### View

There are could be several ways how to implement it. I'll show you only two of possible ways.

##### Single method to render ViewState:
```Java
interface MyView {
 Observable<Boolean> loadDataIntent();
 void render(MyViewState viewState);
}
```

There are only two methods. loadDataIntent() provides intent to presenter and render(), which is called to display current state. If you don't like such implementation of View interface, you may be interested in the next one.

##### Multiple methods to render ViewState:
```Java
interface MyView {
  Observable<String> searchIntent();
  void loadingState();
  void errorState();
  void emptyState();
  void resultState(List<Node> matches);
  // and so on, as many as you wish...
}
```

By the way, there is could be more than one method to provide intent to presenter.

### ViewState

There is could be many ways to implement it. All you need is to remember, that it **must** represent exactly the current state of a view. So let's look at several examples.

```Java
class MyViewState {
  private final boolean loading;
  private final Throwable error;
  private final List<Node> data = new ArrayList<>();
  
  MyViewState(boolean loading, Throwable error, List<Node> data) {
    this.loading = loading;
    this.error = error;
    this.data.clear();
    this.data.addAll(data);
  }
  
  boolean isLoading() {
    return loading;
  }
  
  boolean isError() {
    return error != null; 
  }
  
  Throwable getError() {
    return error;
  }
  
  boolean isEmpty() {
    return data.isEmpty();
  }
  
  List<Node> getData() {
    return data;
  }
} 
```
Feel free to use Builder or Fabric pattern in such case.

Another way to implement ViewState is using of an interface.
```Java
interface ViewState {

}

final class LoadingState extends ViewState {

}

final class EmptyState extends ViewState {

}

final class ErrorState implements ViewState {
  private final Throwable error;
  
  ErrorState(Throwable error) {
    this.error = error;
  }
  
  Throwable getError() {
    return error;
  }
}

final class ResultState implements ViewState {
  private final List<Node> data;
  
  ResultState(List<Node> data) {
    this.data = data;
  }
  
  List<Node> getData() {
    return data;
  }
}
```
You may choose any approach you wish.

### Interactor

I use such pattern, but as Hannes Dorfmann's says, it could be an Interactor, Usecase, Repository - whatever you use in your app.

```Java
class SearchInteractor {
  private final Api api;
  
  SearchInteractor(Api api) {
    this.api = api;
  }
  
  Observable<SearchViewState> searchFor(final String query) {
    if (query.isEmpty())
      return Observable.just(SearchViewState.SearchNotStartedYet())
    else
      return api.searchFor(query)
        .map(new Function<List<User>, SearchViewState>() {
            @Override
            public SearchViewState apply(List<User> list) throws Exception {
                if (list.isEmpty())
                    return SearchViewState.EmptyResult(query);

                return SearchViewState.SearchResult(query, list);
            }
        })
        .startWith(SearchViewState.Loading())
        .onErrorReturn(new Function<Throwable, SearchViewState>() {
            @Override
            public SearchViewState apply(Throwable throwable) throws Exception {
                return SearchViewState.Error(searchQueryString, throwable);
            }
        });
  }
}
```

Basically interactor intended for mapping each step of data flow into a ViewState and its delivering to a presenter.

All you saw before actually are not the part of this library, it only shows you the approach how to provide intents and use ViewState to display steps of data flow. Their implementation depends on your personal preferences, so feel free to experiment.

Following sections are main core of this library.

### Presenter

The main hero, which couples previous parts together and responsible to provide interactor with intents and deliver last emitted ViewState to its View.
To implement presenter you must extend abstract **MviBasePresenter** class and implement two methods: provideViewStateObservable() and apply().

```Java
class SearchPresenter extends MviBasePresenter<SearchView, SearchViewState> {

private final SearchInteractor interactor;

    SearchPresenter(SearchInteractor interactor) {
        super(SearchViewState.SearchNotStartedYet()); // Call parent constructor to set default ViewState, if you have such.
        this.interactor = interactor;
    }

    @Override
    protected Observable<SearchViewState> provideViewStateObservable() {
      Observable<String> searchIntent 
               = createIntentObservable(new ActionIntentBinder<SearchView, String>() {
                    @NonNull
                    @Override
                    public Observable<String> bind(@NonNull SearchView view) {
                        return view.searchIntent(); // Provide search action intent.
                    }
                });
    
      return searchIntent.switchMap(new Function<String, ObservableSource<? extends SearchViewState>>() {
                    @Override
                    public ObservableSource<? extends SearchViewState> apply(String s) throws Exception {
                        return interactor.search(s); // Starts searching each time, when query string is changed.
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
    
    @Override
    protected void releaseData() {
        super.releaseData();
        // Release presenter data, if you need.
    }

    @Override
    public void apply(@NonNull SearchView view, @NonNull SearchViewState viewState) {
        // Apply new ViewState to its View.
        if (viewState.isDefaultState())
            view.defaultState();
        else if (viewState.isLoadingState())
            view.loadingState();
        else if (viewState.isErrorState())
            view.errorState(viewState.getError());
        else if (viewState.isEmptyState())
            view.emptyState();
        else if (viewState.isResultState())
            view.resultState(viewState.getResult());
    }
}
```
What's going on here? 

Within **provideViewStateObservable()** implementation we create search intent using **MviBasePresenters**'s **createIntentObservable(ActionIntentBinder)** method and then map it into ViewState observable. **ActionIntentBinder** is an interface, which responsible for binding intents to their View. Remember, each time you create intent you **must** use **createIntentObservable(ActionIntentBinder)** method to be sure, that created intents will be saved and re-binded after changes of the View attachment state during Activity/Fragment lifecycle events including config changes.

Override **releaseData()** method, if you need to release data you use in presenter.

Both **provideViewStateObservable()** and **releaseData()** are called once. The first one, after first attachment of a View to its presenter. And the second one, when presenter is to be destroyed.

Use **apply(View, ViewState)** method to apply a ViewState to its View.

Next example shows you case, when you have more than one action intent.

```Java
...
@Override
    protected Observable<DepartmentsViewState> provideViewStateObservable() {
        Observable<DepartmentsViewState> loadDepartmentsIntent 
                = createIntentObservable(new ActionIntentBinder<DepartmentsView, Boolean>() {
                    @NonNull
                    @Override
                    public Observable<Boolean> bind(@NonNull DepartmentsView view) {
                        return view.loadDepartmentsIntent();
                    }
                })
                .switchMap(new Function<Boolean, ObservableSource<? extends DepartmentsViewState>>() {
                    @Override
                    public ObservableSource<? extends DepartmentsViewState> apply(Boolean aBoolean) throws Exception {
                        return interactor.loadDepartments();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        Observable<DepartmentsViewState> pullToRefreshIntent 
                = createIntentObservable(new ActionIntentBinder<DepartmentsView, Boolean>() {
                    @NonNull
                    @Override
                    public Observable<Boolean> bind(@NonNull DepartmentsView view) {
                        return view.pullToRefreshIntent();
                    }
                })
                .switchMap(new Function<Boolean, ObservableSource<? extends DepartmentsViewState>>() {
                    @Override
                    public ObservableSource<? extends DepartmentsViewState> apply(Boolean aBoolean) throws Exception {
                        return interactor.refreshData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        Observable<DepartmentsViewState> allIntents = Observable.merge(loadDepartmentsIntent, pullToRefreshIntent);

        DepartmentsViewState initialState = DepartmentsViewState.LoadingDepartmentState();

        return allIntents.scan(initialState, new BiFunction<DepartmentsViewState, DepartmentsViewState, DepartmentsViewState>() {
            @Override
            public DepartmentsViewState apply(DepartmentsViewState previousState, DepartmentsViewState newState) throws Exception {
                return reduceViewState(previousState, newState);
            }
        });
    }
...
```

Here we've got two action intents, which are merged into single ViewState observable. In order two reduce ViewState we use **scan(InitValue, Accumulator)** method. Remember to use **createIntentObservable(ActionIntentBinder)** each time, you need to add an intent. [Detailed example](https://github.com/vladimirlogachov/MVI/blob/master/mvi-sample/src/main/java/com/vladimirlogachov/mvi/sample/departments/DepartmentsPresenter.java). 

So how will our Activity/Fragment looks like?

```Java

public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<SearchPresenter>, com.vladimirlogachov.mvi.sample.search.SearchView {
        
    ...    
        
    private SearchPresenter presenter;

    ...

    @Override
    public void onStart() {
        super.onStart();
        presenter.onViewAttached(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onViewDetached();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroyed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Actually there is should be a kind of DI in real app.
        presenter = new SearchPresenter(new SearchInteractor(new SearchEngine())); 
    }
    
    ...

    @Override
    public Observable<String> searchIntent() {
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
      // ops
    }

    @Override
    public void errorState(Throwable error) {
      // ops
    }

    @Override
    public void emptyState() {
        // ops
    }
    @Override
    public void defaultState() {
        // ops
    }

    @Override
    public void loadingState() {
       // ops
    }

    ...
}
```
[Detailed example](https://github.com/vladimirlogachov/MVI/blob/master/mvi-sample/src/main/java/com/vladimirlogachov/mvi/sample/search/SearchFragment.java).

There is one more thing. Presenter implements **MviPresenter** interface methods: **onViewAttached(View)**, **onViewDetached()** and **onDestroyed()**, so you need to call them manually. Think you know the right place for this, don't you?

### PresenterLoader (optional)
Use it in order to save presenter instance, during Activity/Fragment instance recreation after configuration changes.
To create one, you must extend **MviPresenterLoader** abstract class.

```Java
class SearchPresenterLoader extends MviPresenterLoader<SearchPresenter> {
    
    SearchPresenterLoader(Context context) {
        super(context);
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(new SearchInteractor(new SearchEngine()));
    }
}
```

There is one abstract method **createPresenter()**, which provides presenter instance to the loader.

Then in your Activity/Fragment initialize the loader.

```Java
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<SearchPresenter>, MySearchView {

    private static final int LOADER_ID = 0x10;
    
    private SearchPresenter;
    
    ...
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    
    @Override
    public Loader<SearchPresenter> onCreateLoader(int id, Bundle args) {
        return new SearchPresenterLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<SearchPresenter> loader, SearchPresenter data) {
        this.presenter = data;
    }

    @Override
    public void onLoaderReset(Loader<SearchPresenter> loader) {
        presenter = null;
    }
    
    ....
}
```
Now you will always have valid presenter instance.

#### Note:
There are several things you should remember about Loaders:
  - Google recommends to call **initLoader(int, Bundle, LoaderCallbacks)** in **Activity#onCreate()** or **Fragment#onActivityCreated()**;
  - In Activities, after calling **super.onStart** the Presenter will be ready to use in every circumstance. However, in Fragments when first created, the Presenter will be deliver after **super.onStart**, but on recreation it will be delivered after **super.onResume**. So, on Fragments we can just rely that our Presenter will be there after **super.onResume**.
