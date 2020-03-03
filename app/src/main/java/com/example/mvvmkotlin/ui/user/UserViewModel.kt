package com.example.mvvmkotlin.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.mvvmkotlin.model.Repo
import com.example.mvvmkotlin.model.User
import com.example.mvvmkotlin.repository.RepoRepository
import com.example.mvvmkotlin.repository.Resource
import com.example.mvvmkotlin.repository.UserRepository
import com.example.mvvmkotlin.utils.AbsentLiveData
import javax.inject.Inject
import kotlin.math.log

class UserViewModel
@Inject constructor(userRepository: UserRepository, repoRepository: RepoRepository) : ViewModel() {

    private val _login = MutableLiveData<String>()
    val login: LiveData<String> get() = _login

    val repositories: LiveData<Resource<List<Repo>>> = Transformations.switchMap(_login) {
        login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repoRepository.loadRepos(login)
            }
    }

    val user: LiveData<Resource<User>> = Transformations.switchMap(_login) {
        login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUser(login)
            }
    }

    fun setLogin(login: String?) {
        if (_login.value != login) {
            _login.value = login
        }
    }

    fun retry(){
        _login.value?.let {
            _login.value = it
        }
    }

}