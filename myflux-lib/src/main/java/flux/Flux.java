package flux;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxFragmentRequester;

import java.util.UUID;

/**
 * Created by Bruce-Home on 2016/2/16.
 */
public class Flux {
    public static FluxCore get() {
        return FluxCore.INSTANCE;
    }

    public static final String FLUX_REQUESTER_TAG = "$FLUX_REQUESTER_TAG$";

    /**
     * no need for activity
     */
    private static <T extends FluxFragmentRequester> T getRequester(Activity activity, Class<T> requesterClz, String tag) {
        T requester = FluxFragmentRequester.getRequesterOrCreate(activity.getFragmentManager(), requesterClz, tag);
        FluxCore.INSTANCE.register(requester.getReceiverId(), activity);
        return requester;
    }

    /**
     * for activity
     */
    public static <T extends FluxFragmentRequester> T getRequester(Activity activity, Class<T> requesterClz) {
        return getRequester(activity, requesterClz, FLUX_REQUESTER_TAG);
    }



    /**
     * for only fragment in activity
     */
    public static <T extends FluxFragmentRequester> T getRequester(Fragment fragment, Class<T> requesterClz) {
        return getRequester(fragment, requesterClz, FLUX_REQUESTER_TAG);
    }

    /**
     * for multi fragment with unique tag in activity
     */
    public static <T extends FluxFragmentRequester> T getRequester(Fragment fragment, Class<T> requesterClz, String tag) {
        T requester = FluxFragmentRequester.getRequesterOrCreate(fragment.getFragmentManager(), requesterClz, tag);
        FluxCore.INSTANCE.register(requester.getReceiverId(), fragment);
        return requester;
    }

    /**
     * for multi fragment with auto tag in activity
     */
    public static <T extends FluxFragmentRequester> T getRequester(Fragment fragment, Class<T> requesterClz, Bundle bundle) {
        String TAG = UUID.randomUUID().toString();
        if (bundle != null) {
            TAG = bundle.getString(FLUX_REQUESTER_TAG, TAG);
        }
        return getRequester(fragment, requesterClz, TAG);
    }

    /**
     * for multi fragment with auto tag in activity
     * call this at Fragment.onSaveInstanceState()
     */
    public static void fluxOnSaveInstanceState(FluxFragmentRequester requester, Bundle bundle) {
        bundle.putString(FLUX_REQUESTER_TAG, requester.getTag());
    }
}