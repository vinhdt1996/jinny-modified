package sg.prelens.jinny.repositories.inmemory.promotionarchived

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.features.promotion.PromotionPageFragment
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.PromotionRepository
import sg.prelens.jinny.repositories.inmemory.promotion.PromotionDataSourceFactory
import java.util.concurrent.Executor

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/19/18<br>.
 */
class PromotionArchivedInMemoryByPageKeyedRepository(private val api: ApiLink,
                                                     private val networkExecutor: Executor) : PromotionRepository {


    override fun fetchPromotion(keyword: String, pageSize: Int, order: String): Listing<PromotionList> {
        val sourceFactory = PromotionDataSourceFactory(api, networkExecutor, keyword, order,PromotionPageFragment.TYPE_ARCHIVED)
        val config = PagedList
                .Config
                .Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize)
                .build()
        val livePagedList = LivePagedListBuilder(sourceFactory, config)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                .setBackgroundThreadExecutor(networkExecutor)
                .build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceArchivedLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceArchivedLiveData, {
                    it.networkState
                }),
                retry = {
                    sourceFactory.sourceArchivedLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceArchivedLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }
}