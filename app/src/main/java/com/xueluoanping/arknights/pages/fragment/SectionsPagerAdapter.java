package com.xueluoanping.arknights.pages.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.main.Game;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    public static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private Game.GameInfo[] games;
    private final Context mContext;

    public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        mContext = fragmentActivity;

    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new AccountMangerFragment();
        return GamesFragment.newInstance(games[position-1]);
    }

    @Override
    public int getItemCount() {
        if (games != null)
            return games.length + 1;
        else return 1;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // 注意：DiffUtil 实用程序类依靠 ID 来标识项。如果您使用 ViewPager2 分页浏览可变集合，则还必须替换 getItemId() 和 containsItem()。
    @Override
    public boolean containsItem(long itemId) {
        return super.containsItem(itemId);
    }

    public void updateGames(Game.GameInfo[] games) {
        this.games = games;

    }


}