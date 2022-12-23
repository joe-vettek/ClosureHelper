package com.xueluoanping.arknights.custom.Item;


import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xueluoanping.arknights.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class itemAdapterSpecial extends BaseQuickAdapter<ItemModel, BaseViewHolder> {
    private static final String TAG = "songAdapter";

    public itemAdapterSpecial(int layoutResId, @Nullable List<ItemModel> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NotNull final BaseViewHolder baseViewHolder, final ItemModel Item) {

        baseViewHolder.setText(R.id.tv_ItemDescription, Item.text);

        ImageView dialogTV = baseViewHolder.getView(R.id.iv_ItemIcon);
        Glide.with(getContext())
                .load("http://ak.dzp.me/dst/items/"+Item.key+".webp")
                .into(dialogTV);
    }


}
