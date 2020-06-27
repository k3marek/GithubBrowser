package com.k3labs.githubbrowser.ui.user

import androidx.lifecycle.*
import com.k3labs.githubbrowser.di.AssistedSavedStateViewModelFactory
import com.k3labs.githubbrowser.repository.FavRepoRepository
import com.k3labs.githubbrowser.repository.RepoRepository
import com.k3labs.githubbrowser.repository.UserRepository
import com.k3labs.githubbrowser.util.AbsentLiveData
import com.k3labs.githubbrowser.vo.RepoAndFav
import com.k3labs.githubbrowser.vo.Resource
import com.k3labs.githubbrowser.vo.User
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class UserViewModel @AssistedInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val favRepoRepository: FavRepoRepository,
    repoRepository: RepoRepository
) : ViewModel() {
    private val _login = MutableLiveData<String>()
    val login: LiveData<String>
        get() = _login
    val repositories: LiveData<Resource<List<RepoAndFav>>> = Transformations
        .switchMap(_login) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repoRepository.loadRepos(login).asLiveData()
            }
        }
    val user: LiveData<Resource<User>> = Transformations
        .switchMap(_login) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUser(login).asLiveData()
            }
        }

    init {
//        setLogin("w@w.com")
    }

    fun setLogin(login: String?) {
        if (_login.value != login) {
            _login.value = login
        }
    }

    fun retry() {
        _login.value?.let {
            _login.value = it
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

    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<UserViewModel>
}
