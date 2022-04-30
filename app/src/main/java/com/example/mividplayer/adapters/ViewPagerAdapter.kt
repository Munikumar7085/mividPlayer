package com.example.mividplayer.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                       val fragmentlist: ArrayList<Fragment>
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return fragmentlist.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.i("viewAdapter","position: $position : fragment : ${fragmentlist[position]}")

        return fragmentlist[position]
    }

}