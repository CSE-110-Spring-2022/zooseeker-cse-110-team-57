package edu.ucsd.cse110.project_ms1;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class US10_aligator_test {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void uS10_aligator_test() {
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
                                        2),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.selected_animals_number), withText("1"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.an_selected_animal), withText("Alligators"),
                        withParent(withParent(withId(R.id.all_selected_animals))),
                        isDisplayed()));
        textView2.check(matches(withText("Alligators")));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.plan_button), withText("Plan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.exhibit_name), withText("Alligators"),
                        withParent(withParent(withId(R.id.planed_route))),
                        isDisplayed()));
        textView3.check(matches(withText("Alligators")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.exhibit_distance), withText("(110.0 ft)"),
                        withParent(withParent(withId(R.id.planed_route))),
                        isDisplayed()));
        textView4.check(matches(withText("(110.0 ft)")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.exhibit_address), withText(" Reptile Road"),
                        withParent(withParent(withId(R.id.planed_route))),
                        isDisplayed()));
        textView5.check(matches(withText(" Reptile Road")));

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.direction_button), withText("Directions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.start_exhibit_name), withText("  From: Entrance and Exit Gate"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView6.check(matches(withText("  From: Entrance and Exit Gate")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.goal_exhibit_name), withText("  To: Alligators"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView7.check(matches(withText("  To: Alligators")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.path_total_distance), withText("(110.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView8.check(matches(withText("(110.0ft)")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.a_direction_item), withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\"."),
                        withParent(withParent(withId(R.id.brief_path))),
                        isDisplayed()));
        textView9.check(matches(withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\".")));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.a_direction_item), withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\"."),
                        withParent(withParent(withId(R.id.brief_path))),
                        isDisplayed()));
        textView10.check(matches(withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\".")));

        ViewInteraction textView11 = onView(
                allOf(withId(R.id.a_direction_item), withText("Walk 100.0ft along \"Reptile Road\" from \"Entrance Plaza\" to \"Alligators\"."),
                        withParent(withParent(withId(R.id.brief_path))),
                        isDisplayed()));
        textView11.check(matches(withText("Walk 100.0ft along \"Reptile Road\" from \"Entrance Plaza\" to \"Alligators\".")));

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.previous_text), withText("\"Entrance and Exit Gate\" (0.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView12.check(matches(withText("\"Entrance and Exit Gate\" (0.0ft)")));

        ViewInteraction textView13 = onView(
                allOf(withId(R.id.next_text), withText("\"Entrance and Exit Gate\" (110.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView13.check(matches(withText("\"Entrance and Exit Gate\" (110.0ft)")));

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.next_button), withText("NEXT"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction textView14 = onView(
                allOf(withId(R.id.goal_exhibit_name), withText("  To: Entrance and Exit Gate"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView14.check(matches(withText("  To: Entrance and Exit Gate")));

        ViewInteraction textView15 = onView(
                allOf(withId(R.id.path_total_distance), withText("(110.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView15.check(matches(withText("(110.0ft)")));

        ViewInteraction textView16 = onView(
                allOf(withId(R.id.a_direction_item), withText("Walk 100.0ft along \"Reptile Road\" from \"Entrance Plaza\" to \"Alligators\"."),
                        withParent(withParent(withId(R.id.brief_path))),
                        isDisplayed()));
        textView16.check(matches(withText("Walk 100.0ft along \"Reptile Road\" from \"Entrance Plaza\" to \"Alligators\".")));

        ViewInteraction textView17 = onView(
                allOf(withId(R.id.a_direction_item), withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\"."),
                        withParent(withParent(withId(R.id.brief_path))),
                        isDisplayed()));
        textView17.check(matches(withText("Walk 10.0ft along \"Entrance Way\" from \"Entrance and Exit Gate\" to \"Entrance Plaza\".")));

        ViewInteraction textView18 = onView(
                allOf(withId(R.id.previous_text), withText("\"Alligators\" (110.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView18.check(matches(withText("\"Alligators\" (110.0ft)")));

        ViewInteraction textView19 = onView(
                allOf(withId(R.id.next_text), withText("\"None\" (0.0ft)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView19.check(matches(withText("\"None\" (0.0ft)")));

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.next_button), withText("NEXT"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction textView20 = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Alert!"),
                        withParent(allOf(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView20.check(matches(withText("Alert!")));

        ViewInteraction textView21 = onView(
                allOf(withId(android.R.id.message), withText("Reach the end of tour."),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
                        isDisplayed()));
        textView21.check(matches(withText("Reach the end of tour.")));

        ViewInteraction materialButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton6.perform(scrollTo(), click());
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
