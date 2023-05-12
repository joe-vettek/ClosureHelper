package com.xueluoanping.arknights.custom.GameLog;

import androidx.annotation.ColorRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.pro.spTool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameLogAdapter extends BaseQuickAdapter<GameLog, BaseViewHolder> {
    public GameLogAdapter(int layoutResId, @Nullable List<GameLog> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, GameLog detail) {

        String regex = ".*完成.*|.*战斗结束.*";
        String regex2 = ".*战斗开启成功.*|.*仓库识别.*";
        String regex3 = ".*错误.*|.*失败.*";
        String regex4 = ".*5★.*|.*6★.*";
        String regex5 = ".*计划.*|.*下次.*";

        baseViewHolder.setText(R.id.iv_ts, detail.getTs())
                .setText(R.id.tv_info, detail.getInfo());
        // setColor(baseViewHolder, R.color.limegreen);
        if (detail.getInfo().matches(regex4))
            setColor(baseViewHolder, R.color.goldenrod);
      else  if (detail.getInfo().matches(regex))
            setColor(baseViewHolder, R.color.limegreen);
        else if (detail.getInfo().matches(regex2))
            setColor(baseViewHolder, R.color.darkturquoise);
        else if (detail.getInfo().matches(regex3))
            setColor(baseViewHolder, R.color.darkred);
        else if (detail.getInfo().matches(regex5))
            setColor(baseViewHolder, R.color.pink_pressed);

        // else if (spTool.getTheme() == R.style.AppTheme_NoActionBar_nightTheme)
        //     setColor(baseViewHolder, R.color.colorText);
    }

    private void setColor(BaseViewHolder baseViewHolder, @ColorRes int limegreen) {
        baseViewHolder.setTextColor(R.id.iv_ts, getContext().getColor(limegreen))
                .setTextColor(R.id.tv_info, getContext().getColor(limegreen));
    }
}
