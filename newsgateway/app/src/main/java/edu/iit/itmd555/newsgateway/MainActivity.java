package edu.iit.itmd555.newsgateway;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String SERVICE_BROADCAST = "service";
    public static final String NEWS_BROADCAST = "news";
    private GoogleSignInClient mGoogleSignInClient;


    private ArrayList<String> sources = new ArrayList<>();
    private List<Category> categories;
    private Menu mainMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private String currentSource;
    private ServiceReceiver serviceReceiver;
    private NewsReceiver newsReceiver;
    private String[] colors = {"#E53935", "#4527A0", "#039BE5", "#43A047", "#FFEB3B", "#F4511E", "#607D8B", "#795548", "#AEEA00", "#004D40"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        Intent intent = new Intent(this, NewsService.class);
        startService(intent);

        serviceReceiver = new ServiceReceiver(this);
        IntentFilter filter1 = new IntentFilter(SERVICE_BROADCAST);
        registerReceiver(serviceReceiver, filter1);

        newsReceiver = new NewsReceiver(this);
        IntentFilter filter2 = new IntentFilter(NEWS_BROADCAST);
        registerReceiver(newsReceiver, filter2);

        categories = new ArrayList<>();
        AsyncSourceLoader asyncSourceLoader = new AsyncSourceLoader(this);
        asyncSourceLoader.execute();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(serviceReceiver);
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

    private void selectItem(int position) {

        pager.setBackground(null);
        currentSource = sources.get(position);

        Intent intent = new Intent();
        intent.setAction(SERVICE_BROADCAST);

        boolean found = false;
        for (Category category : categories) {
            if(found)
                break;
            for(Source source : category.getSources()) {
                if(source.getName().equals(currentSource)) {
                    intent.putExtra("source", source.getId());
                    found = true;
                    break;
                }
            }
        }
        sendBroadcast(intent);

        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void updateData(ArrayList<Category> newCategories) {
        categories.clear();
        sources.clear();
        categories.addAll(newCategories);
        mainMenu.add("all");
        for (Category category : this.categories) {
            mainMenu.add(category.getCategory());
            for (Source source : category.getSources()) {
                sources.add(source.getName());
            }
        }
        mainMenu.add("Logout");

        Collections.sort(sources);

        try {
            for (int i = 1; i > 0; i++) {
                MenuItem item = mainMenu.getItem(i);
                String c = item.getTitle().toString();
                SpannableString s = new SpannableString(c);
                s.setSpan(new ForegroundColorSpan(Color.parseColor(colors[i - 1])), 0, s.length(), 0);
                item.setTitle(s);
                for (Category category : categories) {
                    if(category.getCategory().equals(c)) {
                        category.setColor(colors[i - 1]);
                        break;
                    }
                }
            }
        } catch(IndexOutOfBoundsException a) {

        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, R.layout.drawer_item, sources){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                String source = ((TextView)view).getText().toString();
                for (Category category : categories) {
                    for (Source s : category.getSources()) {
                        if(s.getName().equals(source)) {
                            ((TextView)view).setTextColor(Color.parseColor(category.getColor()));
                            break;
                        }
                    }
                }
                return view;
            };
        };
        mDrawerList.setAdapter(itemsAdapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void setArticles(List<Article> articleList) {

        setTitle(currentSource);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articleList.size(); i++) {
            fragments.add(ArticleFragment.newInstance(articleList.get(i), i+1, articleList.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        String categorySelected = item.getTitle().toString();
        if(categorySelected.equals("all")) {
            sources.clear();
            for (Category category : categories) {
                for (Source source : category.getSources()) {
                    sources.add(source.getName());
                }
            }
            Collections.sort(sources);
            ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        }
        else if (categorySelected.equals("Logout"))
        {
            signOut();
        }


        else {
            for (Category category : categories) {
                if(category.getCategory().equals(categorySelected)) {
                    sources.clear();
                    for (Source source : category.getSources()) {
                        sources.add(source.getName());
                    }
                    ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
                    break;
                }
            }
        }
        return  true;
    }

    private void signOut() {

        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        private long baseId = 0;

        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

    }

}
