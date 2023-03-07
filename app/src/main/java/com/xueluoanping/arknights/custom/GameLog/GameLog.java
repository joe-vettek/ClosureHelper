package com.xueluoanping.arknights.custom.GameLog;

public class GameLog {
    private String ts;
    private String info;
    public long ts0;

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getTs() {
        return ts;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    //    关于日志分析，目前设定以下几点
    //    当前状态，下一次基建/作战/收取线索时间
    //    作战统计，代理时长
}
