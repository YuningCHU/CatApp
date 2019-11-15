package app.com.catapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BreadListFragment.OnListFragmentInteractionListener {
    BreadListFragment fragment;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init bottom menu
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //add fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new BreadListFragment();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    //handle list item click event
    @Override
    public void onListFragmentInteraction(Breed item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("breed", item);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update favorites list
        if(navigation.getSelectedItemId() == R.id.navigation_fav){
            fragment.doFavorite();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    fragment.doSearch();
                    return true;
                case R.id.navigation_fav:
                    fragment.doFavorite();
                    return true;
            }
            return false;
        }
    };

}
