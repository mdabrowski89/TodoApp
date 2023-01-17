package pl.mobite.todoapp.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import pl.mobite.lib.viewbinding.viewBinding
import pl.mobite.todoapp.home.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNavigation()
    }

    private fun initBottomNavigation() = with(binding) {
        val childNavController = (childFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment).navController
        NavigationUI.setupWithNavController(bottomNavigation, childNavController)
    }
}
