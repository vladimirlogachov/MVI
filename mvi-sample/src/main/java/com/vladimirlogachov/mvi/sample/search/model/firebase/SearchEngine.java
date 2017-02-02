package com.vladimirlogachov.mvi.sample.search.model.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vladimirlogachov.mvi.sample.search.model.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Predicate;

public class SearchEngine {
    private final DatabaseReference usersRef;

    public SearchEngine() {
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public Observable<List<User>> searchFor(final String searchQuery) {
        return createPeopleSearchObservable()
                .filter(new Predicate<User>() {
                    @Override
                    public boolean test(User user) throws Exception {
                        return user.getName().toLowerCase().contains(searchQuery);
                    }
                })
                .delay(2, TimeUnit.SECONDS)
                .toList()
                .toObservable();
    }

    private Observable<User> createPeopleSearchObservable() {
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(final ObservableEmitter<User> e) throws Exception {
                if (e.isDisposed()) return;

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren())
                            e.onNext(user.getValue(User.class));

                        e.onComplete();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        e.onError(databaseError.toException());
                    }
                });
            }
        });
    }
}
