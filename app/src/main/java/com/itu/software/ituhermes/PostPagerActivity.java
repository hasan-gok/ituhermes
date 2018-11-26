package com.itu.software.ituhermes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itu.software.ituhermes.Fragments.PostDialogFragment;
import com.itu.software.ituhermes.Fragments.PostPageFragment;
import com.itu.software.ituhermes.Tasks.SubscribeTask;
import com.itu.software.ituhermes.Wrapper.Topic;

public class PostPagerActivity extends AppCompatActivity implements IUICallback<Void>, ViewPager.OnPageChangeListener {
    private static final String TAG = "PostPagerActivity";
    public static final String TOPIC_KEY = "com.itu.software.ituhermes.postPager.topicKey";
    public static final String PAGENUMBER_KEY = "com.itu.software.ituhermes.postPager.pageNumberKey";
    ViewPager viewPager;
    PostPagerAdapter adapter;
    FloatingActionButton floatingActionButton;
    Toolbar toolbar;
    TextView toolbarTitle;
    SwitchCompat subscribeSwitch;
    Topic topic;
    int pageCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_pager);
        viewPager = findViewById(R.id.post_pager);
        floatingActionButton = findViewById(R.id.create_post_button);
        topic = (Topic) getIntent().getExtras().getSerializable(TOPIC_KEY);
        pageCount = 1;
        toolbar = findViewById(R.id.post_toolbar);
        toolbarTitle = findViewById(R.id.posts_toolbar_text);
        toolbarTitle.setText(topic.getTitle());
        subscribeSwitch = toolbar.findViewById(R.id.subscribe_switch);
        subscribeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                SubscribeTask task = new SubscribeTask(PostPagerActivity.this, topic);
                task.execute();
            }
        });
        if (topic.isSubscribing()) {
            subscribeSwitch.setText(R.string.subscribing);
            subscribeSwitch.setChecked(true);
        } else {
            subscribeSwitch.setText(R.string.notsubscribing);
            subscribeSwitch.setChecked(false);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new PostPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDialogFragment fragment = new PostDialogFragment();
                Bundle info = new Bundle();
                info.putSerializable(TOPIC_KEY, topic);
                fragment.setArguments(info);
                fragment.setCallback(PostPagerActivity.this);
                fragment.show(getSupportFragmentManager(), "postDialog");
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        switch (i) {
            case ViewPager.SCROLL_STATE_DRAGGING: {
                if (!viewPager.canScrollHorizontally(1)) {
                    pageCount++;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void callbackUI(Code code, Void data) {
    }

    @Override
    public void callbackUI(Code code) {
        switch (code) {
            case POST_SUCCESS: {
                Snackbar.make(viewPager, getString(R.string.post_success), Snackbar.LENGTH_SHORT).show();
                adapter.updateCurrentFragment();
                break;
            }
            case FAIL: {
                if (pageCount > 1) {
                    pageCount--;
                    adapter.notifyDataSetChanged();
                }
                break;
            }
            case SUBSCRIBE_SUCCESS: {
                switchSubscription();
                break;
            }
            case SUBSCRIBE_FAIL: {
                Snackbar.make(viewPager, getString(R.string.unidentified_error), Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private void switchSubscription() {
        if (!topic.isSubscribing()) {
            subscribeSwitch.setText(R.string.subscribing);
            subscribeSwitch.setChecked(true);
            topic.setSubscribing(true);
        } else {
            subscribeSwitch.setText(R.string.notsubscribing);
            subscribeSwitch.setChecked(false);
            topic.setSubscribing(false);
        }
        subscribeSwitch.setClickable(true);

    }

    private class PostPagerAdapter extends FragmentStatePagerAdapter {
        private PostPageFragment currentFragment;

        PostPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new PostPageFragment();
            Bundle info = new Bundle();
            info.putSerializable(TOPIC_KEY, topic);
            info.putInt(PAGENUMBER_KEY, i);
            fragment.setArguments(info);
            ((PostPageFragment) fragment).setCallback(PostPagerActivity.this);
            return fragment;
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (currentFragment != object) {
                currentFragment = (PostPageFragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }

        void updateCurrentFragment() {
            currentFragment.updateItems();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            PostPageFragment fragment = (PostPageFragment) object;
            if (fragment.isFailed()) {
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

}
