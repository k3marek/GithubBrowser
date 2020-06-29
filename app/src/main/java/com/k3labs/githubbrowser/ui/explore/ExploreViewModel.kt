package com.k3labs.githubbrowser.ui.explore

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.k3labs.githubbrowser.repository.FavRepoRepository
import com.k3labs.githubbrowser.repository.RepoRepository
import com.k3labs.githubbrowser.util.AbsentLiveData
import com.k3labs.githubbrowser.vo.RepoAndFav
import com.k3labs.githubbrowser.vo.Resource
import com.k3labs.githubbrowser.vo.Status
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ExploreViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val favRepoRepository: FavRepoRepository
) : ViewModel() {

    private val _query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(repoRepository)
    val query: LiveData<String> = _query

    val results: LiveData<Resource<List<RepoAndFav>?>> = _query.switchMap { search ->
        if (search.isBlank()) {
            AbsentLiveData.create()
        } else {
            repoRepository.search(search).asLiveData()
        }
    }


    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) {
            return
        }
        nextPageHandler.reset()
        _query.value = input
    }

    fun loadNextPage() {
        viewModelScope.launch {
            _query.value?.let {
                if (it.isNotBlank()) {
                    nextPageHandler.queryNextPage(it)
                }
            }
        }
    }

    fun refresh() {
        _query.value?.let {
            _query.value = it
        }
    }

    fun switchFav(fav: RepoAndFav) {
        viewModelScope.launch {
            if (fav.isFav) {
                favRepoRepository.deleteFav(fav.favoriteRepo!!)
            } else {
                favRepoRepository.addFav(fav.repo.id, fav.repo.name, fav.repo.owner.login)
            }
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: RepoRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        suspend fun queryNextPage(query: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query).asLiveData()
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }
    }
}
