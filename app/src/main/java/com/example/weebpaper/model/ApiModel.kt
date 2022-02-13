package com.example.weebpaper.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ApiModel(application: Application) : AndroidViewModel(application) {

    private val _imageUrl: MutableLiveData<String> =
        MutableLiveData()
    val imageUrl: LiveData<String> = _imageUrl

    fun loadWallpaper(url: String) {
        val queue = Volley.newRequestQueue(getApplication())

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                _imageUrl.value = response.getString("url")
                Log.d("checkkk", url)
            },
            {

            })
        //Add the request to the request que
        queue.add(jsonObjectRequest)
    }
}