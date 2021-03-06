package food.map.gallery

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import food.map.databinding.FragmentGalleryBinding
import food.map.databinding.RvItemGalleryBinding
import gun0912.tedimagepicker.builder.TedImagePicker
import java.io.*


class GalleryFragment: Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private var selectedUriList: List<Uri>? = null


    companion object{
        fun newInstance(): GalleryFragment {
            val args = Bundle().apply {
                //putString("test", "test")
            }

            val fragment = GalleryFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)


        showMultiImage(loadImage())

        binding.button.setOnClickListener {
            TedPermission.create()
                .setDeniedMessage("[설정]에서 카메라 권한을 허용해주세요!")
                .setPermissions(android.Manifest.permission.CAMERA)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    }

                })
                .check()

            TedPermission.create()
                .setDeniedMessage("[설정]에서 저장소 권한을 허용해주세요!")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    }

                })
                .check()

            TedImagePicker.with(requireContext()).startMultiImage { uriList -> showMultiImage(uriList) }
        }

        super.onCreate(savedInstanceState)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun saveImage(uriList: List<Uri>) {

        var photo_uriList_str = ""
        for (i in uriList.indices){
            val photo_uri_str = uriList[i].toString()
            photo_uriList_str = photo_uriList_str + photo_uri_str +"\n"
        }

        val filePath = requireContext().filesDir.path + "/photo_uriList.txt"
        val file = File(filePath)
        val fileWriter = FileWriter(file, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(photo_uriList_str)
        bufferedWriter.close()

    }


    private fun loadImage():ArrayList<Uri> {
        val filePath = requireContext().filesDir.path + "/photo_uriList.txt"
        val file = File(filePath)
        if(!file.exists()) {
            saveImage(arrayListOf<Uri>())
        }
        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)
        val uriList = arrayListOf<Uri>()

        bufferedReader.readLines().forEach() {
            Log.d("Load", it)
            uriList.add(Uri.parse(it))

        }
        return uriList
    }

    fun deleteImage(tag: String) {
        val uriList = loadImage()
        uriList.removeAt(tag.toInt())
        saveImage(uriList)
        Toast.makeText(context, "사진이 삭제되었습니다!", Toast.LENGTH_SHORT).show()
    }

    fun showBiggerImage(tag: String){
        val uriList = loadImage()
        val uri = uriList.get(tag.toInt())
        binding.ivImage.visibility = View.VISIBLE
        binding.containerSelectedPhotos.visibility = View.GONE
        binding.hsvImage.visibility = View.GONE
        binding.button.visibility = View.GONE

        Glide.with(this).load(uri).into(binding.ivImage)

        binding.ivImage.setOnClickListener {
            showMultiImage(loadImage())
            Log.d("sdsd", "sdsd")
        }

    }



    private fun showMultiImage(uriList: List<Uri>) {
        val saved_uriList = loadImage().union(uriList)
        saveImage(saved_uriList.toList())
        val selectedUriList = saved_uriList.toList()
        Log.d("ted", "uriList: $uriList")
        binding.ivImage.visibility = View.GONE
        binding.hsvImage.visibility  =View.VISIBLE
        binding.containerSelectedPhotos.visibility = View.VISIBLE
        binding.button.visibility = View.VISIBLE

        binding.containerSelectedPhotos.removeAllViews()



        val heightPixels = resources.displayMetrics.heightPixels
        val widthPixels = resources.displayMetrics.widthPixels
        val xdpi = resources.displayMetrics.xdpi
        val ydpi = resources.displayMetrics.ydpi

        val column = selectedUriList.size/3
        val remainder = selectedUriList.size%3

        for (i in 0..(column- 1)){
            val itemImageBinding = RvItemGalleryBinding.inflate(LayoutInflater.from(context))
            Glide.with(this)
                .load(selectedUriList[3*i])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg1)
            Glide.with(this)
                .load(selectedUriList[3*i+1])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg2)
            Glide.with(this)
                .load(selectedUriList[3*i+2])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg3)

            itemImageBinding.dogPhotoImg1.setTag("${3*i}")
            itemImageBinding.dogPhotoImg2.setTag("${3*i+1}")
            itemImageBinding.dogPhotoImg3.setTag("${3*i+2}")

            itemImageBinding.root.layoutParams = FrameLayout.LayoutParams(widthPixels, widthPixels/3)

            itemImageBinding.dogPhotoImg1.setOnLongClickListener {
                Log.d("delete", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())
                true
            }
            itemImageBinding.dogPhotoImg2.setOnLongClickListener {
                Log.d("delete", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())

                true
            }
            itemImageBinding.dogPhotoImg3.setOnLongClickListener {
                Log.d("delete", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())

                true
            }

            itemImageBinding.dogPhotoImg1.setOnClickListener {
                showBiggerImage(it.tag.toString())
            }
            itemImageBinding.dogPhotoImg2.setOnClickListener {
                showBiggerImage(it.tag.toString())
            }
            itemImageBinding.dogPhotoImg3.setOnClickListener {
                showBiggerImage(it.tag.toString())

            }

            binding.containerSelectedPhotos.addView(itemImageBinding.root)
        }

        if (remainder == 1){
            val itemImageBinding = RvItemGalleryBinding.inflate(LayoutInflater.from(context))
            Glide.with(this)
                .load(selectedUriList[3*column])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg1)

            itemImageBinding.dogPhotoImg1.setTag("${3*column}")
            itemImageBinding.dogPhotoImg1.setOnLongClickListener {
                Log.d("dsdsd", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())
                true
            }

            itemImageBinding.dogPhotoImg1.setOnClickListener {
                showBiggerImage(it.tag.toString())
            }

            itemImageBinding.root.layoutParams = FrameLayout.LayoutParams(widthPixels, widthPixels/3)
            binding.containerSelectedPhotos.addView(itemImageBinding.root)
        }

        if (remainder == 2){
            val itemImageBinding = RvItemGalleryBinding.inflate(LayoutInflater.from(context))
            Glide.with(this)
                .load(selectedUriList[3*column])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg1)
            Glide.with(this)
                .load(selectedUriList[3*column+1])
                .apply(RequestOptions().fitCenter())
                .into(itemImageBinding.dogPhotoImg2)

            itemImageBinding.dogPhotoImg1.setTag("${3*column}")
            itemImageBinding.dogPhotoImg1.setOnLongClickListener {
                Log.d("dsdsd", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())
                true
            }
            itemImageBinding.dogPhotoImg2.setTag("${3*column+1}")
            itemImageBinding.dogPhotoImg2.setOnLongClickListener {
                Log.d("dsdsd", it.tag.toString())
                deleteImage(it.tag.toString())
                showMultiImage(loadImage())
                true
            }

            itemImageBinding.dogPhotoImg1.setOnClickListener {
                showBiggerImage(it.tag.toString())
            }
            itemImageBinding.dogPhotoImg2.setOnClickListener {
                showBiggerImage(it.tag.toString())
            }

            itemImageBinding.root.layoutParams = FrameLayout.LayoutParams(widthPixels, widthPixels/3)
            binding.containerSelectedPhotos.addView(itemImageBinding.root)

        }

    }


}
