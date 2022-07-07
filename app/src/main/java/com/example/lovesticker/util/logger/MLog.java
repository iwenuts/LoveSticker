package com.example.lovesticker.util.logger;

import com.orhanobut.logger.Logger;

public class MLog {
    public static void logLifeStateA(String msg) {
        Logger.log(Logger.DEBUG,"##ActivityState",msg,null);
    }

    public static void logLifeStateF(String msg) {
        Logger.log(Logger.DEBUG,"##FragmentState",msg,null);
    }


    public static void logI(String msg) {
        Logger.log(Logger.INFO,"##Info","###" + msg,null);
    }

    public static void logW(String msg) {
        Logger.log(Logger.WARN,"##Warn","###" + msg,null);
    }

    public static void logE(String msg) {
        Logger.log(Logger.ERROR,"##Error","###" + msg,null);
    }

}
