package com.itu.software.ituhermes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    Button subscribeButton;
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
        toolbar.setTitle(topic.getTitle());
        subscribeButton = toolbar.findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscribeTask<PostPagerActivity> task = new SubscribeTask<>(PostPagerActivity.this, topic);
                task.execute();
            }
        });
        if (topic.isSubscribing()) {
            subscribeButton.setText(R.string.unsubscribe);
            subscribeButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            subscribeButton.setText(R.string.subscribe);
            subscribeButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
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
        subscribeButton.setClickable(false);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (!topic.isSubscribing()) {
            final ObjectAnimator animator = ObjectAnimator.ofObject(subscribeButton, "backgroundColor", new ArgbEvaluator(), getResources().getColor(R.color.design_default_color_primary_dark), getResources().getColor(R.color.colorAccent));
            animator.setDuration(shortAnimTime);
            Log.d(TAG, "switchSubscription: " + animator.getPropertyName());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    subscribeButton.setText(R.string.unsubscribe);
                    topic.setSubscribing(true);
                    subscribeButton.setClickable(true);
                }
            });
            animator.start();
        } else {
            final ObjectAnimator animator = ObjectAnimator.ofObject(subscribeButton, "backgroundColor", new ArgbEvaluator(), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.design_default_color_primary_dark));
            animator.setDuration(shortAnimTime);
            Log.d(TAG, "switchSubscription: " + animator.getPropertyName());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    subscribeButton.setText(R.string.subscribe);
                    topic.setSubscribing(false);
                    subscribeButton.setClickable(true);
                }
            });
            animator.start();
        }
    }

    private class PostPagerAdapter extends FragmentStatePagerAdapter {
        private PostPageFragment currentFragment;

        public PostPagerAdapter(FragmentManager fm) {
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

        public void updateCurrentFragment() {
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
