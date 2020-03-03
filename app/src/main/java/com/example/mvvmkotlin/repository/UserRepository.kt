package com.example.mvvmkotlin.repository

import androidx.lifecycle.LiveData
import com.example.mvvmkotlin.AppExecutors
import com.example.mvvmkotlin.api.ApiResponse
import com.example.mvvmkotlin.api.GithubApi
import com.example.mvvmkotlin.db.UserDao
import com.example.mvvmkotlin.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubApi: GithubApi
){
    fun loadUser(login: String) : LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                return userDao.insert(item)
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.findByLogin(login)
            }

            override fun createCall(): LiveData<ApiResponse<User>> {
                return githubApi.getUser(login)
            }

        }.asLiveData()
    }
}