package com.example.towerdefense;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {
    public final static Point GAME_SCREEN_SIZE = new Point(1280, 720);

    private static SoundManager soundManager;
    private static GameManager gameManager;
    private static UiManager uiManager;
    private static GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //스태틱 클래스이므로 객체 생성하지 않고 바로 초기화
        BitmapManager.bitmapListInit(getApplicationContext());

        if (soundManager == null && SoundManager.SoundList.values().length != 0)
            soundManager = new SoundManager(getApplicationContext());

        if (uiManager == null)
            uiManager = new UiManager();

        if (gameManager == null)
            gameManager = new GameManager(soundManager, uiManager);

        if (gameView == null) {
            Point mobileSize = new Point();

            getWindowManager().getDefaultDisplay().getSize(mobileSize);
            gameView = new GameView(getApplicationContext(), gameManager, uiManager, mobileSize);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            setContentView(gameView);
        }
    }

    //종료시 물어보는 대화창을 생성함
    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitDlg = new AlertDialog.Builder(this);
        exitDlg.setTitle("확인");
        exitDlg.setMessage("게임을 종료하시겠습니까?");
        exitDlg.setIcon(R.drawable.tower001);
        exitDlg.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                android.os.Process.killProcess( android.os.Process.myPid() );
            }
        });
        exitDlg.setNegativeButton("Cancel",null);
        exitDlg.setCancelable(true);
        exitDlg.show();
    }
}