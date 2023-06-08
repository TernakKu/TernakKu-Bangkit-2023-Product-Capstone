package com.dicoding.ternakku.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ternakku.MainViewModel
import com.dicoding.ternakku.preference.LoginPreference
import com.dicoding.ternakku.ui.history.HistoryViewModel
import com.dicoding.ternakku.ui.login.LoginViewModel
import com.dicoding.ternakku.ui.scan.ScanViewModel

class ViewModelFactory(private val pref: LoginPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            /*modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(pref) as T*/
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(pref) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(pref) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}