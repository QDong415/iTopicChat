package com.dq.itopic.activity.chat.call.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.AppOpsManagerCompat;

import com.dq.itopic.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PermissionCheckUtil {

    @TargetApi(19)
    public static boolean canDrawOverlays(final Context context, boolean needOpenPermissionSetting) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                boolean booleanValue = (Boolean)Settings.class.getDeclaredMethod("canDrawOverlays", Context.class).invoke((Object)null, context);
                if (!booleanValue && needOpenPermissionSetting) {
                    ArrayList<String> permissionList = new ArrayList();
                    permissionList.add("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                    showPermissionAlert(context, context.getString(R.string.rc_permission_grant_needed) + getNotGrantedPermissionMsg(context, permissionList), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (-1 == which) {
                                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }

                        }
                    });
                }

                return booleanValue;
            } catch (Exception var7) {
                return true;
            }
        } else if (Build.VERSION.SDK_INT < 19) {
            return true;
        } else {
            Object systemService = context.getSystemService(Context.APP_OPS_SERVICE);

            Method method;
            try {
                method = Class.forName("android.app.AppOpsManager").getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
            } catch (NoSuchMethodException var9) {
                method = null;
            } catch (ClassNotFoundException var10) {
                method = null;
            }

            if (method != null) {
                try {
                    Integer tmp = (Integer)method.invoke(systemService, 24, context.getApplicationInfo().uid, context.getPackageName());
                    result = tmp == 0;
                } catch (Exception var8) {
                }
            }

            return result;
        }
    }

    @TargetApi(11)
    private static void showPermissionAlert(Context context, String content, DialogInterface.OnClickListener listener) {
        (new AlertDialog.Builder(context)).setMessage(content).setPositiveButton(R.string.rc_confirm, listener).setNegativeButton(R.string.rc_cancel, listener).setCancelable(false).create().show();
    }

    public static String getNotGrantedPermissionMsg(Context context, String[] permissions, int[] grantResults) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.rc_permission_grant_needed));
//        sb.append("(");
//
//        for(int i = 0; i < permissions.length; ++i) {
//            if (grantResults[i] == -1) {
//                String permissionName = context.getString(context.getResources().getIdentifier("rc_" + permissions[i], "string", context.getPackageName()), new Object[]{0});
//                sb.append(permissionName);
//                if (i != permissions.length - 1) {
//                    sb.append(" ");
//                }
//            }
//        }
//
//        sb.append(")");
        return sb.toString();
    }


    private static String getNotGrantedPermissionMsg(Context context, List<String> permissions) {
        Set<String> permissionsValue = new HashSet();
        Iterator var4 = permissions.iterator();

        while(var4.hasNext()) {
            String permission = (String)var4.next();
            String permissionValue = context.getString(context.getResources().getIdentifier("rc_" + permission, "string", context.getPackageName()), new Object[]{0});
            permissionsValue.add(permissionValue);
        }

        String result = "(";

        String value;
        for(Iterator var8 = permissionsValue.iterator(); var8.hasNext(); result = result + value + " ") {
            value = (String)var8.next();
        }

        result = result.trim() + ")";
        return result;
    }


    private static boolean isFlyme() {
        String osString = "";

        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            osString = (String)get.invoke(clz, "ro.build.display.id", "");
        } catch (Exception var3) {
        }

        return osString.toLowerCase().contains("flyme");
    }

    public static boolean checkPermissions(Context context, @NonNull String[] permissions) {
        if (permissions != null && permissions.length != 0) {
            String[] var2 = permissions;
            int var3 = permissions.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String permission = var2[var4];
                if (!hasPermission(context, permission)) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private static boolean hasPermission(Context context, String permission) {
        String opStr = AppOpsManagerCompat.permissionToOp(permission);
        if (opStr == null) {
            return true;
        } else {
            return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void showRequestPermissionFailedAlter(final Context context, String content) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case -1:
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        Uri uri = Uri.fromParts("package", context.getPackageName(), (String)null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    case -2:
                    default:
                }
            }
        };
        (new AlertDialog.Builder(context)).setMessage(content).setPositiveButton(R.string.rc_confirm, listener).setNegativeButton(R.string.rc_cancel, listener).setCancelable(false).create().show();
    }
}
