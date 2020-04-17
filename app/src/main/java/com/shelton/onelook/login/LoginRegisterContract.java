package com.shelton.onelook.login;

import com.shelton.onelook.base.BaseModel;
import com.shelton.onelook.base.BasePresenter;
import com.shelton.onelook.base.BaseView;

import io.reactivex.Observable;

public interface LoginRegisterContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void login(String account, String oldPassword);

        void register(String account, String oldPassword, String newPassword);

        void modifyPassword(String account, String oldPassword, String newPassword);
    }

    interface Model extends BaseModel {
        Observable<String> login(String account, String oldPassword);

        Observable<String> register(String account, String oldPassword, String newPassword);

        Observable<String> modifyPassword(String account, String oldPassword, String newPassword);
    }
}