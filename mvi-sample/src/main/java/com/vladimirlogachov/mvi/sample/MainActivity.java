package com.vladimirlogachov.mvi.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vladimirlogachov.mvi.sample.departments.DepartmentsFragment;
import com.vladimirlogachov.mvi.sample.search.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null)
            setMainFragment();
    }

    private void setMainFragment() {
        setFragment(new DepartmentsFragment());
    }

    private void setFragment(Fragment fragment) {
        setFragment(fragment, false);
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    @OnClick(R.id.fab)
    void goToSearch() {
        setFragment(new SearchFragment(), true);
    }
}
