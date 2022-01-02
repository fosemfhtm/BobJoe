package food.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import food.map.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewpager.apply {
            adapter = MainViewPagerAdapter(context as FragmentActivity)
            reduceDragSensitivity()
        }

        binding.viewpager.adapter

        TabLayoutMediator(binding.tabs, binding.viewpager) {tab, pos ->
            tab.text = "Title $pos"
            when (pos) {
                0 -> {
                    tab.text = "전화번호부"
                    tab.setIcon(android.R.drawable.ic_menu_call)
                }
                1 -> {
                    tab.text = "갤러리"
                    tab.setIcon(android.R.drawable.ic_menu_gallery)
                }
                2 -> {
                    tab.text = "포털정보"
                    tab.setIcon(android.R.drawable.ic_menu_search)
                }
            }
        }.attach()
    }

    private fun ViewPager2.reduceDragSensitivity() {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true

        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true

        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop*5)
    }
}