package com.k3labs.githubbrowser.ui.fav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.binding.FragmentDataBindingComponent
import com.k3labs.githubbrowser.databinding.FavFragmentBinding
import com.k3labs.githubbrowser.di.Injectable
import com.k3labs.githubbrowser.ui.common.EmptyStateCallback
import com.k3labs.githubbrowser.ui.common.RetryCallback
import com.k3labs.githubbrowser.util.autoCleared
import javax.inject.Inject

class FavFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FavFragmentBinding>()

    var adapter by autoCleared<FavListAdapter>()

    val favViewModel: FavViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fav_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = FavListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            favClickCallback = {
                favViewModel.switchFav(it)
            },
            navigateToDetailsCallback = {
                navController().navigate(
                    FavFragmentDirections.showRepo(
                        it.repo.id,
                        it.repo.owner.login,
                        it.repo.name
                    )
                )
            })
        binding.repos.adapter = adapter


        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                favViewModel.refresh()
            }
        }
        binding.emptyCallback = object : EmptyStateCallback {
            override fun invoke() {
                navController().navigate(FavFragmentDirections.showExplore())
            }
        }
        subscribeUi(binding)
    }

    private fun subscribeUi(binding: FavFragmentBinding) {
        favViewModel.favRepos.observe(viewLifecycleOwner, Observer { result ->
            binding.reposResource = result
            adapter.submitList(result?.data)
        })
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
