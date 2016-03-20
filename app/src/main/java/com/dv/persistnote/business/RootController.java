package com.dv.persistnote.business;

import android.os.Message;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.dv.persistnote.base.network.GitService;
import com.dv.persistnote.base.network.Gitmodel;
import com.dv.persistnote.framework.ui.AbstractScreen;
import com.dv.persistnote.framework.ui.AbstractTabContentView;
import com.dv.persistnote.framework.core.MsgDef;
import com.dv.persistnote.framework.core.AbstractController;
import com.dv.persistnote.framework.core.BaseEnv;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hang on 2016/3/13.
 * 最底部的窗口，用于承载各个Tab
 */
public class RootController extends AbstractController{

    private RootScreen mRootScreen;
    private SparseArray<AbstractTabContentView> mTabViews = new SparseArray<AbstractTabContentView>();

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == MsgDef.MSG_INIT_ROOTSCREEN) {
            mRootScreen = new RootScreen(mContext, this);
            mWindowMgr.createWindowStack(mRootScreen);

            checkLoginState();
        }
    }

    private void checkLoginState() {
        mDispatcher.sendMessage(MsgDef.MSG_SHOW_WELCOME_SCREEN);
    }

    public RootController(BaseEnv baseEnv) {
        super(baseEnv);
    }

    @Override
    public void onWindowStateChange(AbstractScreen target, byte stateFlag) {

    }

    @Override
    public boolean onWindowKeyEvent(AbstractScreen target, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean handleAction(int actionId, Object arg, Object result) {
        testPost();
        return false;
    }

    private void testPost() {
        String API = "https://api.github.com";

        RestAdapter restAdapter = new RestAdapter.Builder().
                setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();

        GitService git = restAdapter.create(GitService.class);

        git.getFeed("cmcchemc", new Callback<Gitmodel>() {
            @Override
            public void success(Gitmodel gitmodel, Response response) {
                mRootScreen.setCheckInText(gitmodel.getLocation());
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }
}
