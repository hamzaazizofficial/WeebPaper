package com.example.weebpaper.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weebpaper.databinding.ActivityHomeScreenBinding
import com.example.weebpaper.model.ApiModel

class HomeScreen : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding
    private var currentImgUrl: String = ""
    lateinit var apiViewModel: ApiModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiViewModel = ViewModelProvider(this)[ApiModel::class.java]
        showWallpaper()

        binding.fab.setOnClickListener {
            showWallpaper()
        }
    }

    private fun showWallpaper() {
        binding.progressBar.visibility = View.VISIBLE
        apiViewModel.loadMeme(onSuccess = { response ->
            currentImgUrl = response.getString("url")
            Glide.with(this).load(currentImgUrl).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            }).into(binding.imgView)
        })
    }
}