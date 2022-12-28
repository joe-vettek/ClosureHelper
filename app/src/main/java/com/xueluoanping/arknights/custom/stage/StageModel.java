package com.xueluoanping.arknights.custom.stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StageModel implements Serializable {
    private String stageId = "";


    private String code = "";
    private String name = "";
    private String description = "";
    private int apCost = 0;
    private String diffGroup = "";
    public ArrayList<Reward> displayRewards = new ArrayList<>();
    private boolean isSelected = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getName0() {
        return name;
    }

    public void setName0(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // public ArrayList<Map.Entry<String, String>> getDisplayRewards() {
    //     return displayRewards;
    // }
    //
    // public void setDisplayRewards(ArrayList<Map.Entry<String, String>> displayRewards) {
    //     this.displayRewards = displayRewards;
    // }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getApCost() {
        return apCost;
    }

    public void setApCost(int apCost) {
        this.apCost = apCost;
    }

    public String getDiffGroup() {
        return diffGroup;
    }

    public void setDiffGroup(String diffGroup) {
        this.diffGroup = diffGroup;
    }


    public boolean containsDrop(String regex) {
        AtomicBoolean result = new AtomicBoolean(false);
        this.displayRewards.forEach(reward -> {
            if (reward.getName0().toLowerCase().contains(regex)) {
                result.set(true);
            }

        });
        return result.get();
    }


    @Override
    public String toString() {
        return this.code + "(" + this.getName0() + ")；";
    }

    public static class Reward implements Serializable {
        private String itemId;
        private String name;
        private String iconId;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getName0() {
            return name;
        }

        public void setName0(String name) {
            this.name = name;
        }

        public String getIconId() {
            return iconId;
        }

        public void setIconId(String iconId) {
            this.iconId = iconId;
        }

    }


}
