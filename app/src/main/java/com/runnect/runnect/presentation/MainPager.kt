package com.runnect.runnect.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.runnect.runnect.presentation.coursemain.CourseMainFragment
import com.runnect.runnect.presentation.discover.DiscoverFragment
import com.runnect.runnect.presentation.mypage.MyPageFragment
import com.runnect.runnect.presentation.storage.StorageMainFragment

class MainPager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return fragments.getOrPut(position) {
            when (position) {
                0 -> CourseMainFragment()
                1 -> StorageMainFragment()
                2 -> DiscoverFragment().apply {
                    MainActivity.discoverFragment = this
                }

                3 -> MyPageFragment()
                else -> CourseMainFragment()
            }
        }
    }
}