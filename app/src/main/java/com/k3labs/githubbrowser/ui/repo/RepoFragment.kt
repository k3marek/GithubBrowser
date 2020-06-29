package com.k3labs.githubbrowser.ui.repo

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.binding.FragmentDataBindingComponent
import com.k3labs.githubbrowser.databinding.RepoFragmentBinding
import com.k3labs.githubbrowser.di.Injectable
import com.k3labs.githubbrowser.ui.common.RetryCallback
import com.k3labs.githubbrowser.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

/**
 * The UI Controller for displaying a Github Repo's information with its contributors.
 */
class RepoFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val repoViewModel: RepoViewModel by viewModels {
        viewModelFactory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<RepoFragmentBinding>()

    private val params by navArgs<RepoFragmentArgs>()
    private var adapter by autoCleared<ContributorAdapter>()

    private lateinit var menuItemSwitchFav: MenuItem


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.repo_fragment,
            container,
            false
        )
        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                repoViewModel.retry()
            }
        }
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.move)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_repo, menu)
        menuItemSwitchFav = menu.findItem(R.id.action_switch_fav)
        updateFavMenuIcon(repoViewModel.fav.value != null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_fav -> {
                switchFav()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Timber.v("onViewCreated params ${params.id}")
        repoViewModel.setId(params.id, params.owner, params.name)

        binding.lifecycleOwner = viewLifecycleOwner

        adapter = ContributorAdapter(dataBindingComponent, appExecutors) { contributor, imageView ->
            val extras = FragmentNavigatorExtras(
                imageView to contributor.login
            )
            findNavController().navigate(
                RepoFragmentDirections.showUser(contributor.login, contributor.avatarUrl),
                extras
            )
        }
        binding.contributorList.adapter = adapter

        postponeEnterTransition()
        binding.contributorList.doOnPreDraw {
            startPostponedEnterTransition()
        }

        subscribeUi(binding)
    }

    private fun subscribeUi(binding: RepoFragmentBinding) {
        binding.repoAndFav = repoViewModel.repo
        repoViewModel.repo.observe(viewLifecycleOwner) {
//            Timber.v("repo owner ${it.data?.repo?.owner ?: "null"}")
            if (::menuItemSwitchFav.isInitialized) {
                menuItemSwitchFav.isEnabled = it.data != null
            }
        }
        repoViewModel.fav.observe(viewLifecycleOwner) {
            if (::menuItemSwitchFav.isInitialized) {
                updateFavMenuIcon(it != null)
            }
        }
        repoViewModel.contributors.observe(viewLifecycleOwner, Observer { listResource ->
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    private fun updateFavMenuIcon(isFav: Boolean) {
        menuItemSwitchFav.icon = ContextCompat.getDrawable(
            requireContext(),
            if (isFav) R.drawable.ic_bookmark_black_24dp else R.drawable.ic_bookmark_border_black_24dp
        )
    }

    private fun switchFav() {
        repoViewModel.switchFav()
    }
}
