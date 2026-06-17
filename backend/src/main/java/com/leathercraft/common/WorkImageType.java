package com.leathercraft.common;

public class WorkImageType {
    public static final int FINISHED = 1;
    public static final int PROCESS = 2;
    public static final int DETAIL = 3;

    public static boolean isValid(int type) {
        return type == FINISHED || type == PROCESS || type == DETAIL;
    }

    public static String getName(int type) {
        switch (type) {
            case FINISHED:
                return "成品图";
            case PROCESS:
                return "过程图";
            case DETAIL:
                return "细节图";
            default:
                return "未知";
        }
    }
}
