package com.xueluoanping.arknights.custom.stage;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.resource.dzp;
import com.xueluoanping.arknights.api.tool.ToolTable;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.custom.CenterSpaceImageSpan;
import com.xueluoanping.arknights.custom.Item.ItemModel;
import com.xueluoanping.arknights.custom.TextImageTransformation;
import com.xueluoanping.arknights.pages.GameSettingsActivity;
import com.xueluoanping.arknights.pro.SimpleTool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StageAdapter extends BaseQuickAdapter<StageModel, BaseViewHolder> {


    public StageAdapter(int layoutResId, @Nullable List<StageModel> data) {
        super(layoutResId, data);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, StageModel stageModel) {
        StringBuilder stringBuilder = new StringBuilder();
        String difficulty = stageModel.getDiffGroup().equals("TOUGH") ? "??????" : "??????";
        int pos = stageModel.getDescription().indexOf("\\n<@lv.item><");
        if (pos > -1)
            stringBuilder
                    .append("???")
                    .append(stageModel.getDescription()
                            .substring(0, pos)
                            .replace("\\n", "\n")
                            // .replace("</>", "")
                            // .replace("<@lv.sp>", "")
                            .replaceAll("<.*?>", "")
                    )
                    .append("???");
        else stringBuilder
                .append("?????????");
        // stringBuilder.append("????????????");
        TableRow tr = baseViewHolder.findView(R.id.ic_tr_drops);
        if (tr != null) {
            int itemWidth = 160;
            int itemHeight = 160;
            tr.removeAllViews();
            int paintColor = ToolTheme.getColorValue(getContext(), R.attr.colorPrimary);
            stageModel.displayRewards.forEach(entry -> {
                ImageView view = new ImageView(getContext());
                view.setOnClickListener(v ->
                        Toast.makeText(getContext(), entry.getName0(), Toast.LENGTH_SHORT).show()
                );
                ViewGroup.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.width = itemWidth;
                params.height = itemHeight;
                view.setLayoutParams(params);
                tr.addView(view);
                Glide.with(getContext())
                        .load(dzp.getItemIconUrlById(entry.getIconId()))
                        .apply(new RequestOptions().transform(new TextImageTransformation(entry.getOccPercent(), paintColor, false)))
                        .error(Glide.with(getContext()).load(R.mipmap.pic_holder))
                        .into(view);
                // stringBuilder.append(entry.getName0()).append("???");
            });
        }

        TextView textView = baseViewHolder.findView(R.id.ic_tv_diffGroup);
        if (textView != null) {
            baseViewHolder.setText(R.id.ic_tv_diffGroup, difficulty);
            if (!stageModel.isOpen()) {
                baseViewHolder.setText(R.id.ic_tv_diffGroup, "??????");
                textView.setBackground(getContext().getDrawable(R.drawable.bg_ripple_round_red));
            } else {
                if (difficulty.equals("??????"))
                    textView.setBackground(getContext().getDrawable(R.drawable.bg_ripple_round2));
                else textView.setBackground(getContext().getDrawable(R.drawable.bg_ripple_round3));
            }
            textView.setPadding(10, 0, 10, 0);
        }
        baseViewHolder.setText(R.id.ic_tv_batteleId, "  " + stageModel.getCode() + "???" + stageModel.getName0() + "???");
        // baseViewHolder.setText(R.id.ic_tv_batteleName, stageModel.getName0());
        baseViewHolder.setText(R.id.ic_tv_apCost, stageModel.getApCost() + "");
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
