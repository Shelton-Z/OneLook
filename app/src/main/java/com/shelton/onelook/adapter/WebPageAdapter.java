package com.shelton.onelook.adapter;

import com.shelton.onelook.main.WebViewFragment;
import com.shelton.onelook.util.WebPageHelper;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WebPageAdapter extends FragmentPagerAdapter {
    //private FragmentManager fm;
    //public static final int ADD_PAGE=0;
    //public static final int DELETE_PAGE=1;
    //private int deleteItem=-1,notifyType=1;
    public WebPageAdapter(FragmentManager fm) {
        super(fm);
        //this.fm=fm;
    }

    @Override
    public WebViewFragment getItem(int position) {
        return WebPageHelper.webpagelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return WebPageHelper.webpagelist.get(position).hashCode();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return WebPageHelper.webpagelist.size();
    }
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container,position);
//    }
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        Log.d("WebView","调用了destroyItem");
//        if(notifyType==1&&position==deleteItem){
//            fm.beginTransaction().remove((Fragment) object).commit();
//            deleteItem=-1;
//            return;
//        }
//        super.destroyItem(container, position, object);
//    }

//    public int getDeleteItem() {
//        return deleteItem;
//    }

//    public void setDeleteItem(int deleteItem) {
//        this.deleteItem = deleteItem;
//    }

//    public void notifyDataSetChanged(int type) {
//        notifyType=type;
//        super.notifyDataSetChanged();
//    }

}
