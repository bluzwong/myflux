package flux;

import com.github.bluzwong.myflux.lib.FluxCore;

/**
 * Created by Bruce-Home on 2016/2/16.
 */
public class Flux {
    public static FluxCore get() {
        return FluxCore.INSTANCE;
    }
}