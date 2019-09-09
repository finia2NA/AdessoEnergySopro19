package energy.adesso.adessoandroidapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import energy.adesso.adessoandroidapp.ui.activity.DetailActivity;
import energy.adesso.adessoandroidapp.ui.activity.MainActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.matcher.ViewMatchers.hasContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

public class ListElementClickTest {
  @Rule
  public IntentsTestRule<MainActivity> mActivity = new IntentsTestRule<MainActivity>(MainActivity.class);

  @Test
  public void click_elec() {
    if (hasChildren(R.id.elec_list)) {
      onView(allOf(
          isDescendantOfA(withId(R.id.elec_list)),
          withId(1))).perform(click());

      intended(allOf(
              hasComponent(DetailActivity.class.getName()),
              hasExtraWithKey("meter")));
    }
  }

  @Test
  public void click_gas() {
    if (hasChildren(R.id.gas_list)) {
      onView(allOf(
          isDescendantOfA(withId(R.id.gas_list)),
          withId(1))).perform(click());

      intended(allOf(
              hasComponent(DetailActivity.class.getName()),
              hasExtraWithKey("meter")));
    }
  }

  @Test
  public void click_water() {
    if (hasChildren(R.id.water_list)) {
      onView(allOf(
          isDescendantOfA(withId(R.id.water_list)),
          withId(1))).perform(click());

      intended(allOf(
              hasComponent(DetailActivity.class.getName()),
              hasExtraWithKey("meter")));
    }
  }

  private boolean hasChildren(int id) {
    View v = null;
    try {
      v = ((ViewGroup)mActivity.getActivity().findViewById(id)).getChildAt(0);
    } catch (Exception e) { }
    return v != null;
  }
}
