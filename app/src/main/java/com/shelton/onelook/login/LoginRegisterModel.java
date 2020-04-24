package com.shelton.onelook.login;

import com.shelton.onelook.login.User.UserDB;
import com.shelton.onelook.login.User.UserData;
import com.shelton.onelook.util.HttpUtil;

import java.net.URLEncoder;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginRegisterModel implements LoginRegisterContract.Model {

    private OkHttpClient okHttpClient;
    private UserDB ud = new UserDB();

    LoginRegisterModel() {
        okHttpClient = HttpUtil.getHttpClient();
    }

    @Override
    public Observable<String> login(final String account, final String oldPassword) {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
//                String path = "http://36078d58.nat123.cc/AndroidRegisterAndLogin_war/login";
//                Map<String, String> params = new HashMap<>();
//                params.put("username", account);
//                params.put("password", oldPassword);
//
//                ResponseBody responseBody = executeHttp(path, params);
//                if (responseBody != null) {
//                    String result = responseBody.string().replaceAll("(\r\n|\r|\n|\n\r)", "");
//                    emitter.onNext(result);
//                    responseBody.close();
//                } else {
//                    emitter.onError(new Throwable("响应体为空"));
//                }
            UserData user = new UserData();
            user.setUserName(account);
            user.setPassword(oldPassword);
            if (ud.myLogin(user)) {
                emitter.onNext("登录成功");
            } else {
                emitter.onError(new Throwable("登录失败"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> register(final String account, final String oldPassword, final String newPassword) {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
//                String path = "http://36078d58.nat123.cc/AndroidRegisterAndLogin_war/register";
//                Map<String, String> params = new HashMap<>();
//                params.put("username", account);
//                params.put("password", oldPassword);
//                params.put("newPassword", newPassword);
//
//                ResponseBody responseBody = executeHttp(path, params);
//                if (responseBody != null) {
//                    String result = responseBody.string().replaceAll("(\r\n|\r|\n|\n\r)", "");
//                    emitter.onNext(result);
//                    responseBody.close();
//                } else {
//                    emitter.onError(new Throwable("响应体为空"));
//                }
            if (oldPassword.equals(newPassword)) {
                UserData user = new UserData();
                user.setUserName(account);
                user.setPassword(oldPassword);
                if (ud.myRegister(user)) {
                    emitter.onNext("注册成功");
                } else {
                    emitter.onError(new Throwable("注册失败"));
                }
            } else {
                emitter.onError(new Throwable("密码输入不一致"));
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> modifyPassword(final String account, final String oldPassword, final String newPassword) {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
//                String path = "http://36078d58.nat123.cc/AndroidRegisterAndLogin_war/modify";
//                Map<String, String> params = new HashMap<>();
//                params.put("username", account);
//                params.put("password", oldPassword);
//                params.put("newPassword", newPassword);
//
//                ResponseBody responseBody = executeHttp(path, params);
//                if (responseBody != null) {
//                    String result = responseBody.string().replaceAll("(\r\n|\r|\n|\n\r)", "");
//                    emitter.onNext(result);
//                    responseBody.close();
//                } else {
//                    emitter.onError(new Throwable("响应体为空"));
//                }
            UserData user = new UserData();
            user.setUserName(account);
            user.setPassword(oldPassword);
            if (ud.myModifyPassword(user, newPassword)) {
                emitter.onNext("修改密码成功");
            } else {
                emitter.onError(new Throwable("修改密码失败"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private ResponseBody executeHttp(String path, Map<String, String> params) throws Exception {
        StringBuilder url = new StringBuilder(path);
        url.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=");
            url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            url.append("&");
        }
        url.deleteCharAt(url.length() - 1);
        Request request = new Request.Builder()
                .url(url.toString())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body();
    }
}
