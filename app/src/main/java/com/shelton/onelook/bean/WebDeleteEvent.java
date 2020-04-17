package com.shelton.onelook.bean;

public class WebDeleteEvent {
    private int viewTop;
    public WebDeleteEvent(int top){
        viewTop=top;
    }
    public int getViewTop(){
        return viewTop;
    }
}
