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
public class SignupActivityTest {
    @Rule
    public ActivityTestRule<SignupActivity> rule = new ActivityTestRule<>(SignupActivity.class);

    @Test
    public void validInputs() {
        onView(withId(R.id.signup_email)).perform(typeText("hasa1n@gmail.com")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_name)).perform(typeText("Hasan")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_lname)).perform(typeText("Gok")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_pass)).perform(typeText("11235813")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_pass_val)).perform(typeText("11235813")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        Assert.assertEquals(rule.getActivityResult().getResultCode(), RESULT_OK);
    }

    @Test
    public void mismatchedPasswords() {
        onView(withId(R.id.signup_email)).perform(typeText("hasa1n@gmail.com")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_name)).perform(typeText("Hasan")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_lname)).perform(typeText("Gok")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_pass)).perform(typeText("11235813")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_pass_val)).perform(typeText("11235812")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        String err = rule.getActivity().getResources().getString(R.string.error_password_mismatch);
        onView(withId(R.id.signup_pass)).check(matches(hasErrorText(err)));
    }

    @Test
    public void emptyFields() {
        onView(withId(R.id.signup_button)).perform(click());
        String err = rule.getActivity().getResources().getString(R.string.error_field_required);
        onView(withId(R.id.signup_email)).check(matches(hasErrorText(err)));
    }
}
