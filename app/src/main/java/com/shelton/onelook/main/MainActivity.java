package com.shelton.onelook.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shelton.onelook.R;
import com.shelton.onelook.adapter.MenuListAdapter;
import com.shelton.onelook.adapter.WebPageAdapter;
import com.shelton.onelook.base.BaseActivity;
import com.shelton.onelook.bean.WeatherInfoBean;
import com.shelton.onelook.bean.WebDeleteEvent;
import com.shelton.onelook.bean.WebFragmentLoadBean;
import com.shelton.onelook.login.LoginActivity;
import com.shelton.onelook.task.DownloaderTask;
import com.shelton.onelook.ui.CollectionEditActivity;
import com.shelton.onelook.ui.ConfigActivity;
import com.shelton.onelook.ui.DownloadRecordActivity;
import com.shelton.onelook.ui.HistoryAndLabelActivity;
import com.shelton.onelook.ui.QueryActivity;
import com.shelton.onelook.util.DownloadHelper;
import com.shelton.onelook.util.MyUtil;
import com.shelton.onelook.util.WebPageHelper;
import com.shelton.onelook.widget.MingWebView;
import com.shelton.onelook.widget.MyViewPager;
import com.shelton.onelook.widget.ScrollLayout;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    @BindView(R.id.home_button)
    ImageView homeButton;
    @BindView(R.id.menu_button)
    ImageView menuButton;
    @BindView(R.id.query_button)
    TextView queryButton;
    @BindView(R.id.web_back)
    ImageView backButton;
    @BindView(R.id.web_freshen)
    ImageView freshenButton;
    @BindView(R.id.web_multi)
    ImageView multiButton;
    @BindView(R.id.web_next)
    ImageView nextButton;
    @BindView(R.id.web_stop_loading)
    ImageView stopLoading;

    @BindView(R.id.menu_list)
    ListView listView;
    @BindView(R.id.exit_button)
    TextView exitButton;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.weather)
    TextView now_temperature;
    @BindView(R.id.current_city)
    TextView city;

    @BindView(R.id.anchor)
    View anchor;
    @BindView(R.id.toolbar)
    View toolbar;
    @BindView(R.id.status_bar)
    View statusBar;
    @BindView(R.id.bottom_bar)
    View bottomBar;
    @BindView(R.id.web_page_control_bar)
    View webPageControlBackground;
    @BindView(R.id.add_web_page)
    Button addWebPage;
    @BindView(R.id.web_container)
    MyViewPager mViewPager;
    @BindView(R.id.web_layout)
    ScrollLayout webLayout;
    @BindView(R.id.dot_indicator)
    LinearLayout indicator;

    private MingWebView webView;
    private WebPageAdapter webpageAdapter;
    private MenuListAdapter menuAdapter;
    private long mExitTime;    //按下返回键退出时的时间
    private boolean first = true;  //有两种含义：第一次运行app时或标签页最后一页被删后需要重新定位当前WebView对象
    private boolean isZoom = false;  //是否缩放
    private SharedPreferences preferences;
    private int firstPosition = 0;
    private int selectMenuPosition = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_TransparentActivity);
        super.onCreate(savedInstanceState);

        //网络状态变化广播监听
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChange, mFilter);

        //天气结果广播监听
        IntentFilter mFilter2 = new IntentFilter();
        mFilter2.addAction("weather_refresh");
        LocalBroadcastManager.getInstance(this).registerReceiver(refresh, mFilter2);

        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //MainActivityPermissionsDispatcher.getWeatherPermissionWithCheck(this);
        EventBus.getDefault().register(this);
    }

    @SuppressLint("SetTextI18n")
    protected void onResume() {
        super.onResume();

        city.setText(preferences.getString("cityName", "未知城市"));
        if (webView != null) {
            webView.getSettings().setTextZoom(Integer.valueOf(preferences.getString("text_size", "100")));
        }
        toolbar.setBackgroundColor(Color.parseColor(preferences.getString("theme_color", "#474747")));
        statusBar.setBackgroundColor(Color.parseColor(preferences.getString("theme_color", "#474747")));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String url;
        if (action != null)
            switch (action) {
                case Intent.ACTION_VIEW:
                case "com.shelton.onelook.action.VIEW":
                    url = intent.getDataString();
                    if (url != null) {
                        Log.d("Main", "onNewIntent地址Path：" + url);
                        webView.loadUrl(url);
                        return;
                    }
                    break;
                default:
                    url = intent.getStringExtra("shortcut_url");
                    if (webView != null) {
                        webView.loadUrl(url);
                    }
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Bundle> bundles = new ArrayList<>();
        for (WebViewFragment fragment : WebPageHelper.webpagelist) {
            Bundle save = new Bundle();
            WebView webView = fragment.getInnerWebView();
            if (webView != null)
                webView.saveState(save);
            bundles.add(save);

        }
        outState.putInt("web_page_count", WebPageHelper.webpagelist.size());
        outState.putParcelableArrayList("web_page_bundle", (ArrayList<? extends Parcelable>) bundles);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebPageHelper.webpagelist.clear();
        unregisterReceiver(networkChange);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refresh);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void getWeatherPermission() {
        startService(new Intent(this, WeatherService.class));
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void weatherPermissionDenied() {
        finish();
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void weatherPermissionNeverAsk() {
        MyUtil.createDialog(MainActivity.this, "警告",
                "当前应用缺少必要权限，请点击“设置”开启权限或点击“取消”关闭应用。",
                "设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                        MainActivity.this.startActivity(intent);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        MainActivity.this.finish();
                    }
                });
    }


    private void initView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            List<Bundle> bundles = savedInstanceState.getParcelableArrayList("web_page_bundle");
            int count = savedInstanceState.getInt("web_page_count");
            for (int i = 0; i < count; i++) {
                WebViewFragment fragment = new WebViewFragment(bundles != null ? bundles.get(i) : null);
                WebPageHelper.webpagelist.add(fragment);
            }
            initDot(count);
        } else {
            String url = getIntent().getStringExtra("shortcut_url");
            Log.d("Main", "onCreate地址Path：" + url);
            WebViewFragment fragment = new WebViewFragment(null, url);
            WebPageHelper.webpagelist.add(fragment);
            initDot(1);
        }

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        menuAdapter = new MenuListAdapter(this);
        listView.setAdapter(menuAdapter);

        mViewPager.setOnLayoutClickListener(new MyViewPager.OnLayoutClickListener() {
            @Override
            public void onLayoutClick() {
                ZoomChange(1);
            }
        });
        ((ViewGroup) mViewPager.getParent()).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });
        mViewPager.setPageMargin(MyUtil.dip2px(this, 45));
        webpageAdapter = new WebPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(webpageAdapter);

        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicator.getChildAt(firstPosition).setEnabled(false);
                indicator.getChildAt(position).setEnabled(true);
                firstPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NotNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NotNull View drawerView) {
                switch (selectMenuPosition) {
                    case -1:
                        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 2);
                        break;
                    case 0:
                        webView.loadUrl("http://dushu.m.baidu.com");
                        break;
                    case 1:
                        webView.loadUrl("https://m.bilibili.com");
                        break;
                    case 2:
                        webView.loadUrl("http://m.xinhuanet.com");
                        break;
                    case 3:
                        //如果不是新的请求，getFavicon只能返回旧图，待修复
                        Intent intent = new Intent(MainActivity.this, CollectionEditActivity.class);
                        Bitmap icon = webView.getFavicon();
                        if (icon == null)
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.collection_icon_default);
                        intent.putExtra("icon", icon);
                        intent.putExtra("title", webView.getTitle());
                        intent.putExtra("url", webView.getUrl());
                        startActivity(intent);
                        break;
                    case 4:
                        startActivityForResult(new Intent(MainActivity.this, DownloadRecordActivity.class), 1);
                        break;
                    case 5:
                        startActivityForResult(new Intent(MainActivity.this, HistoryAndLabelActivity.class), 1);
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                        break;
                }
                selectMenuPosition = -2;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initDot(int count) {
        indicator.removeAllViews();
        View view;
        for (int i = 0; i < count; i++) {
            //创建底部指示器(小圆点)
            view = new View(this);
            view.setBackgroundResource(R.drawable.dot_background);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MyUtil.dip2px(this, 7), MyUtil.dip2px(this, 7));
            //设置间隔
            if (i != 0) {
                layoutParams.leftMargin = MyUtil.dip2px(this, 6);
            }
            //添加到LinearLayout
            indicator.addView(view, layoutParams);
        }
        indicator.getChildAt(mViewPager.getCurrentItem()).setEnabled(true);
        firstPosition = mViewPager.getCurrentItem();
    }

    @OnItemClick(R.id.menu_list)
    public void onItemClick(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        selectMenuPosition = position;
    }

    @OnClick({R.id.menu_button, R.id.query_button, R.id.web_back, R.id.web_next,
            R.id.web_freshen, R.id.web_stop_loading, R.id.web_multi, R.id.add_web_page,
            R.id.home_button, R.id.exit_button, R.id.tv_username})
    public void onClick(View view) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) mDrawerLayout.closeDrawers();
        int object = view.getId();
        switch (object) {
            case R.id.menu_button:
                mDrawerLayout.openDrawer(GravityCompat.START, true);
                break;
            case R.id.query_button:
                startActivityForResult(new Intent(this, QueryActivity.class), 1);
                break;
            case R.id.web_back:
                webView.goBack();
                break;
            case R.id.web_next:
                webView.goForward();
                break;
            case R.id.web_freshen:
                webView.reload();
                break;
            case R.id.web_stop_loading:
                webView.stopLoading();
                break;
            case R.id.web_multi:
                ZoomChange(0);
                break;
            case R.id.add_web_page:
                if (WebPageHelper.webpagelist.size() >= WebPageHelper.WEB_PAGE_LIMIT_NUM) {
                    Toast.makeText(this, "窗口数量超过最大值", Toast.LENGTH_SHORT).show();
                } else {
                    WebViewFragment fragment = new WebViewFragment(null);
                    WebPageHelper.webpagelist.add(mViewPager.getCurrentItem() + 1, fragment);
                    webpageAdapter.notifyDataSetChanged();
                    initDot(WebPageHelper.webpagelist.size());
                    fixWebPage(mViewPager.getCurrentItem() + 1);
                    ZoomChange(1);

                }
                break;
            case R.id.home_button:
                webView.loadUrl("file:///android_asset/index.html");
                break;
            case R.id.exit_button:
                checkDownloadTask();
                break;
            case R.id.tv_username:
                selectMenuPosition = -1;
                break;
            default:
                Toast.makeText(this, "开发中...", Toast.LENGTH_SHORT).show();
        }
    }

    private void ZoomChange(int flag) {
        //0为缩小，1为放大
        if (flag == 0) {
            isZoom = true;
            for (WebViewFragment webViewFragment : WebPageHelper.webpagelist) {
                webViewFragment.getInnerWebView().onPause();
                webViewFragment.getInnerWebView().pauseTimers();
            }
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mViewPager.setFullScreen(false);

            webLayout.scrollTo(0, 0);


            mViewPager.clearAnimation();
            mViewPager.animate().scaleX(0.65f).scaleY(0.65f).setDuration(400).start();

            webPageControlBackground.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            bottomBar.setVisibility(View.INVISIBLE);
            indicator.setVisibility(View.VISIBLE);

        } else {
            //防止viewpager滑动错位
            fixWebPage(mViewPager.getCurrentItem());

            webView = WebPageHelper.webpagelist.get(mViewPager.getCurrentItem()).getInnerWebView(); //定位当前的WebView对象
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
            isZoom = false;

            for (WebViewFragment webViewFragment : WebPageHelper.webpagelist) {
                if (WebPageHelper.webpagelist.get(mViewPager.getCurrentItem()).equals(webViewFragment)) {
                    webView.onResume();     //由于调用onResume会导致所有WebView都处于活动状态，而onPause只是针对单个
                    webView.resumeTimers();
                } else {
                    webViewFragment.getInnerWebView().onPause();
                    webViewFragment.getInnerWebView().pauseTimers();
                }
            }
            mViewPager.setFullScreen(true);

            mViewPager.clearAnimation();
            mViewPager.animate().scaleX(1f).scaleY(1f).setDuration(0).start();

            webPageControlBackground.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.INVISIBLE);


            //检测当前的webview对象是否可以向前或前后浏览
            if (!webView.canGoBack()) {
                backButton.setEnabled(false);
            } else {
                backButton.setEnabled(true);
            }
            if (!webView.canGoForward()) {
                nextButton.setEnabled(false);
            } else {
                nextButton.setEnabled(true);
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetWebView(MingWebView webView) {
        if (first) {
            //调用代表为新添加的webview
            MainActivity.this.webView = webView;
            first = false;
        }

        webView.setOnScrollChangedCallback(new MingWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int dx, int dy) {
                webLayout.scrollBy(0, dy);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageStarted(String url) {
        webLayout.scrollTo(0, 0);
        progressBar.setVisibility(View.VISIBLE);
        backButton.setEnabled(false);
        nextButton.setEnabled(false);
        freshenButton.setVisibility(View.INVISIBLE);
        stopLoading.setVisibility(View.VISIBLE);
        menuAdapter.setAllowCollect(false);
        menuAdapter.isEnabled(3);
        menuAdapter.notifyDataSetInvalidated();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressChanged(Integer progress) {
        if (progress > 80) {
            //进度大于80，一般网页就加载完成了,但是为了能够在点击收藏标签前收到icon，必须等到加载完成到100
            progressBar.setVisibility(View.GONE);
            freshenButton.setVisibility(View.VISIBLE);
            stopLoading.setVisibility(View.INVISIBLE);
            if (!webView.canGoBack()) {
                backButton.setEnabled(false);
            } else {
                backButton.setEnabled(true);
            }
            if (!webView.canGoForward()) {
                nextButton.setEnabled(false);
            } else {
                nextButton.setEnabled(true);
            }
            menuAdapter.setAllowCollect(true);
            menuAdapter.isEnabled(3);
            menuAdapter.notifyDataSetInvalidated();

        } else {
            progressBar.setProgress(progress);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebFragmentLoad(WebFragmentLoadBean loadBean) {
        WebViewFragment fragment = new WebViewFragment(null, loadBean.getUrl());
        WebPageHelper.webpagelist.add(mViewPager.getCurrentItem() + 1, fragment);
        webpageAdapter.notifyDataSetChanged();
        initDot(WebPageHelper.webpagelist.size());
        if (loadBean.getLoadInMode() == WebViewFragment.LOAD_IN_NEW_WINDOW) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebFragmentDelete(WebDeleteEvent event) {
        //删除动画
        int viewTop = event.getViewTop();
        int value;
        if (viewTop > 0) {
            value = 2500;
        } else {
            value = -2500;
        }
        View selectedView = WebPageHelper.webpagelist.get(mViewPager.getCurrentItem()).getInnerContainer();
        Animation animation = new TranslateAnimation(0, 0, viewTop, value);
        animation.setDuration(400);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                WebPageHelper.webpagelist.get(mViewPager.getCurrentItem()).getInnerContainer().removeAllViews();
//                WebPageHelper.webpagelist.get(mViewPager.getCurrentItem()).getInnerWebView().destroy();
                WebPageHelper.webpagelist.remove(mViewPager.getCurrentItem());
                webpageAdapter.notifyDataSetChanged();
                if (WebPageHelper.webpagelist.size() == 0) {
                    first = true;
                    WebViewFragment fragment = new WebViewFragment(null);
                    WebPageHelper.webpagelist.add(fragment);
                    webpageAdapter.notifyDataSetChanged();
                    ZoomChange(1);
                }
                initDot(WebPageHelper.webpagelist.size());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        selectedView.startAnimation(animation);

    }

    private void fixWebPage(int position) {
        webpageAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(position, true);
    }

    public void onBackPressed() {
        if (isZoom) {
            ZoomChange(1);
        } else {
            if (!webView.canGoBack()) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "再按一次退出浏览器", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    checkDownloadTask();
                }
            } else {
                webView.goBack();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (!webView.getUrl().equals(data.getStringExtra("currentUri"))) {
                        webView.loadUrl(data.getStringExtra("currentUri"));
                    }

                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    tvUserName.setText(data.getStringExtra("username"));
                }
                break;
        }
    }

    private BroadcastReceiver networkChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Log.d("mark", "当前网络名称：" + name);
                MainActivityPermissionsDispatcher.getWeatherPermissionWithCheck(MainActivity.this);
                if (webView != null)
                    webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                if (webView != null)
                    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }

        }
    };
    private BroadcastReceiver refresh = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            WeatherInfoBean bean = (WeatherInfoBean) intent.getSerializableExtra("content");
            now_temperature.setText(bean.getTemperature());
            city.setText(bean.getCity());
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void checkDownloadTask() {
        if (DownloadHelper.downloadList.size() > 0) {
            MyUtil.createDialog(this, "退出提示",
                    "有下载任务正在进行，退出浏览器将删除临时下载文件，仍要退出？",
                    "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (DownloaderTask task : DownloadHelper.downloadList) {
                                task.cancel(true);
                                //noinspection ResultOfMethodCallIgnored
                                new File(task.getFilePath()).delete();
                            }
                            DownloadHelper.downloadList.clear();
                            MainActivity.super.onBackPressed();
                        }
                    }, null);
        } else {
            super.onBackPressed();
        }
    }

}
