package com.example.weebpaper.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weebpaper.data.ApiService
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _imageUrl: MutableLiveData<String> =
        MutableLiveData()
    val imageUrl: LiveData<String> = _imageUrl
    private val apiService = ApiService.create()

    fun loadWallpaper(subredditName: String) {
        viewModelScope.launch {
            val imageModel = apiService.getImageUrl(subredditName)
            _imageUrl.value = imageModel.imageUrl
        }
    }
}