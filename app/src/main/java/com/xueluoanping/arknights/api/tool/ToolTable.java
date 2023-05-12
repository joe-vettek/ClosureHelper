package com.xueluoanping.arknights.api.tool;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.api.resource.penguin_stats;

public class ToolTable {
    private String TAG = ToolTable.class.getSimpleName();

    public static ToolTable getInstance() {
        return instance;
    }

    public static void initInstance() {
        new Thread(() -> {
            instance = new ToolTable();
        }).start();
    }

    private static ToolTable instance;
    // private JsonObject CharacterTable;
    private JsonObject ItemTable;
    private JsonObject StageTable;
    private JsonObject StageValidInfoTable;
    private JsonArray MatrixTable;

    private JsonObject ItemNameTable;

    private boolean isUpdating = false;

    private ToolTable() {
        this.isUpdating = true;

        // this.CharacterTable = loadCharacterTable();
        this.ItemTable = loadItemTable();
        this.ItemNameTable = loadItemNameTable();

        JsonObject stage_table = loadDataTable("data/stage_table.json");
        this.StageTable = loadStageTable_Base(stage_table);
        this.StageValidInfoTable = loadStageValidInfoTable(stage_table);


        try {
            this.MatrixTable = loadMatrixTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.isUpdating = false;
    }


    // 仅仅统计主要的
    public boolean hasCompleteInit() {
        return this.ItemTable != null
                && this.StageTable != null
                && this.ItemNameTable != null;
    }


    public boolean isUpdating() {
        return this.isUpdating;
    }

    private JsonObject loadDataTable(String name) {
        try {
            Gson g = new Gson();
            JsonObject j = g.fromJson(ToolFile.getTextFile(name), JsonObject.class);
            // Log.d(TAG, "loadDataTable: "+j);
            return j;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private JsonObject loadItemTable() {
        JsonObject j = loadDataTable("data/item_table.json");
        if (j != null && j.has("items")) {
            return j.getAsJsonObject("items");
        } else return null;
    }

    private JsonObject loadStageTable_Base(JsonObject j) {
        if (j != null && j.has("stages")) {
            return j.getAsJsonObject("stages");
        } else return null;
    }

    private JsonObject loadStageValidInfoTable(JsonObject j) {
        if (j != null && j.has("stageValidInfo")) {
            return j.getAsJsonObject("stageValidInfo");
        } else return null;
    }

    private JsonArray loadMatrixTable() {
        JsonObject j = loadDataTable(penguin_stats.fileName_matrixAll);
        if (j != null && j.has("matrix")) {
            return j.getAsJsonArray("matrix");
        } else return null;
    }

    public void updateMatrixTable() {
        this.MatrixTable = loadMatrixTable();
    }

    public void updateStageTable() {
        this.StageTable = loadStageTable_Base(loadDataTable("data/stage_table.json"));
    }

    public void updateItemTable() {
        this.ItemTable = loadItemTable();
    }

    public void updateItemNameTable() {
        this.ItemNameTable = loadItemNameTable();
    }

    private JsonObject loadItemNameTable() {
        return loadDataTable("data/item_name_table.json");
    }

    // public JsonObject getCharacterTable() {
    //     return CharacterTable;
    // }

    public JsonObject getItemTable() {
        return ItemTable;
    }

    public JsonObject getStageTable() {
        return StageTable;
    }

    public JsonObject getStageValidInfoTable() {
        return StageValidInfoTable;
    }

    public JsonObject getItemNameTable() {
        return ItemNameTable;
    }

    public JsonArray getMatrixTable() {
        return MatrixTable;
    }
}
