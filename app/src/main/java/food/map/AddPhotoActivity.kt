package food.map.phone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import food.map.MainActivity
import food.map.Photo
import food.map.data.PhonePage
import food.map.databinding.ActivityAddPhoneBinding
import food.map.databinding.FragmentGalleryBinding
import gun0912.tedimagepicker.builder.TedImagePicker


class AddPhotoActivity : Activity() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var data: ArrayList<Photo>
    private var selectedUriList: List<Uri>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

}