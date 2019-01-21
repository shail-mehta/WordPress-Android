package org.wordpress.android.ui.stats.refresh.lists.sections.granular

import kotlinx.coroutines.CoroutineDispatcher
import org.wordpress.android.R
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.network.utils.StatsGranularity
import org.wordpress.android.fluxc.store.StatsStore.StatsTypes
import org.wordpress.android.ui.stats.refresh.lists.sections.BaseStatsUseCase.StatefulUseCase
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem
import java.util.Date

abstract class GranularStatefulUseCase<DOMAIN_MODEL, UI_STATE>(
    type: StatsTypes,
    mainDispatcher: CoroutineDispatcher,
    val selectedDateProvider: SelectedDateProvider,
    val statsGranularity: StatsGranularity,
    defaultUiState: UI_STATE
) : StatefulUseCase<DOMAIN_MODEL, UI_STATE>(type, mainDispatcher, defaultUiState) {
    abstract suspend fun loadCachedData(selectedDate: Date, site: SiteModel)

    final override suspend fun loadCachedData(site: SiteModel) {
        selectedDateProvider.getSelectedDate(statsGranularity)?.let { date ->
            loadCachedData(date, site)
        }
    }

    abstract suspend fun fetchRemoteData(selectedDate: Date, site: SiteModel, forced: Boolean)

    final override suspend fun fetchRemoteData(site: SiteModel, forced: Boolean) {
        selectedDateProvider.getSelectedDate(statsGranularity)?.let { date ->
            fetchRemoteData(date, site, forced)
        }
    }

    override fun buildEmptyItem(): List<BlockListItem> {
        return buildLoadingItem() + listOf(BlockListItem.Empty(textResource = R.string.stats_no_data_for_period))
    }
}
