package food.map

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

const val PAGE_NUMBER = 3

class MainViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount() = PAGE_NUMBER

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> { BlankFragment.newInstance() }
            1 -> { BlankFragment.newInstance() }
            else -> { BlankFragment.newInstance() }
        }
    }
}