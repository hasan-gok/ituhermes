package com.itu.software.ituhermes;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.ViewActions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void correctCredentials() {
        String email = "hasangok@gmail.com";
        String password = "11235813";
        onView(withId(R.id.login_email)).perform(typeText(email));
        onView(withId(R.id.login_password)).perform(typeText(password)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        Assert.assertEquals(rule.getActivityResult().getResultCode(), RESULT_OK);
    }

    @Test
    public void wrongCredentials() {
        String email = "asdasdasd@sdsa.com";
        String password = "1231sdsa";
        onView(withId(R.id.login_email)).perform(typeText(email));
        onView(withId(R.id.login_password)).perform(typeText(password)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        String no_user = rule.getActivity().getResources().getString(R.string.error_no_user);
        onView(withId(R.id.login_email)).check(matches((hasErrorText(no_user))));
    }
}
