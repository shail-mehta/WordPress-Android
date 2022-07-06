package org.wordpress.android.e2e.pages;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.test.espresso.ViewInteraction;

import org.hamcrest.Matcher;
import org.wordpress.android.R;
import org.wordpress.android.support.WPSupportUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.wordpress.android.support.WPSupportUtils.clickOn;
import static org.wordpress.android.support.WPSupportUtils.getTranslatedString;
import static org.wordpress.android.support.WPSupportUtils.isElementDisplayed;
import static org.wordpress.android.support.WPSupportUtils.longClickOn;
import static org.wordpress.android.support.WPSupportUtils.selectItemWithTitleInTabLayout;
import static org.wordpress.android.support.WPSupportUtils.waitForElementToBeDisplayedWithoutFailure;

public class MySitesPage {
    private static ViewInteraction chooseSiteLabel =
            onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))));

    public MySitesPage() {
    }

    public MySitesPage go() {
        clickOn(R.id.nav_sites);

        return this;
    }

    public void switchSite() {
        clickOn(R.id.switch_site);
        chooseSiteLabel.check(matches(withText("Choose site")));
    }

    private void longClickSite(String siteName) {
        ViewInteraction siteRow = onView(withText(siteName));
        longClickOn(siteRow);
    }

    public void removeSite(String siteName) {
        switchSite();
        longClickSite(siteName);
        clickOn(android.R.id.button1);
    }

    public void startNewPost() {
        clickOn(R.id.fab_button);
        if (isElementDisplayed(R.id.design_bottom_sheet)) {
            // If Stories are enabled, FAB opens a bottom sheet with options - select the 'Blog post' option
            clickOn(onView(withText(R.string.my_site_bottom_sheet_add_post)));
        }
    }

    public void clickSettingsItem() {
        clickItemWithText(R.string.my_site_btn_site_settings);
    }

    public void clickBlogPostsItem() {
        clickItemWithText(R.string.my_site_btn_blog_posts);
    }

    public void clickActivityLog() {
        clickItemWithText(R.string.activity_log);
    }

    public void clickScan() {
        clickItemWithText(R.string.scan);
    }

    public void clickBackup() {
        clickItemWithText(R.string.backup);
    }

    public void goToStats() {
        goToMenuTab();
        clickItemWithText(R.string.stats);
    }

    public void goToMedia() {
        goToMenuTab();
        clickItemWithText(R.string.media);
    }

    public void switchToSite(String siteUrl) {
        // Choose the "sites" tab in the nav
        clickOn(R.id.nav_sites);

        // Choose "Switch Site"
        clickOn(R.id.switch_site);

        (new SitePickerPage()).chooseSiteWithURL(siteUrl);
    }

    public StatsPage clickStats() {
        clickItemWithText(R.string.stats);
        waitForElementToBeDisplayedWithoutFailure(
                onView(withId(R.id.tabLayout))
        );

        // Stats are opened with the last selected tab active (or `Insights` by default)
        // Since we don't know what was the last used tab, we're using a "dumb" wait
        // instead of the conditional wait (e.g. waiting for a certain tab element to appear).
        WPSupportUtils.sleep();

        return new StatsPage();
    }

    private void clickItemWithText(int stringResId) {
        clickItem(withText(stringResId));
    }

    private void clickItem(final Matcher<View> itemViewMatcher) {
        if (isElementDisplayed(R.id.recycler_view)) {
            // If My Site Improvements are enabled, we reach the item in a different way
            onView(withId(R.id.recycler_view))
                    .perform(actionOnItem(hasDescendant(itemViewMatcher), click()));
        }
    }

    public static void goToPosts() {
        goToMenuTab();
        clickOn(onView(withText(R.string.posts)));
    }

    @SuppressWarnings("unused")
    public static void goToHomeTab() {
        selectItemWithTitleInTabLayout(getTranslatedString(R.string.my_site_dashboard_tab_title), R.id.tab_layout);
    }

    public static void goToMenuTab() {
        selectItemWithTitleInTabLayout(getTranslatedString(R.string.my_site_menu_tab_title), R.id.tab_layout);
    }
}
