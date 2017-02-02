package com.vladimirlogachov.mvi.sample.departments.model.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vladimirlogachov.mvi.sample.departments.model.Department;
import com.vladimirlogachov.mvi.sample.departments.model.DepartmentEmployee;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class DepartmentsLoadEngine {
    private DatabaseReference departmentsRef;
    private DatabaseReference employeesRef;

    public DepartmentsLoadEngine() {
        departmentsRef = FirebaseDatabase.getInstance().getReference("departments");
        employeesRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public Observable<List<Department>> loadDepartments() {
        return createDepartmentsObservable()
                .delay(2, TimeUnit.SECONDS)
                .toList()
                .toObservable();
    }

    private Observable<Department> createDepartmentsObservable() {
        return Observable.create(new ObservableOnSubscribe<Department>() {
            @Override
            public void subscribe(final ObservableEmitter<Department> e) throws Exception {
                if (e.isDisposed()) return;

                departmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (Department department : parseDepartmentsResult(dataSnapshot))
                            e.onNext(department);

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

    private Set<Department> parseDepartmentsResult(DataSnapshot dataSnapshot) {
        Set<Department> result = new HashSet<>();

        for (DataSnapshot node : dataSnapshot.getChildren())
            result.add(extractDepartment(node));

        return result;
    }

    private Department extractDepartment(DataSnapshot node) {
        Department department = node.getValue(Department.class);
        department.setId(node.getKey());
        return department;
    }

    public Observable<List<DepartmentEmployee>> getDepartmentEmployees() {
        return createEmployeesObservable()
                .toList()
                .toObservable();
    }

    private Observable<DepartmentEmployee> createEmployeesObservable() {
        return Observable.create(new ObservableOnSubscribe<DepartmentEmployee>() {
            @Override
            public void subscribe(final ObservableEmitter<DepartmentEmployee> e) throws Exception {
                if (e.isDisposed()) return;

                employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DepartmentEmployee employee : parseEmployeesResult(dataSnapshot))
                                    e.onNext(employee);

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

    private Set<DepartmentEmployee> parseEmployeesResult(DataSnapshot dataSnapshot) {
        Set<DepartmentEmployee> result = new HashSet<>();

        for (DataSnapshot node : dataSnapshot.getChildren())
            result.add(extractEmployee(node));

        return result;
    }

    private DepartmentEmployee extractEmployee(DataSnapshot node) {
        return node.getValue(DepartmentEmployee.class);
    }
}
