package com.k3labs.githubbrowser.di

import com.k3labs.githubbrowser.ui.explore.ExploreFragment
import com.k3labs.githubbrowser.ui.fav.FavFragment
import com.k3labs.githubbrowser.ui.repo.RepoFragment
import com.k3labs.githubbrowser.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): ExploreFragment

    @ContributesAndroidInjector
    abstract fun contributeFavFragment(): FavFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment
}
