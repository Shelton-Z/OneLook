package com.shelton.onelook.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class User {

    static class UserData implements Serializable {
        private String userName;        //用户名
        private String password;        //密码

        String getUserName() {
            return userName;
        }

        void setUserName(String userName) {
            this.userName = userName;
        }

        String getPassword() {
            return password;
        }

        void setPassword(String password) {
            this.password = password;
        }
    }

    static class UserDB {
        static List<UserData> lists = new ArrayList<>();
        static UserData administrator = new UserData();
        //获取用户数据
        static List<UserData> users = UserDB.lists;

        static {
            administrator.setUserName("admin");
            administrator.setPassword("admin");
            lists.add(administrator);
        }

        //用户登录
        boolean myLogin(UserData user) {
            for (UserData tmp : users) {
                if (user.getUserName().equals(tmp.getUserName()) &&
                        user.getPassword().equals(tmp.getPassword())) {
                    return true;
                }
            }
            return false;
        }

        //注册用户
        boolean myRegister(UserData user) {
            for (UserData tmp : users) {
                if (user.getUserName().equals(tmp.getUserName())) {
                    return false;
                }
            }
            users.add(user);
            return true;
        }

        //修改密码
        boolean myModifyPassword(UserData user, String newPassword) {
            for (UserData tmp : users) {
                if (user.getUserName().equals(tmp.getUserName()) &&
                        user.getPassword().equals(tmp.getPassword())) {
                    tmp.setPassword(newPassword);
                    return true;
                }
            }
            return false;
        }
    }

//
//    public void setDataList(String tag, List<UserData> lists) {
//        if (null == lists || lists.size() <= 0)
//            return;
//        Gson gson = new Gson();
//        //转换成json数据，再保存
//        String strJson = gson.toJson(lists);
//        editor.clear();
//        editor.putString(tag, strJson);
//        editor.commit();
//    }
//
//    public List<UserData> getDataList(String tag, SharedPreferences preferences) {
//        List<UserData> lists = new ArrayList<>();
//        String strJson = preferences.getString(tag, null);
//        if (null == strJson) {
//            return lists;
//        }
//        Gson gson = new Gson();
//        lists = gson.fromJson(strJson, new TypeToken<List<UserData>>() {
//        }.getType());
//        return lists;
//    }


}





