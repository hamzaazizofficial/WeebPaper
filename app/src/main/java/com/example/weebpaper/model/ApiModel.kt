package com.example.weebpaper.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ApiModel(application: Application) : AndroidViewModel(application) {
    //    lateinit var currentImgUrl: String
    fun loadMeme(onSuccess: (JSONObject) -> Unit) {
        val queue = Volley.newRequestQueue(getApplication())
        val url = "https://meme-api.herokuapp.com/gimme/animeart"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                onSuccess(response)
                Log.d("checkkk", url)
            },
            {

            })
        //Add the request to the request que
        queue.add(jsonObjectRequest)
    }
}