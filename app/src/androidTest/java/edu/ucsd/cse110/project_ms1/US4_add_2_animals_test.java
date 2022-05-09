package edu.ucsd.cse110.project_ms1;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US4_add_2_animals_test {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void uS4_add_2_animals() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.search_bar_2), withContentDescription("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.add_to_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.all_searched_animals),
                                        1),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.selected_animals_number), withText("1"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.an_selected_animal), withText("Lions"),
                        withParent(withParent(withId(R.id.all_selected_animals))),
                        isDisplayed()));
        textView2.check(matches(withText("Lions")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.an_selected_animal), withText("Lions"),
                        withParent(withParent(withId(R.id.all_selected_animals))),
                        isDisplayed()));
        textView3.check(matches(withText("Lions")));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.add_to_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.all_searched_animals),
                                        0),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.add_to_button), withText("Add"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.all_searched_animals),
                                        0),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.selected_animals_number), withText("2"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView4.check(matches(withText("2")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Collapse"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.action_bar),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.all_selected_animals),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.an_selected_animal), withText("Lions"),
                        withParent(withParent(withId(R.id.all_selected_animals))),
                        isDisplayed()));
        textView5.check(matches(withText("Lions")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.an_selected_animal), withText("Elephant Odyssey"),
                        withParent(withParent(withId(R.id.all_selected_animals))),
                        isDisplayed()));
        textView6.check(matches(withText("Elephant Odyssey")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
