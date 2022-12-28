package com.xueluoanping.arknights.custom.stage;

import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.custom.CenterSpaceImageSpan;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.pages.GameSettingsActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StageAdapter extends BaseQuickAdapter<StageModel, BaseViewHolder> {

    public StageAdapter(int layoutResId, @Nullable List<StageModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, StageModel stageModel) {
        // LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        // View view=layoutInflater.inflate(R.layout.cell_normal,parent,false);

        //简单点写就是
        // TextView view1=LayoutInflater.from(getContext()).inflate(R.layout.ic_battle,parent,false);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append(stageModel.getCode()).append("(").append(stageModel.getName0()).append(")")
                .append("  <理智消耗：").append(stageModel.getApCost()).append(">");
        int pos = stageModel.getDescription().indexOf("\\n<@lv.item><");
        if (pos > -1)
            stringBuilder
                    .append("\n【")
                    .append(stageModel.getDescription()
                            .substring(0, pos)
                            .replace("\\n", "\n")
                            // .replace("</>", "")
                            // .replace("<@lv.sp>", "")
                            .replaceAll("<.*?>", "")
                    )
                    .append("】");
        stringBuilder.append("\n掉落物：");
        //  TextView tv=baseViewHolder.findView(R.id.tv_main);
        //
        //  SpannableStringBuilder spannableString = new SpannableStringBuilder(stageModel.getName0());
        //  RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
        // LinearLayout linearLayout= baseViewHolder.findView(R.id.ll_stage);

        stageModel.displayRewards.forEach(entry -> {
            // ImageView view=new ImageView(getContext());
            // linearLayout.addView(view);
            // Glide.with(getContext())
            //         .load("http://ak.dzp.me/dst/items/"+stageModel.displayRewards.get(0).getIconId()+".webp")
            //         .into(view);
            stringBuilder.append(entry.getName0()).append("；");
        });
        stringBuilder
                .append("\n");
        baseViewHolder.setText(R.id.tv_main, stringBuilder);

        if (stageModel.isSelected())
            baseViewHolder.setVisible(R.id.iv_right, true);
        else
            baseViewHolder.setVisible(R.id.iv_right, false);

        // baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        //     private long cacheTime = 0;
        //
        //     @Override
        //     public void onClick(View v) {
        //
        //         // if (GameSettingsActivity.isAllowMultipleSelection())
        //         {
        //             stageModel.setSelected(!stageModel.isSelected());
        //             // notifyItemChanged(baseViewHolder.getLayoutPosition());
        //             if (stageModel.isSelected())
        //                 baseViewHolder.setVisible(R.id.iv_right, true);
        //             else
        //                 baseViewHolder.setVisible(R.id.iv_right, false);
        //
        //         }
        //     }
        // });
    }
}
