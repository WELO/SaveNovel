package com.welo.savenovel.presentation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Iterator;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

public class SharedPrefMgr
{
	/**
	 * For saving int data type.
	 */
	public static void saveSharedPref(Context context, String key, int value, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.putInt(key, value);
		editor.apply();
	}

	/**
	 * For saving long data type.
	 */
	public static void saveSharedPref(Context context, String key, long value, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.putLong(key, value);
		editor.apply();
	}

	/**
	 * For saving boolean data type.
	 */
	public static void saveSharedPref(Context context, String key, boolean value, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	/**
	 * For saving String data type.
	 */
	public static void saveSharedPref(Context context, String key, String value, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.putString(key, value);
		editor.apply();
	}

	/**
	 * For saving bundle object.
	 */
	public static void saveSharedPref(Context context, String key, Bundle bundle, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();

		Object object;
		for(String secondKey : bundle.keySet())
		{
			object = bundle.get(secondKey);

			if(object instanceof Integer)
				saveSharedPref(context, key + "_" + secondKey, (Integer) object, category);
			else if(object instanceof Long)
				saveSharedPref(context, key + "_" + secondKey, (Long) object, category);
			else if(object instanceof Boolean)
				saveSharedPref(context, key + "_" + secondKey, (Boolean) object, category);
			else if(object instanceof String)
				saveSharedPref(context, key + "_" + secondKey, (String) object, category);
		}

		editor.apply();
	}

	/**
	 * For loading int data type.
	 */
	public static int loadSharedPref(Context context, String key, int value, String category)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		return sharedPref.getInt(key, value);
	}

	/**
	 * For loading long data type.
	 */
	public static long loadSharedPref(Context context, String key, long value, String category)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		return sharedPref.getLong(key, value);
	}

	/**
	 * For loading boolean data type.
	 */
	public static boolean loadSharedPref(Context context, String key, boolean value, String category)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		return sharedPref.getBoolean(key, value);
	}

	/**
	 * For loading String data type.
	 */
	public static String loadSharedPref(Context context, String key, String value, String category)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		return sharedPref.getString(key, value);
	}

	/**
	 * For loading bundle object.
	 */
	public static Bundle loadSharedPref(Context context, String key, String category)
	{
		Bundle bundle = new Bundle();

		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		@SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) sharedPref.getAll();
		Iterator<String> iterator = map.keySet().iterator();

		Object object;
		while(iterator.hasNext())
		{
			String temp = iterator.next();
			if(temp.startsWith(key))
			{
				String secondKey = temp.split("_")[1];
				object = map.get(temp);

				if(object instanceof Integer)
					bundle.putInt(secondKey, (Integer) object);
				else if(object instanceof Long)
					bundle.putLong(secondKey, (Long) object);
				else if(object instanceof Boolean)
					bundle.putBoolean(secondKey, (Boolean) object);
				else if(object instanceof String)
					bundle.putString(secondKey, (String) object);
			}
		}

		return bundle;
	}

	/**
	 * Remove the specific pair by key
	 */
	public static void removeSharedPref(Context context, String key, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.remove(key);
		editor.apply();
	}

	/**
	 * Clear all sharedPreference of specific file
	 */
	public static void clearSharedPref(Context context, String category)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(category, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.apply();
	}

	/**
	 * For loading int data type with Flowable.
	 */
	public static Flowable<Integer> loadSharedPrefListener(Context context, String searchKey, int value, String category) {
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		PublishProcessor<Integer> publishProcessor = PublishProcessor.create();
		sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(searchKey.equals(key)){
                publishProcessor.onNext(loadSharedPref(context,key,value,category));
            }
        });
		return publishProcessor;
	}

	/**
	 * For loading long data type with Flowable.
	 */
	public static Flowable<Long> loadSharedPrefListener(Context context, String searchKey, long value, String category) {
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		PublishProcessor<Long> publishProcessor = PublishProcessor.create();
		sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(searchKey.equals(key)){
                publishProcessor.onNext(loadSharedPref(context,key,value,category));
            }
        });
		return publishProcessor;
	}

	/**
	 * For loading boolean data type with Flowable.
	 */
	public static Flowable<Boolean> loadSharedPrefListener(Context context, String searchKey, boolean value, String category) {
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		PublishProcessor<Boolean> publishProcessor = PublishProcessor.create();
		sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(searchKey.equals(key)){
                publishProcessor.onNext(loadSharedPref(context,key,value,category));
            }
        });
		return publishProcessor;
	}

	/**
	 * For loading String data type with Flowable.
	 */
	public static Flowable<String> loadSharedPrefListener(Context context, String searchKey, String value, String category) {
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		PublishProcessor<String> publishProcessor = PublishProcessor.create();
		sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(searchKey.equals(key)){
                publishProcessor.onNext(loadSharedPref(context,key,value,category));
            }
        });
		return publishProcessor;
	}

	/**
	 * For loading bundle object with Flowable.
	 */
	public static Flowable<Bundle> loadSharedPrefListener(Context context, String searchKey, String category)
	{
		SharedPreferences sharedPref = context.getSharedPreferences(category, Context.MODE_PRIVATE);
		PublishProcessor<Bundle> publishProcessor = PublishProcessor.create();
		sharedPref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if(searchKey.equals(key)){
                publishProcessor.onNext(loadSharedPref(context,key,category));
            }
        });
		return publishProcessor;
	}
}