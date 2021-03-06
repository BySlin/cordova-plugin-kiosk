package jk.cordova.plugin.kiosk;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.Settings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import java.util.HashSet;

public class KioskPlugin extends CordovaPlugin {

    private static final String EXIT_KIOSK = "exitKiosk";
    private static final String IS_IN_KIOSK = "isInKiosk";
    private static final String IS_SET_AS_LAUNCHER = "isSetAsLauncher";
    private static final String SET_ALLOWED_KEYS = "setAllowedKeys";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if (IS_IN_KIOSK.equals(action)) {

                callbackContext.success(Boolean.toString(KioskActivity.running));
                return true;

            } else if (IS_SET_AS_LAUNCHER.equals(action)) {

                String myPackage = cordova.getActivity().getApplicationContext().getPackageName();
                callbackContext.success(Boolean.toString(myPackage.equals(findLauncherPackageName())));
                return true;

            } else if (EXIT_KIOSK.equals(action)) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent chooser = Intent.createChooser(intent, "Select destination...");
                if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                    cordova.getActivity().startActivity(chooser);
                }
//                cordova.getActivity().startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));

                callbackContext.success();
                return true;

            } else if (SET_ALLOWED_KEYS.equals(action)) {

                System.out.println("setAllowedKeys: " + args.toString());
                HashSet<Integer> allowedKeys = new HashSet<>();
                for (int i = 0; i < args.length(); i++) {
                    allowedKeys.add(args.optInt(i));
                }
                KioskActivity.allowedKeys = allowedKeys;

                callbackContext.success();
                return true;
            }
            callbackContext.error("Invalid action");
            return false;
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }

    private String findLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = this.cordova.getActivity().getPackageManager().resolveActivity(intent, 0);
        return res.activityInfo.packageName;
    }
}

