package food.map

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import food.map.gallery.GalleryFragment
import food.map.phone.PhoneBookFragment

const val PAGE_NUMBER = 3

class MainViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount() = PAGE_NUMBER

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> { PhoneBookFragment.newInstance() }
            1 -> { GalleryFragment.newInstance() }
            else -> { MapViewFragment.newInstance() }
        }
    }
}