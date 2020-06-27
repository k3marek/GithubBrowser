package com.k3labs.githubbrowser.ui.repo

import androidx.lifecycle.*
import com.k3labs.githubbrowser.repository.FavRepoRepository
import com.k3labs.githubbrowser.repository.RepoRepository
import com.k3labs.githubbrowser.util.AbsentLiveData
import com.k3labs.githubbrowser.vo.Contributor
import com.k3labs.githubbrowser.vo.FavoriteRepo
import com.k3labs.githubbrowser.vo.RepoAndFav
import com.k3labs.githubbrowser.vo.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

//@OpenForTesting
class RepoViewModel @Inject constructor(
    repository: RepoRepository,
    private val favRepoRepository: FavRepoRepository
) : ViewModel() {
    private val _repoId: MutableLiveData<RepoId> = MutableLiveData()

    val repoId: LiveData<RepoId>
        get() = _repoId

    val repo: LiveData<Resource<RepoAndFav?>> = _repoId.switchMap { input ->
        input.ifExists { id, owner, name ->
            repository.loadRepo(owner, name).asLiveData()
        }
    }

    val fav: LiveData<FavoriteRepo?> = _repoId.switchMap { input ->
        input.ifExists { id, owner, name ->
            favRepoRepository.loadFav(id, owner, name).asLiveData()
        }
    }

    val contributors: LiveData<Resource<List<Contributor>?>> = _repoId.switchMap { input ->
        input.ifExists { id, owner, name ->
            repository.loadContributors(id, owner, name).asLiveData()
        }
    }

    fun switchFav() {
        viewModelScope.launch {
            repo.value?.let {
                if (it.isAvailable()) {
                    if (it.data!!.isFav) {
                        favRepoRepository.deleteFav(it.data.favoriteRepo!!)
                    } else {
                        favRepoRepository.addFav(
                            it.data.repo.id,
                            it.data.repo.name,
                            it.data.repo.owner.login
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        val id = _repoId.value?.id
        val owner = _repoId.value?.owner
        val name = _repoId.value?.name
        if (id != null && owner != null && name != null) {
            _repoId.value = RepoId(id, owner, name)
        }
    }

    fun setId(id: Int, owner: String, name: String) {
        val update = RepoId(id, owner, name)
        if (_repoId.value == update) {
            return
        }
        _repoId.value = update
    }

    data class RepoId(val id: Int, val owner: String, val name: String) {
        fun <T> ifExists(f: (Int, String, String) -> LiveData<T>): LiveData<T> {
            return if (owner.isBlank() || name.isBlank()) {
                AbsentLiveData.create()
            } else {
                f(id, owner, name)
            }
        }
    }
}
