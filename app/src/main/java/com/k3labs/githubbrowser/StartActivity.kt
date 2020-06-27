package com.k3labs.githubbrowser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.k3labs.githubbrowser.databinding.StartActivityBinding
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


class StartActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: StartActivityBinding = DataBindingUtil.setContentView(
            this,
            R.layout.start_activity
        )
        navController = Navigation.findNavController(this, R.id.nav_host_controller)

        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.explore, R.id.fav, R.id.user))

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.apply {
            bottomNavigationView.setupWithNavController(navController)
            binding.bottomNavigationView.selectedItemId = R.id.explore
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}