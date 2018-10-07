package com.rhino.ui.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Set;

/**
 * <p>The utils of SharedPreferences</p>
 *
 * @author LuoLin
 * @since Create on 2018/10/7.
 **/
public class SharedPreferencesUtils {

    private final static String DEFAULT_SHARE_PREFERENCES_FILE_NAME = "share_preferences";
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private static SharedPreferencesUtils instance;
    public static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtils();
                }
            }
        }
        return instance;
    }

    private SharedPreferencesUtils() {
    }

    public void init(Context context) {
        this.init(context, DEFAULT_SHARE_PREFERENCES_FILE_NAME);
    }

    public void init(Context context, String sharePreferencesFileName) {
        this.mContext = context.getApplicationContext();
        this.mSharedPreferences = mContext.getSharedPreferences(sharePreferencesFileName, Context.MODE_PRIVATE);
    }

    public void put(String key, boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putBoolean(key, value);
            edit.commit();
        }
    }

    public void put(String key, String value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putString(key, value);
            edit.commit();
        }
    }

    public void put(String key, int value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putInt(key, value);
            edit.commit();
        }
    }

    public void put(String key, float value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putFloat(key, value);
            edit.commit();
        }
    }

    public void put(String key, long value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putLong(key, value);
            edit.commit();
        }
    }

    public void put(String key, Set<String> value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (edit != null) {
            edit.putStringSet(key, value);
            edit.commit();
        }
    }

    public void put(String key, Object object) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            if (edit != null) {
                edit.putString(key, objectVal);
                edit.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String get(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public String get(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public int get(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public float get(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public long get(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public Set<String> get(String key, Set<String> defValue) {
        return mSharedPreferences.getStringSet(key, defValue);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> cls) {
        if (mSharedPreferences.contains(key)) {
            String objectVal = mSharedPreferences.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;
            try {
                bais = new ByteArrayInputStream(buffer);
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void remove(String key) {
        mSharedPreferences.edit().remove(key).commit();
    }

    public void clearAll() {
        mSharedPreferences.edit().clear().commit();
    }


}
