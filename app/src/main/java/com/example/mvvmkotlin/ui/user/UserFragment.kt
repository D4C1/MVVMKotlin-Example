package com.example.mvvmkotlin.ui.user


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mvvmkotlin.AppExecutors

import com.example.mvvmkotlin.R
import com.example.mvvmkotlin.binding.FragmentDataBindingComponent
import com.example.mvvmkotlin.databinding.FragmentUserBinding
import com.example.mvvmkotlin.di.Injectable
import com.example.mvvmkotlin.ui.common.RepoListAdapter
import com.example.mvvmkotlin.ui.common.RetryCallback
import com.example.mvvmkotlin.utils.autoCleared
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<FragmentUserBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private  val userViewModel: UserViewModel by viewModels {
        viewModelFactory
    }

    private val params by navArgs<UserFragmentArgs>()
    private var adapter by autoCleared<RepoListAdapter>()
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_user, container, false)

        val dataBinding = DataBindingUtil.inflate<FragmentUserBinding>(
            inflater,
            R.layout.fragment_user,
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
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)

        handler.postDelayed(100) {
            startPostponedEnterTransition()
        }

        postponeEnterTransition()
        return dataBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val params = UserFragmentArgs.fromBundle(arguments!!)

        userViewModel.setLogin(params.login)
        userViewModel.user.observe(viewLifecycleOwner, Observer {
            userResource ->
            binding.user = userResource?.data
            binding.userResource = userResource
        })

        val adapter = RepoListAdapter(dataBindingComponent, appExecutors, false) {
            repo ->
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToRepoFragment(repo.name, repo.owner.login))
        }
        binding.repoList.adapter = adapter
        this.adapter = adapter
        initRepoList()
    }

    private fun initRepoList() {
        userViewModel.repositories.observe(viewLifecycleOwner, Observer {
            repos->
            adapter.submitList(repos?.data)
        })
    }


}
