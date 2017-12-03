package com.example.towerdefense;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 김재현 on 2017-07-25.
 */

class GameView extends View {
    private GameManager gameManager;
    private UiManager uiManager;
    //현재 핸드폰 기종의 화면 크기가 저장됨
    private Point mobileSize;

    //View에 사용될 Canvas의 표준 크기 Rect
    private final Rect VIEW_RECT;
    //View에 사용될 Canvas의 표준 크기 비율 PointF
    private final PointF VIEW_SCALE;
    //View의 초당 업데이트될 횟수
    private float viewFPS;
    //View의 업데이트 주기(Nano초 기준)
    private long viewUpdateTime;

    //맵을 그리는 데 사용될 Paint
    private Paint mapPaint;

    private ViewThread thread;

    public GameView(Context context, GameManager gameMgr, UiManager uiMgr, Point mSize){
        super(context);
        gameManager = gameMgr;
        gameManager.setGameView(this);
        uiManager = uiMgr;
        mobileSize = new Point(mSize);

        //Canvas의 크기는 MainActivity에서 정의한 표준 크기(1280*720)를 사용함
        VIEW_RECT = new Rect(0, 0, MainActivity.GAME_SCREEN_SIZE.x, MainActivity.GAME_SCREEN_SIZE.y);
        VIEW_SCALE = new PointF((float)mobileSize.x / (float)VIEW_RECT.width(),
                (float)mobileSize.y / (float)VIEW_RECT.height());

        //기본 FPS는 60으로 설정
        setViewFPS(Math.min(60f*NanoTimer.getTimerSpeed(), 60f));

        mapPaint = new Paint();
        mapPaint.setAntiAlias(true);

        setBackgroundColor(Color.BLACK);
    }

    /**
     * View의 초당 업데이트 횟수를 설정함 (0f < fps <=60.0f)
     * @param fps View의 초당 업데이트 횟수
     * @return 설정 성공 여부
     */
    public boolean setViewFPS(float fps) {
        if(fps <= 0f || fps > 60.0f)
            return false;

        viewFPS = fps;
        viewUpdateTime = Math.max((long)Math.floor(1000 / 60), (long)Math.floor(1000 / viewFPS));

        return true;
    }

    @Override
    /**
     * 사이즈 체인지가 될 시 스레드 (재)실행 (최초 생성시에도 실행됨)
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(thread == null)
            thread = new ViewThread();

        thread.isRun = true;
        thread.start();
    }

    @Override
    /**
     * View의 Draw 메소드
     */
    protected synchronized void onDraw(Canvas canvas) {
        final Canvas c = canvas;

        if(c != null) {
            //캔버스 크기를 표준 화면 크기로 맞춤
            c.scale(VIEW_SCALE.x, VIEW_SCALE.y, c.getWidth() / 2, c.getHeight() / 2);

            //캔버스의 Rect를 가져옴
            final Rect canvasRect = c.getClipBounds();

            //GameThread와 동기화 후 canvas에 그리기 시작함
            synchronized (gameManager.getGameThread()) {
                //게임 진행상황을 그림

                //맵을 그림
                c.drawBitmap(gameManager.getStageManager().getMapFieldBitmap(), canvasRect.left, canvasRect.top, mapPaint);

                //그릴 수 있는 게임 영역에 맞춰 구성 요소들을 그림
                RectF gameScreenRect = gameManager.getGameScreenRect();
                final RectF drawableGameRect = new RectF(
                        canvasRect.left + gameScreenRect.left,
                        canvasRect.top + gameScreenRect.top,
                        canvasRect.left + gameScreenRect.right,
                        canvasRect.top + gameScreenRect.bottom
                );

                //각 오브젝트 풀을 가져와 모든 활성화된 오브젝트를 대상으로 그리는 메소드를 호출
                ObjectPool<Enemy> enemyObjectPool = gameManager.getEnemies();
                enemyObjectPool.batchEnabled(new ObjectPool.Batch() {
                    @Override
                    public void function(Object currentObject, int objNum) {
                        Enemy currentEnemy = (Enemy)currentObject;
                        currentEnemy.draw(c, drawableGameRect);
                        currentEnemy.getPosX();
                    }
                });

                ObjectPool<Tower> towerObjectPool = gameManager.getTowers();
                towerObjectPool.batchEnabled(new ObjectPool.Batch() {
                    @Override
                    public void function(Object currentObject, int objNum) {
                        ((Tower) currentObject).draw(c, drawableGameRect);
                    }
                });

                ObjectPool<Bullet> bulletObjectPool = gameManager.getBullets();
                bulletObjectPool.batchEnabled(new ObjectPool.Batch() {
                    @Override
                    public void function(Object currentObject, int objNum) {
                        ((Bullet) currentObject).draw(c, drawableGameRect);
                    }
                });

                //ui를 그림
                uiManager.updateUi();
                for (UiManager.UiObject u : uiManager.getUiList()) {
                    u.draw(c);
                }

            }
        }
    }

    //View 스레드
    public class ViewThread extends Thread {
        private boolean isRun = false;

        @Override
        public void run(){
            while (isRun) {
                try {
                    postInvalidate();
                    sleep(viewUpdateTime);
                } catch (Exception e) {
                    //
                }
            }
        }

    }

    //터치 이벤트가 발생 시 호출되는 메소드
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX() / VIEW_SCALE.x;
        float y = event.getY() / VIEW_SCALE.y;

        gameManager.reactTouchEvent(event.getAction(), x, y);

        return true; //processed
    }

}