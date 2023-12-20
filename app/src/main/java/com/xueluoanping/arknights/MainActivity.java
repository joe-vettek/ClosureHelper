package com.xueluoanping.arknights;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xueluoanping.arknights.api.BetterEntry;
import com.xueluoanping.arknights.api.main.Game;
import com.xueluoanping.arknights.api.main.auth;
import com.xueluoanping.arknights.api.resource.Kengxxiao;
import com.xueluoanping.arknights.api.resource.monster_siren;
import com.xueluoanping.arknights.api.tool.ToolTime;
import com.xueluoanping.arknights.base.BaseActivity;
import com.xueluoanping.arknights.global.Global;
import com.xueluoanping.arknights.pages.AboutActivity;
import com.xueluoanping.arknights.pages.LoginActivity;
import com.xueluoanping.arknights.pages.SettingActivity;
import com.xueluoanping.arknights.pages.WebActivity2;
import com.xueluoanping.arknights.pages.fragment.FragmentWithName;
import com.xueluoanping.arknights.pages.fragment.GamesFragment;
import com.xueluoanping.arknights.pages.fragment.SectionsPagerAdapter;
import com.xueluoanping.arknights.pro.SimpleTool;
import com.xueluoanping.arknights.pro.spTool;
import com.xueluoanping.arknights.services.SimpleService;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    //more按钮的监听器
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawer.openDrawer(Gravity.LEFT);
        }
    };

    private Game.GameInfo[] games;
    private ViewPager2 viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabs;
    private TabLayoutMediator tabLayoutMediator;
    private ImageButton bt_search;
    private EditText editText;
    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Window w = getWindow();
        // w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // 目前唯一有效的方法，让不打开左侧导航时标题栏不透明，其余时候 透明


        // auth.initGTCaptcha4Client(MainActivity.this);
        //
        // new Thread(() -> auth.click(new AtomicReference<>())).start();
        // setAppSpecial();

        requestforPermissons();

        Kengxxiao.checkforResource(this);

        SimpleApplication.checkAPPUpdate(this);

        bindComponents();
        Global.prepareBaseData(this);
        startService(1);

        // Intent ii = new Intent(getApplicationContext(), CheckActivity.class);
        // startActivity(ii);
        if (spTool.getToken(SimpleApplication.getContext()).isEmpty()) {
            loginKLXE();
        }

        showAnnouncement();
        queryArknightsIsMaintaining();
        loadAllGames();
    }

    private void setAppSpecial() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (spTool.getStartMusic()) playMusic(true);
        else playMusic(false);
    }

    private void loginKLXE() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void showAnnouncement() {

        // SimpleTool.toastInThread(this, "可露希尔公告:" + getString(R.string.AnnouncementText));

        if (ToolTime.getTimeOffset() != 0)
            SimpleTool.toastInThread(this, "您的所在时区非北京时间，注意本APP使用北京时间显示内容！");

        String v = spTool.getVersion();
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            // v = pi.versionName;
            spTool.setVersion(pi.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // if (!v.equals(spTool.getVersion())) {
        //     AlertDialog.Builder dialogAnnouncementBuilder = new AlertDialog.Builder(MainActivity.this)
        //             .setIcon(R.mipmap.npc_007_closure)
        //             .setTitle("1.3.x 更新日志（仅在更新后首次启动时显示）")
        //             .setMessage(R.string.UpdateText)
        //             .setPositiveButton("关闭", null);
        //     final AlertDialog dlg = dialogAnnouncementBuilder.create();
        //     dlg.show();
        // }

        // Timer t = new Timer();
        // t.schedule(new TimerTask() {
        //     public void run() {
        //         dlg.dismiss();
        //     }
        // }, 3000);
    }

    private void requestforPermissons() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                permissions = new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.POST_NOTIFICATIONS};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
                }
            }
        }


    }

    private void bindComponents() {
        //加载布局
        drawer = findViewById(R.id.drawer_layout);

        editText = findViewById(R.id.input);
        bt_search = findViewById(R.id.search_button);
        bt_search.setOnClickListener(v -> {
            String url = WebActivity2.getsUrl_prtsSearch(editText.getText().toString());
            startWebActivity(url);
        });

        //左侧栏加载
        NavigationView v = findViewById(R.id.nav_view);
        v.setNavigationItemSelectedListener(this);

        //左侧滑动
        ImageView iv_toleft = findViewById(R.id.iv_menu);
        iv_toleft.setOnClickListener(onClickListener);

        //设置tabhost
        sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        // tabs.setupWithViewPager(viewPager);
        tabLayoutMediator = new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> {
                    Fragment myFragment = getSupportFragmentManager().findFragmentByTag("f" + position);
                    if (myFragment instanceof FragmentWithName) {
                        String t0 = ((FragmentWithName) myFragment).getName() != null ?
                                ((FragmentWithName) myFragment).getName() :
                                SimpleTool.protectTelephoneNum(games[position - 1].account);
                        tab.setText("" + t0);
                    } else {
                        if (position != 0)
                            tab.setText("实例" + position + "");
                    }
                }
        );
        tabLayoutMediator.attach();


        //实例化，保存对fragment对象的引用
        final GamesFragment[] fragmentsArray = new GamesFragment[sectionsPagerAdapter.getItemCount()];

        //实例化，对于viewpager添加状态监听器
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }


        });

        //初始化浮动按钮，注意全路径
        final FloatingActionsMenu floatingActionsMenu0 = findViewById(R.id.multiple_actions);
        com.getbase.floatingactionbutton.FloatingActionButton floatingActionsMenu = findViewById(R.id.fab1);
        com.getbase.floatingactionbutton.FloatingActionButton floatingActionsMenu2 = findViewById(R.id.fab2);
        com.getbase.floatingactionbutton.FloatingActionButton floatingActionsMenu3 = findViewById(R.id.fab3);
        com.getbase.floatingactionbutton.FloatingActionButton floatingActionsMenu4 = findViewById(R.id.fab4);
        //浮动按钮
        floatingActionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu0.collapse();
                hideInputMethod();
                goNextTheme();

            }
        });
        floatingActionsMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                floatingActionsMenu0.collapse();

                return true;
            }

        });
        //事件标记，注意避免空指针
        floatingActionsMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu0.collapse();
                ((SimpleApplication) SimpleApplication.getContext()).restart();
            }
        });
        //异步播放音乐
        floatingActionsMenu3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                floatingActionsMenu0.collapse();
                playMusic(true);


            }
        });

        floatingActionsMenu4.setOnClickListener(v1 -> {
            floatingActionsMenu0.collapse();
            int position = viewPager.getCurrentItem();
            Fragment myFragment = getSupportFragmentManager().findFragmentByTag("f" + position);
            if (myFragment instanceof FragmentWithName) {
                ((FragmentWithName) myFragment).scrollTo(0, 0);
            } else {
                // if (position != 0)
                //     tab.setText("实例" + position + "");
            }
        });
    }

    private void playMusic(boolean random) {
        new Thread(() -> {
            try {
                if (mediaPlayer != null) {
                    //  重置，暂停，最后释放
                    try {
                        // if (mediaPlayer.isPlaying()) {
                        //     mediaPlayer.reset();
                        //     mediaPlayer.stop();
                        // }
                        mediaPlayer.reset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();
                String randomName = "";
                if (random) {
                    BetterEntry<String, String> ee = monster_siren.getRandomSong();
                    mediaPlayer.setDataSource(ee.getValue());
                    randomName = ee.getKey();
                } else {
                    // 避免不存在
                    try {
                        int id = spTool.getMusicSelect();
                        int realId = Integer.parseInt(getResources().getStringArray(R.array.music_type_id)[id]);
                        if (realId != -100)
                            mediaPlayer.setDataSource(monster_siren.getMusicFilePos(realId));
                        else {
                            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.running_in_the_dark);
                            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());

                        }
                        mediaPlayer.setLooping(true);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        Log.d(TAG, "playMusic: 播放失败");
                    }
                }
                mediaPlayer.prepare();
                // mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                String finalRandomName = randomName;
                mediaPlayer.setOnPreparedListener((p) -> {
                    p.start();
                    if (random)
                        toastInThread("正在播放：  塞壬唱片 - " + finalRandomName);
                });
                if (random)
                {
                    mediaPlayer.setOnCompletionListener((p) -> {
                        p.release();
                        mediaPlayer = null;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(10);
                        toastInThread("播放结束");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (random)
                    toastInThread("暂无可用资源，请重试");
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }).start();
    }


    public void startService(int actionCode) {
        // if (!SimpleService.isRunning)
        {
            Intent intent = new Intent(MainActivity.this, SimpleService.class);
            intent.setClass(getApplicationContext(), SimpleService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("actionCode", actionCode);
            startService(intent);
        }
    }

    //侧滑栏选择
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //获取点击的id
        int id = item.getItemId();
        drawer.setSelected(false);
        drawer.closeDrawer(GravityCompat.START);

        switch (id) {
            case R.id.it:
                drawer.closeDrawer(GravityCompat.START);
                Intent i1 = getStartActivityIntent(WebActivity2.class);
                i1.putExtra("url", WebActivity2.url);
                startActivity(i1);
                break;
            case R.id.it_b:
                drawer.closeDrawer(GravityCompat.START);
                Intent i2 = getStartActivityIntent(WebActivity2.class);
                i2.putExtra("url", WebActivity2.url_b);
                startActivity(i2);
                break;
            case R.id.nav_share:
                drawer.closeDrawer(GravityCompat.START);
                startActivity(AboutActivity.class);
                break;
            case R.id.so:
                drawer.closeDrawer(GravityCompat.START);
                String packageName = "com.hypergryph.arknights";
                startPackage(packageName);
                break;
            case R.id.so_b:
                drawer.closeDrawer(GravityCompat.START);
                String packageName_b = "com.hypergryph.arknights.bilibili";
                startPackage(packageName_b);
                break;
            case R.id.nav_settings:
                drawer.closeDrawer(GravityCompat.START);
                startActivity(SettingActivity.class);
                break;
            case R.id.nav_send:
                // Intent data = new Intent(Intent.ACTION_SENDTO);
                // data.setData(Uri.parse("mailto:afc789456123@163.com"));
                // data.putExtra(Intent.EXTRA_SUBJECT, "联系作者（测试）");
                // data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                // startActivity(data);
                toastInThread("请查看帮助。");
                startActivity(AboutActivity.class);
                break;
            case R.id.nav_importMusic:
                drawer.closeDrawer(GravityCompat.START);
                loginKLXE();
                break;
            default:
                drawer.closeDrawer(GravityCompat.START);
                int a = 0;
                break;
        }


        return true;
    }


    public void loadAllGames() {
        new Thread(() -> {
            try {
                games = Game.getGameStatue(SimpleApplication.getContext());

                sectionsPagerAdapter.updateGames(games);
                runOnUiThread(() -> {sectionsPagerAdapter.notifyDataSetChanged();});
                Log.d(TAG, "loadAllGames: " + sectionsPagerAdapter.hasObservers());

                // Intent ii = new Intent(getApplicationContext(), CheckActivity.class);
                // ii.putExtra("challenge", "95085e132e69ae9e2c6cc7702bd0ac4a");
                // ii.putExtra("gt", "73594bc21b4cdbfd7a3ce45226698113");
                // ii.putExtra("account", games[1].account);
                // ii.putExtra("platform", games[1].platform);
                // startActivity(ii);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }).start();
    }

    public void alertTabText() {
        runOnUiThread(() -> {
            tabLayoutMediator.detach();
            tabLayoutMediator.attach();
            //    暂时有点问题，因此只能这样定时刷新。
        });
    }

}