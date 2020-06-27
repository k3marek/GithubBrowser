package com.k3labs.githubbrowser.ui.fav

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.k3labs.githubbrowser.repository.FavRepoRepository
import com.k3labs.githubbrowser.vo.RepoAndFav
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavViewModel @Inject constructor(private val favRepoRepository: FavRepoRepository) :
    ViewModel() {

    val favRepos : LiveData<List<RepoAndFav>?> = favRepoRepository.loadFavRepos().asLiveData()

    fun switchFav(fav: RepoAndFav) {
        viewModelScope.launch {
            if (fav.isFav) {
                favRepoRepository.deleteFav(fav.favoriteRepo!!)
            } else {
                favRepoRepository.addFav(fav.repo.id, fav.repo.name, fav.repo.owner.login)
            }
        }
    }

    fun refresh() {
    }

}
