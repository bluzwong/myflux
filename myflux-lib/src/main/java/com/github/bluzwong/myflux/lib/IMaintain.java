package com.github.bluzwong.myflux.lib;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 */
public interface IMaintain {
     void autoSave(Object obj, SavedData savingData);
    void autoRestore(Object obj, SavedData savedData);
}
