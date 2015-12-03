package cn.andrewlu.app.securediary.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * Created by andrewlu on 2015/11/13.
 */
public class LocationUtil implements AMapLocationListener, AMapLocalWeatherListener {
    private LocationUtil(Context context) {
        locationManagerProxy = LocationManagerProxy.getInstance(context);
        refresh();
    }

    private static LocationUtil _instance = null;

    private LocationManagerProxy locationManagerProxy = null;
    private AMapLocation mLastLocation = null;
    private AMapLocalWeatherLive mWeatherLive = null;

    public static void init(Context context) {
        _instance = new LocationUtil(context);
    }

    public static LocationUtil getInstance() {
        return _instance;
    }

    public AMapLocation getLastLocation() {
        return mLastLocation;
    }

    public AMapLocalWeatherLive getWeatherLive() {
        return mWeatherLive;
    }

    public void refresh() {
        locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 30 * 1000, 150, this);
        locationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
    }

    public interface OnLocationChangeListener {
        void onLocationChanged(AMapLocation aMapLocation);
    }

    public interface OnWeatherUpdateListener {
        void onWeatherUpdated(AMapLocalWeatherLive weatherLive);
    }

    private OnLocationChangeListener mLocationListener = null;
    private OnWeatherUpdateListener mWeatherUpdateListener = null;

    public void setOnLocationChangeListener(OnLocationChangeListener l) {
        mLocationListener = l;
        if (mLocationListener != null && mLastLocation != null) {
            mLocationListener.onLocationChanged(mLastLocation);
        }
    }

    public void setOnWeatherUpdateListener(OnWeatherUpdateListener l) {
        mWeatherUpdateListener = l;
        if (mWeatherUpdateListener != null && mWeatherLive != null) {
            mWeatherUpdateListener.onWeatherUpdated(mWeatherLive);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLastLocation = aMapLocation;
        if (mLocationListener != null) {
            mLocationListener.onLocationChanged(mLastLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        mWeatherLive = aMapLocalWeatherLive;
        if (mWeatherUpdateListener != null) {
            mWeatherUpdateListener.onWeatherUpdated(mWeatherLive);
        }
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

    }
}
