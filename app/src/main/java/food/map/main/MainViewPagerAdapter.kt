package food.map.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import food.map.gallery.GalleryFragment
import food.map.mapview.MapViewFragment
import food.map.phone.PhoneBookFragment

const val PAGE_NUMBER = 3

class MainViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    var fragments : ArrayList<Fragment> = arrayListOf(
        PhoneBookFragment.newInstance(),
        GalleryFragment.newInstance(),
        MapViewFragment.newInstance()
    )

    override fun getItemCount() = PAGE_NUMBER

    override fun createFragment(position: Int) = fragments[position]
}