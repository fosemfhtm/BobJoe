package food.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import food.map.databinding.FragmentBlankBinding

class BlankFragment: Fragment() {
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!

    companion object{
        fun newInstance(): BlankFragment {
            val args = Bundle().apply {
                //putString("test", "test")
            }

            val fragment = BlankFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textview.text = "hello!"
    }
}