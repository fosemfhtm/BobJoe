package food.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import food.map.databinding.FragmentGalleryBinding

class GalleryFragment: Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

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
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        var food_photoList = arrayListOf<Photo>(
            Photo("gyoban", "daechi", "pizzabay"),

        )

        super.onCreate(savedInstanceState)

        val mAdapter = RVAdapter(context, food_photoList)
        binding.mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(context)
        binding.mRecyclerView.layoutManager = lm
        binding.mRecyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
