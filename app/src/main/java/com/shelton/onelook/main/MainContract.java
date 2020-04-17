package com.shelton.onelook.main;

import com.shelton.onelook.base.BaseModel;
import com.shelton.onelook.base.BasePresenter;
import com.shelton.onelook.base.BaseView;
import com.shelton.onelook.bean.WeatherInfoBean;

import io.reactivex.Observable;

public interface MainContract {
    interface View extends BaseView {
        void showWeatherInfo(WeatherInfoBean weatherInfoBean);
    }

    interface Model extends BaseModel {
        Observable<WeatherInfoBean> getWeatherInfo(String city);
    }

    interface Presenter extends BasePresenter {
        void getWeatherInfo(String city);
    }
}
