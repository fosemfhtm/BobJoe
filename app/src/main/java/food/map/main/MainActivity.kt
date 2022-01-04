package food.map.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import food.map.R
import food.map.databinding.ActivityMainBinding
import food.map.mapview.MapViewFragment
import food.map.phone.PhoneBookFragment
import food.map.utils.slideLeft

class MainActivity : AppCompatActivity(), MapViewFragment.InfoClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainPageAdapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainPageAdapter = MainViewPagerAdapter(this as FragmentActivity)

        binding.viewpager.apply {
            adapter = mainPageAdapter
            reduceDragSensitivity()
        }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position
                binding.viewpager.isUserInputEnabled = pos != 2
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

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
                    tab.text = "맛집지도"
                    tab.setIcon(android.R.drawable.ic_menu_mylocation)
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

    override fun onBackPressed() {
        binding.viewpager.isUserInputEnabled = true
        super.onBackPressed()
        slideLeft()
    }
    override fun onInfoWindowClicked(name: String) {
        (mainPageAdapter.fragments[0] as PhoneBookFragment).binding.rvPhonebook
            .apply {
                for (i in  0 until this.adapter!!.itemCount){
                    if(this.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<TextView>(
                            R.id.tv_title)?.text.toString() == name){
                        this.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                    }
                }
            }
    }
}