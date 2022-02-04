package com.example.weebpaper.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weebpaper.R
import com.example.weebpaper.databinding.ActivityHomeScreenBinding
import com.example.weebpaper.model.ApiModel

class HomeScreen : AppCompatActivity() {

    lateinit var binding: ActivityHomeScreenBinding
    private var currentImgUrl: String = ""
    lateinit var apiViewModel: ApiModel
    private var fabClicked = false

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiViewModel = ViewModelProvider(this)[ApiModel::class.java]
        showWallpaper()

        binding.fabArrow.setOnClickListener {
//            showWallpaper()
            onArrowUpClicked()
        }
    }

    private fun onArrowUpClicked() {
        setVisibility(fabClicked)
        setAnimation(fabClicked)
        fabClicked = !fabClicked
    }

    private fun setAnimation(fabClicked: Boolean) {
        if (!fabClicked) {
            binding.fabArrow.startAnimation(rotateOpen)
            binding.fabSave.startAnimation(fromBottom)
            binding.fabShare.startAnimation(fromBottom)
        } else {
            binding.fabArrow.startAnimation(rotateClose)
            binding.fabSave.startAnimation(toBottom)
            binding.fabShare.startAnimation(toBottom)
        }
    }

    private fun setVisibility(fabClicked: Boolean) {
        if (!fabClicked) {
            binding.fabSave.visibility = View.VISIBLE
            binding.fabShare.visibility = View.VISIBLE
        } else {
            binding.fabSave.visibility = View.INVISIBLE
            binding.fabShare.visibility = View.INVISIBLE
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