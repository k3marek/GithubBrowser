package com.k3labs.githubbrowser.ui.user

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.k3labs.githubbrowser.AppExecutors
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.binding.FragmentDataBindingComponent
import com.k3labs.githubbrowser.databinding.UserFragmentBinding
import com.k3labs.githubbrowser.di.Injectable
import com.k3labs.githubbrowser.ui.common.RetryCallback
import com.k3labs.githubbrowser.ui.explore.RepoAndFavListAdapter
import com.k3labs.githubbrowser.util.autoCleared
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<UserFragmentBinding>()

    val userViewModel: UserViewModel by viewModels { viewModelFactory }

    private val params by navArgs<UserFragmentArgs>()
    private var adapter by autoCleared<RepoAndFavListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<UserFragmentBinding>(
            inflater,
            R.layout.user_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                userViewModel.retry()
            }
        }
        binding = dataBinding
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.move)
        // When the image is loaded, set the image request listener to start the transaction
        binding.imageRequestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }
        postponeEnterTransition(1, TimeUnit.SECONDS)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.args = params
        userViewModel.setLogin(params.login)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.user = userViewModel.user
        adapter = RepoAndFavListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            favClickCallback = {
                userViewModel.switchFav(it)
            },
            navigateToDetailsCallback = {
                navController().navigate(
                    UserFragmentDirections.showRepo(
                        it.repo.id,
                        it.repo.owner.login,
                        it.repo.name
                    )
                )
            }
        )
        binding.repoList.adapter = adapter
        subscribeUi(binding)
    }

    private fun subscribeUi(binding: UserFragmentBinding) {
        userViewModel.repositories.observe(viewLifecycleOwner, Observer { repos ->
            adapter.submitList(repos?.data)
            binding.userResource = repos
        })
        userViewModel.user.observe(viewLifecycleOwner, Observer {
//            Timber.v("User ${it.status} ${it.data?.login}")
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
