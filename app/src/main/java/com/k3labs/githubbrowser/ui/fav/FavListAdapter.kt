package com.k3labs.githubbrowser.ui.fav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.databinding.RepoAndFavListItemBinding
import com.k3labs.githubbrowser.ui.common.DataBoundListAdapter
import com.k3labs.githubbrowser.vo.Repo
import com.k3labs.githubbrowser.vo.RepoAndFav

/**
 * A RecyclerView adapter for [Repo] class.
 */
class FavListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val favClickCallback: ((RepoAndFav) -> Unit)?,
    private val navigateToDetailsCallback: ((RepoAndFav) -> Unit)?
) : DataBoundListAdapter<RepoAndFav, RepoAndFavListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<RepoAndFav>() {
        override fun areItemsTheSame(oldItem: RepoAndFav, newItem: RepoAndFav): Boolean {
            return oldItem.repo.id == newItem.repo.id
        }

        override fun areContentsTheSame(oldItem: RepoAndFav, newItem: RepoAndFav): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup, viewType: Int): RepoAndFavListItemBinding {
        val binding = DataBindingUtil.inflate<RepoAndFavListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.repo_and_fav_list_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.clickListenerFav = View.OnClickListener { view ->
            binding.repoAndFav?.let {
                favClickCallback?.invoke(it)
            }
        }
        
        binding.clickListenerItem = View.OnClickListener {
            binding.repoAndFav?.let {
                navigateToDetailsCallback?.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: RepoAndFavListItemBinding, item: RepoAndFav) {
        binding.repoAndFav = item
    }
}
