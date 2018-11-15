package sg.prelens.jinny.features.addmembership

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.Merchant
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.MerchantRepository

/**
 * Created by tommy on 3/16/18.
 */
class MerchantsViewModel(repository: MerchantRepository) : ViewModel() {
    private val filterRequest = MutableLiveData<String>()

    private val repoResult = Transformations.map(filterRequest){
        repository.fetchMerchant(50,it)
    }

    val merchants = Transformations.switchMap(repoResult){
        it -> it.pagedList
    }

    val errorLiveData = repository.error()

    val networkState = Transformations.switchMap(repoResult){
        it -> it.networkState
    }
    val refreshState = Transformations.switchMap(repoResult){
        it -> it.refreshState
    }

    fun filter(keyword: String = "") {
        filterRequest.value = keyword
    }

    fun refresh() {
//        repoResult.refresh.invoke()
        Transformations.map(repoResult){
            it.refresh.invoke()
        }
    }

    fun retry() {
//        repoResult.retry.invoke()
        Transformations.map(repoResult){
            it.retry.invoke()
        }
    }
}