package com.github.bluzwang.myflux.example

import com.github.bluzwong.myflux.lib.FluxResponse
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType

/**
 * Created by Bruce-Home on 2016/2/5.
 */
class KotlinActivity {

    @ReceiveType(type = arrayOf("2"))
    fun ccf_miku(response:FluxResponse) {
        "aaa"
    }
}