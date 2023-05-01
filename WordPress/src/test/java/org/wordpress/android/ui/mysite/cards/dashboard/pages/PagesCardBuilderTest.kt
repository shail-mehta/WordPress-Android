package org.wordpress.android.ui.mysite.cards.dashboard.pages

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.dashboard.CardModel.PagesCardModel
import org.wordpress.android.fluxc.network.rest.wpcom.dashboard.CardsUtils
import org.wordpress.android.ui.mysite.MySiteCardAndItem.Card.DashboardCards.DashboardCard.PagesCard.PagesCardWithData
import org.wordpress.android.ui.mysite.MySiteCardAndItemBuilderParams
import org.wordpress.android.ui.mysite.MySiteCardAndItemBuilderParams.PagesCardBuilderParams.PagesItemClickParams
import org.wordpress.android.ui.utils.UiString.UiStringRes
import org.wordpress.android.util.DateTimeUtilsWrapper
import org.wordpress.android.util.config.DashboardCardPagesConfig
import kotlin.test.assertEquals

const val PAGE_STATUS_PUBLISH = "publish"
const val PAGE_STATUS_DRAFT = "draft"
const val PAGE_STATUS_SCHEDULED = "future"

const val PAGE_ID = 1
const val PAGE_TITLE = "title"
const val PAGE_CONTENT = "content"
val PAGE_MODIFIED_ON = CardsUtils.fromDate("2023-03-02 10:26:53")
const val PAGE_STATUS = PAGE_STATUS_PUBLISH
val PAGE_DATE = CardsUtils.fromDate("2023-03-02 10:30:53")

private val PAGE_MODEL = PagesCardModel.PageCardModel(
    id = PAGE_ID,
    title = PAGE_TITLE,
    content = PAGE_CONTENT,
    lastModifiedOrScheduledOn = PAGE_MODIFIED_ON,
    status = PAGE_STATUS,
    date = PAGE_DATE
)

private val PAGE_MODEL_2 = PAGE_MODEL.copy(id = 2, status = PAGE_STATUS_DRAFT)

private val PAGE_MODEL_3 = PAGE_MODEL.copy(id = 3, status = PAGE_STATUS_SCHEDULED)

// pages with one item
private val PAGES_MODEL = PagesCardModel(
    pages = listOf(PAGE_MODEL)
)

// pages card with two items
private val PAGES_MODEL_2 = PagesCardModel(
    pages = listOf(PAGE_MODEL, PAGE_MODEL_2)
)

// pages card with three items
private val PAGES_MODEL_3 = PagesCardModel(
    pages = listOf(PAGE_MODEL, PAGE_MODEL_2, PAGE_MODEL_3)
)

@ExperimentalCoroutinesApi
class PagesCardBuilderTest : BaseUnitTest() {
    @Mock
    private lateinit var dashboardCardPagesConfig: DashboardCardPagesConfig

    @Mock
    private lateinit var dateTimeUtilsWrapper: DateTimeUtilsWrapper

    private lateinit var builder: PagesCardBuilder

    private val onPagesCardFooterClick: () -> Unit = { }
    private val onPagesItemClick: (params: PagesItemClickParams) -> Unit = {}

    @Before
    fun build() {
        builder = PagesCardBuilder(dashboardCardPagesConfig, dateTimeUtilsWrapper)
        setupMocks()
    }

    private fun setupMocks() {
        whenever(dateTimeUtilsWrapper.javaDateToTimeSpan(any())).thenReturn("")
        whenever(dateTimeUtilsWrapper.getRelativeTimeSpanString(any())).thenReturn("")
    }

    @Test
    fun `given config is false, when card is built, then return null`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(false)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params)

        assert(result == null)
    }

    @Test
    fun `given there is no page, when card is built, then no pages item is present`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = null,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assert(result.pages.isEmpty())
    }

    @Test
    fun `given a page with published status, when card is built, then pages item has published icon and text`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(UiStringRes(R.string.dashboard_card_page_item_status_published), result.pages[0].status)
        assertEquals(R.drawable.ic_published_page_dashboard_card, result.pages[0].statusIcon)
    }

    @Test
    fun `given there is a page with draft status, when card is built, then pages item has draft icon and text`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL_2,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(UiStringRes(R.string.dashboard_card_page_item_status_draft), result.pages[1].status)
        assertEquals(R.drawable.ic_draft_page_draft_dashboard_card, result.pages[1].statusIcon)
    }

    @Test
    fun `given a page with scheduled status, when card is built, then pages item has scheduled icon and text`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL_3,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(UiStringRes(R.string.dashboard_card_page_item_status_scheduled), result.pages[2].status)
        assertEquals(R.drawable.ic_scheduled_page_dashboard_card, result.pages[2].statusIcon)
    }

    /* LAST MODIFIED OR SCHEDULED ON TIME*/
    @Test
    fun `given a scheduled page, when card is built, then relative time shown is based on scheduled time`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PagesCardModel(pages = listOf(PAGE_MODEL_3)),
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        builder.build(params) as PagesCardWithData

        verify(dateTimeUtilsWrapper).getRelativeTimeSpanString(PAGE_DATE)
    }

    @Test
    fun `given a published page, when card is built, then relative time shown is based on last modified time`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PagesCardModel(pages = listOf(PAGE_MODEL_2)),
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        builder.build(params) as PagesCardWithData

        verify(dateTimeUtilsWrapper).javaDateToTimeSpan(PAGE_MODIFIED_ON)
    }

    /* CREATE NEW PAGE CARD CASES */
    @Test
    fun `given there is no page, when card is built, then create new page card is correct`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = null,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(expected = createPageCardWhenNoPagesPresent, actual = result.footerLink)
    }

    @Test
    fun `given there is one page, when card is built, then create new page card is correct`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(expected = createPageCardWhenLessThanThreePagePresent, actual = result.footerLink)
    }

    @Test
    fun `given there is two pages, when card is built, then create new page card is correct`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL_2,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(expected = createPageCardWhenLessThanThreePagePresent, actual = result.footerLink)
    }

    @Test
    fun `given there are three pages, when card is built, then create new page card is correct`() {
        whenever(dashboardCardPagesConfig.isEnabled()).thenReturn(true)
        val params = MySiteCardAndItemBuilderParams.PagesCardBuilderParams(
            pageCard = PAGES_MODEL_3,
            onFooterLinkClick = onPagesCardFooterClick,
            onPagesItemClick = onPagesItemClick
        )

        val result = builder.build(params) as PagesCardWithData

        assertEquals(expected = createPageCardWhenThreePagePresent, actual = result.footerLink)
    }

    private val createPageCardWhenNoPagesPresent = PagesCardWithData.CreatNewPageItem(
        label = UiStringRes(R.string.dashboard_pages_card_no_pages_create_page_button),
        description = UiStringRes(R.string.dashboard_pages_card_create_another_page_description),
        imageRes = R.drawable.illustration_page_card_create_page,
        onClick = onPagesCardFooterClick
    )

    private val createPageCardWhenLessThanThreePagePresent = PagesCardWithData.CreatNewPageItem(
        label = UiStringRes(R.string.dashboard_pages_card_create_another_page_button),
        description = UiStringRes(R.string.dashboard_pages_card_create_another_page_description),
        imageRes = R.drawable.illustration_page_card_create_page,
        onClick = onPagesCardFooterClick
    )

    private val createPageCardWhenThreePagePresent = PagesCardWithData.CreatNewPageItem(
        label = UiStringRes(R.string.dashboard_pages_card_create_another_page_button),
        onClick = onPagesCardFooterClick
    )
}
