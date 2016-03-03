package flux;

import android.app.Activity;
import android.app.Fragment;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxFragmentRequester;

/**
 * Created by Bruce-Home on 2016/2/16.
 */
public class Flux {
    public static FluxCore get() {
        return FluxCore.INSTANCE;
    }

    public static final String FLUX_REQUESTER_TAG = "FLUX_REQUESTER_TAG";

    /**
     * no need for activity
     * @param activity
     * @param requesterClz
     * @param tag
     * @param <T>
     * @return
     */
    private static <T extends FluxFragmentRequester> T getRequester(Activity activity, Class<T> requesterClz, String tag) {
        T requester = FluxFragmentRequester.getRequesterOrCreate(activity.getFragmentManager(), requesterClz, tag);
        FluxCore.INSTANCE.register(requester.getReceiverId(), activity);
        return requester;
    }

    public static <T extends FluxFragmentRequester> T getRequester(Activity activity, Class<T> requesterClz) {
        return getRequester(activity, requesterClz, FLUX_REQUESTER_TAG);
    }

    public static <T extends FluxFragmentRequester> T getRequester(Fragment fragment, Class<T> requesterClz, String tag) {
        T requester = FluxFragmentRequester.getRequesterOrCreate(fragment.getFragmentManager(), requesterClz, tag);
        FluxCore.INSTANCE.register(requester.getReceiverId(), fragment);
        return requester;
    }

    public static <T extends FluxFragmentRequester> T getRequester(Fragment fragment, Class<T> requesterClz) {
        return getRequester(fragment, requesterClz, FLUX_REQUESTER_TAG);
    }
}