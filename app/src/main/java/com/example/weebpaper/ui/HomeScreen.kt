package com.example.weebpaper.ui

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.weebpaper.R
import com.example.weebpaper.databinding.ActivityHomeScreenBinding
import com.example.weebpaper.model.ApiModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

@Suppress("DEPRECATION")
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

    private val arrowFabSize: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.arrow_fab_size
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiViewModel = ViewModelProvider(this)[ApiModel::class.java]
        binding.fabArrow.startAnimation(arrowFabSize)
        setFabTransparency()
        showWallpaper()

        binding.fabArrow.setOnClickListener {
            onArrowUpClicked()
        }

        binding.fabNext.setOnClickListener { showWallpaper() }

        binding.fabSetWallpaer.setOnClickListener {
            Glide.with(this).asBitmap().load(currentImgUrl).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    Snackbar.make(binding.root, "Wallpaper set successfully", Snackbar.LENGTH_LONG)
                        .show()
                    try {
                        wallpaperManager.setBitmap(resource)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })
        }

        binding.fabShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT, "Check out this wallpaper" +
                        "! $currentImgUrl"
            )
            intent.type = "text/plain"
            val shareIntent = Intent.createChooser(intent, "Share this WallPaper")
            startActivity(shareIntent)
        }

        binding.fabSave.setOnClickListener {
            saveImage()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        apiViewModel.imageUrl.observe(this) { imageUrl ->
            currentImgUrl = imageUrl
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
        }
    }

    private fun setFabTransparency() {
        binding.fabNext.alpha = 0.75f
        binding.fabArrow.alpha = 0.75f
        binding.fabSave.alpha = 0.70f
        binding.fabShare.alpha = 0.70f
        binding.fabSetWallpaer.alpha = 0.70f
    }

    private fun saveImage() {
        Glide.with(this).asBitmap().load(currentImgUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                val albumName = "WeebPaper"
                val filename = "${System.currentTimeMillis()}.png"
                val write: (OutputStream) -> Boolean = {
                    resource.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DCIM}/$albumName"
                        )
                    }

                    application.contentResolver.let {
                        it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            ?.let { uri ->
                                it.openOutputStream(uri)?.let(write)
                            }
                    }
                    Snackbar.make(binding.root, "Saved to Gallery!", Snackbar.LENGTH_LONG).show()
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .toString() + File.separator + albumName
                    val file = File(imagesDir)
                    if (!file.exists()) {
                        file.mkdir()
                    }
                    val image = File(imagesDir, filename)
                    write(FileOutputStream(image))
                    Snackbar.make(binding.root, "Saved to Galley!", Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    private fun onArrowUpClicked() {
        setVisibility(fabClicked)
        setAnimation(fabClicked)
        setFabClickable(fabClicked)
        fabClicked = !fabClicked
    }

    private fun setAnimation(fabClicked: Boolean) {
        if (!fabClicked) {
            binding.fabArrow.startAnimation(rotateOpen)
            binding.fabSave.startAnimation(fromBottom)
            binding.fabShare.startAnimation(fromBottom)
            binding.fabSetWallpaer.startAnimation(fromBottom)
        } else {
            binding.fabArrow.startAnimation(rotateClose)
            binding.fabSave.startAnimation(toBottom)
            binding.fabShare.startAnimation(toBottom)
            binding.fabSetWallpaer.startAnimation(toBottom)
        }
    }

    private fun setVisibility(fabClicked: Boolean) {
        if (!fabClicked) {
            binding.fabSave.visibility = View.VISIBLE
            binding.fabShare.visibility = View.VISIBLE
            binding.fabSetWallpaer.visibility = View.VISIBLE
        } else {
            binding.fabSave.visibility = View.INVISIBLE
            binding.fabShare.visibility = View.INVISIBLE
            binding.fabSetWallpaer.visibility = View.INVISIBLE
        }
    }

    private fun setFabClickable(fabClicked: Boolean) {
        if (!fabClicked) {
            binding.fabSave.isClickable = true
            binding.fabShare.isClickable = true
            binding.fabSetWallpaer.isClickable = true
        } else {
            binding.fabSave.isClickable = false
            binding.fabShare.isClickable = false
            binding.fabSetWallpaer.isClickable = false
        }
    }

    private fun showWallpaper() {
        binding.progressBar.visibility = View.VISIBLE
        apiViewModel.loadWallpaper("https://meme-api.herokuapp.com/gimme/imaginarysliceoflife")
    }
}