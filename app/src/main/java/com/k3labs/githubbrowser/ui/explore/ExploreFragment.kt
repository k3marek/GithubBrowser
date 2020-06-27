package com.k3labs.githubbrowser.ui.explore

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.binding.FragmentDataBindingComponent
import com.k3labs.githubbrowser.databinding.ExploreFragmentBinding
import com.k3labs.githubbrowser.di.Injectable
import com.k3labs.githubbrowser.ui.common.RetryCallback
import com.k3labs.githubbrowser.util.autoCleared
import com.k3labs.githubbrowser.util.dismissKeyboard
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ExploreFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<ExploreFragmentBinding>()

    var adapter by autoCleared<RepoAndFavListAdapter>()

    val exploreViewModel: ExploreViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.explore_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        adapter = RepoAndFavListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            favClickCallback = {
                exploreViewModel.switchFav(it)
            },
            navigateToDetailsCallback = {
                navController().navigate(
                    ExploreFragmentDirections.showRepo(it.repo.id, it.repo.owner.login,it.repo.name)
                )
            }
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.query = exploreViewModel.query
        binding.repoList.adapter = adapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                exploreViewModel.refresh()
            }
        }
        subscribeUi()
    }

    private fun subscribeUi() {
        exploreViewModel.results.observe(viewLifecycleOwner, Observer { results ->
            if (results.isAvailable()) {
                adapter.submitList(results.data)
            }
        })
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        // Dismiss keyboard
        v.dismissKeyboard(activity)
        exploreViewModel.setQuery(query)
    }

    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    exploreViewModel.loadNextPage()
                }
            }
        })
        binding.searchResult = exploreViewModel.results
        exploreViewModel.results.observe(viewLifecycleOwner, Observer { result ->
            adapter.submitList(result?.data)
        })

        exploreViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
