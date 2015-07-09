package gs.zenodotus.back.commands;

import android.util.Log;

import java.lang.ref.WeakReference;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.front.SplashScreenActivity;

public class GetCapabilitiesCommand extends Command {
    private WeakReference<SplashScreenActivity> hostActivity;

    public GetCapabilitiesCommand(SplashScreenActivity splashScreenActivity) {
        Log.v("Zenodot", "create new get capabilities");
        this.hostActivity = new WeakReference<>(splashScreenActivity);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (GlobalDataProvider.areCapabillitiesActual(hostActivity.get())) {
            return null;
        } else {
            DataFactory dataFactory = GlobalDataProvider.getFactory();
//            Log.v("XmlNode", parsedCapabilities.getName());
            dataFactory.storeCapabilitiesInDb();
            // TODO maybe actualization should moved somewhere else?
            GlobalDataProvider.setCapabilitiesActual(hostActivity.get());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void arg) {
        super.onPostExecute(arg);
        this.hostActivity.get().onGetCapabilitiesSuccess();
    }
}
