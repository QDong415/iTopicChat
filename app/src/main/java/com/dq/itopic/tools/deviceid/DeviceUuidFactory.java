package com.dq.itopic.tools.deviceid;/**
 * Created by cyril on 16/1/7.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DeviceUuidFactory {
    protected static final String PREFS_FILE = "dev_id.xml";
    protected static final String DEVICE_UUID_FILE_NAME = ".dev_id.txt";
    protected static final String PREFS_DEVICE_ID = "dev_id";
    protected static final String KEY = "cyril'98";
    protected static UUID uuid;

    private static String recoverDeviceUuidFromSD(Context context) {
        try {
            String dirPath = context.getFilesDir().getAbsolutePath();;
            File dir = new File(dirPath);
            File uuidFile = new File(dir, DEVICE_UUID_FILE_NAME);
            if (!dir.exists() || !uuidFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(uuidFile);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[100];
            int readCount;
            while ((readCount = fileReader.read(buffer)) > 0) {
                sb.append(buffer, 0, readCount);
            }
            //通过UUID.fromString来检查uuid的格式正确性
            UUID uuid = UUID.fromString(EncryptUtils.decryptDES(sb.toString(), KEY));
            return uuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveDeviceUuidToSD(Context context,String uuid) {
        String dirPath = context.getFilesDir().getAbsolutePath();
        File targetFile = new File(dirPath, DEVICE_UUID_FILE_NAME);
        if (targetFile != null) {
            if (targetFile.exists()) {

            } else {
                OutputStreamWriter osw;
                try {
                    osw = new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8");
                    try {
                        osw.write(uuid);
                        osw.flush();
                        osw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public UUID getUuid(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        String recoverDeviceUuidFromSD = recoverDeviceUuidFromSD(context);
                        if (recoverDeviceUuidFromSD != null) {
                            uuid = UUID.fromString(recoverDeviceUuidFromSD);
                        } else {
                            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            try {
                                if (!"9774d56d682e549c".equals(androidId)) {
                                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                                    try {
                                        saveDeviceUuidToSD(context,EncryptUtils.encryptDES(uuid.toString(), KEY));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    uuid = UUID.randomUUID();
                                    try {
                                        saveDeviceUuidToSD(context,EncryptUtils.encryptDES(uuid.toString(), KEY));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (uuid == null){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String time = sdf.format(new Date());
                            final String namekey = time + "-" + (int)(Math.random() * 10000);
                            uuid = UUID.fromString(namekey);
                        }
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                    }
                }
            }
        }
        return uuid;
    }
}

