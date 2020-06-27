package com.k3labs.githubbrowser.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.databinding.RepoListItemBinding
import com.k3labs.githubbrowser.ui.common.DataBoundListAdapter
import com.k3labs.githubbrowser.vo.Repo

/**
 * A RecyclerView adapter for [Repo] class.
 */
class RepoListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val favClickCallback: ((Repo) -> Unit)?,
    private val navigateToDetailsCallback: ((Repo) -> Unit)?
) : DataBoundListAdapter<Repo, RepoListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup, viewType: Int): RepoListItemBinding {
        val binding = DataBindingUtil.inflate<RepoListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.repo_list_item,
            parent,
            false,
            dataBindingComponent
        )
//        binding.showFullName = showFullName
        binding.setClickListenerItem { view ->
            binding.repo?.let {
                navigateToDetailsCallback?.invoke(it)
            }
        }
        binding.setClickListenerFav {
            binding.repo.let {
                favClickCallback?.invoke(it!!)
            }
        }
        return binding
    }


    override fun bind(binding: RepoListItemBinding, item: Repo) {
        binding.repo = item
    }
}
