package gs.zenodotus.back;

import android.util.Log;

import java.lang.ref.WeakReference;

import gs.zenodotus.front.SplashScreenActivity;

public class GetCapabilitiesCommand extends Command {
    private WeakReference<SplashScreenActivity> hostActivity;

    public GetCapabilitiesCommand(SplashScreenActivity splashScreenActivity) {
        Log.v("Zenodot", "create new get capabilities");
        this.hostActivity =
            new WeakReference<SplashScreenActivity>(splashScreenActivity);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (GlobalDataProvider.areCapabillitiesActual(hostActivity.get())) {
            return null;
            // TODO some sqlite preparation?
        } else {
            DataFactory dataFactory = GlobalDataProvider.getFactory();
//            dataFactory.getCapabilities();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void arg) {
        super.onPostExecute(arg);
        this.hostActivity.get().onGetCapabilitiesSuccess();
    }
}
