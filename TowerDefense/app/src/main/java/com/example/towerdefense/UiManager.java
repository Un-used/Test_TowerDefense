package com.example.towerdefense;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 김재현 on 2017-10-19.
 */

public class UiManager {
    private GameManager gameManager;
    private List<UiObject> uiList = Collections.synchronizedList(new ArrayList<UiObject>());
    private List<UiButton> uiButtonList = Collections.synchronizedList(new ArrayList<UiButton>());
    private UiText timeText = null;
    private UiText coinsText = null;
    private UiText livesText = null;
    private UiButton standButton = null;
    private RectF outScreenRect;

    public void setGameManager(GameManager gameMgr) { gameManager = gameMgr; }

    /**
     * 화면 터치 시 호출되는 메소드
     * @param touchAction 터치 액션 종류
     * @param x 터치된 X 좌표
     * @param y 터치된 Y 좌표
     */
    public void reactTouchEvent(int touchAction, float x, float y){

        //버튼 클릭 시 해당 버튼의 이벤트를 실행함
        if(standButton != null){
            if(standButton.touchable)
                standButton.executeTouchEvent(touchAction, x, y);
            else
                standButton = null;
        } else {
            standButton = getTouchedButton(x, y);
            if(standButton != null && standButton.touchable)
                standButton.executeTouchEvent(touchAction, x, y);
        }

    }

    /**
     * 터치된 좌표에 위치한 버튼을 가져옴
     * @param x 터치된 X 좌표
     * @param y 터치된 Y 좌표
     * @return 터치된 Button
     */
    private UiButton getTouchedButton(float x, float y){
        for (int i = 0; i < uiButtonList.size(); ++i){
            UiButton currentButton = uiButtonList.get(i);
            if(currentButton.getTouchBoundWithPos().contains(x, y)){
                return currentButton;
            }
        }

        return null;
    }

    /**
     * Ui 업데이트시 호출되는 메소드
     */
    public void updateUi() {
        if (livesText != null) {
            livesText.text = "Lives: " + gameManager.getLives();
        }

        if (coinsText != null) {
            coinsText.text = "Coins: " + gameManager.getCoins();
        }

        if(timeText != null){
            long remainTime = gameManager.getRemainTime() / 100000000L;
            String timeStr;
            if(remainTime >= 100L || remainTime == 0)
                timeStr = String.format("%3ds", remainTime / 10L);
            else
                timeStr = String.format("%2.1fs", ((float)remainTime) / 10f);

            timeText.text = "Time: " + timeStr;
        }

        for(int i = 0; i < uiButtonList.size(); ++i) {
            UiButton currentButton = uiButtonList.get(i);
            if(currentButton.currentTowerList != null){
                if(currentButton.touchable && currentButton.currentTowerList.reqCoins > gameManager.getCoins())
                    currentButton.setTouchable(false);
                else if (!currentButton.touchable && currentButton.currentTowerList.reqCoins <= gameManager.getCoins())
                    currentButton.setTouchable(true);
            }
        }
    }

    /**
     * 화면의 구성이 바뀔 필요가 있을 때 호출되는 메소드
     */
    public void refreshUi() {
        uiList.clear();
        uiButtonList.clear();

        final RectF gameScreenRect = gameManager.getGameScreenRect();

        //time Ui 텍스트
        timeText = new UiText("", new PointF(gameScreenRect.centerX() - 64, 48));
        Paint timeTextPaint = timeText.getPaint();
        timeTextPaint.setColor(Color.WHITE);
        timeTextPaint.setTextSize(32f);
        timeTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timeText.setPaint(timeTextPaint);
        uiList.add(timeText);

        //life Ui 텍스트
        livesText = new UiText("", new PointF(gameScreenRect.centerX() + 160, 48));
        Paint livesTextPaint = livesText.getPaint();
        livesTextPaint.setColor(Color.CYAN);
        livesTextPaint.setTextSize(40f);
        livesTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        livesText.setPaint(livesTextPaint);
        uiList.add(livesText);

        //coin Ui 텍스트
        coinsText = new UiText("", new PointF(gameScreenRect.right - 256, 48));
        Paint coinsTextPaint = coinsText.getPaint();
        coinsTextPaint.setColor(Color.YELLOW);
        coinsTextPaint.setTextSize(40f);
        coinsTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        coinsText.setPaint(coinsTextPaint);
        uiList.add(coinsText);

        //게임 외의 화면 Rect를 구함(게임 스크린 Rect의 밑 기준으로 버튼을 생성할 Rect)
        outScreenRect = new RectF(0, gameScreenRect.bottom + 1,
                MainActivity.GAME_SCREEN_SIZE.x, MainActivity.GAME_SCREEN_SIZE.y);

        //건설 가능한 타워를 가져와 해당 타워의 버튼을 만듦
        List<TowerList> buildableTowerList = gameManager.getBuildableTowerList();
        for(int i = 0; i < buildableTowerList.size(); ++i){
            //건설 가능한 타워 종류를 가져옴
            final TowerList currentTowerList = buildableTowerList.get(i);

            //건설 가능한 타워의 버튼을 생성
            final UiButton uiButton = new UiButton(BitmapList.BTN_TOWER);

            //타워 건설 버튼의 크기와 좌표 지정
            uiButton.setSpriteScale(outScreenRect.height() / (float) uiButton.getBmpHeight());
            uiButton.setPos(uiButton.getSpriteWidth()*(i+0.5f),
                    outScreenRect.top + uiButton.getSpriteHeight()*0.5f);
            //타워 건설 버튼의 뒷 배경을 크기에 맞춰 생성
            UiImage btnBackImage = new UiImage(BitmapList.BTN_TOWER_BACKGROUND);
            btnBackImage.setSpriteScale(uiButton.getScaleX(), uiButton.getScaleY());
            //타워 건설 버튼의 타워 아이콘 생성
            UiImage btnTowerImage = new UiImage(currentTowerList.bitmapList);
            //뒷 배경 이미지의 위에다가 타워 아이콘을 링크함
            btnBackImage.addUpperSprite(btnTowerImage, true);
            //타워 버튼 아래에다가 뒷 배경 이미지를 링크함
            uiButton.addLowerSprite(btnBackImage, true);

            //ui 버튼의 정보 설정
            uiButton.currentTowerList = currentTowerList;
            //타워의 비용 값 text 설정
            uiButton.linkedText = new UiText("" + currentTowerList.reqCoins, uiButton.getPos().x + 18, uiButton.getPos().y + 45);
            Paint linkedTextPaint = uiButton.linkedText.getPaint();
            linkedTextPaint.setColor(Color.WHITE);
            linkedTextPaint.setTextSize(25f);
            linkedTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            uiButton.linkedText.setPaint(linkedTextPaint);

            //생성된 타워 건설 버튼의 터치 이벤트를 설정
            uiButton.setTouchEvent(new Event() {
                @Override
                public void execute(UiButton uiButton, int touchAction, float x, float y) {
                    // 인자 uiButton 은 해당 uiButton과 같음
                    // 이 코드가 실행될 때는 반드시 standButton == uiButton != null 이다.
                    if (uiButton.isStanding == false) {
                        switch (touchAction) {
                            //터치 시작(처음 눌렀을 때)
                            case MotionEvent.ACTION_POINTER_DOWN:
                            case MotionEvent.ACTION_DOWN:
                                if (getTouchedButton(x, y) == uiButton) {
                                    if(gameManager.getCoins() >= uiButton.currentTowerList.reqCoins)
                                        uiButton.initBitmap(BitmapList.BTN_TOWER_SELECT);
                                } else {
                                    uiButton.initBitmap(BitmapList.BTN_TOWER);
                                    standButton = null;
                                }
                                break;
                            //터치 후 떼지 않고 이동 시
                            case MotionEvent.ACTION_MOVE:
                                //터치가 해당 버튼을 벗어나면
                                if (getTouchedButton(x, y) != uiButton) {
                                    uiButton.initBitmap(BitmapList.BTN_TOWER);
                                    standButton = null;
                                }
                                break;
                            //터치 후 뗐을 때
                            case MotionEvent.ACTION_POINTER_UP:
                            case MotionEvent.ACTION_UP:
                                //손을 뗀 자리가 터치를 시작했던 해당 버튼이면 스탠딩 상태에 들어감
                                if (getTouchedButton(x, y) == uiButton
                                        && uiButton.getBitmapList() == BitmapList.BTN_TOWER_SELECT) {
                                    uiButton.isStanding = true;
                                } else {
                                    uiButton.initBitmap(BitmapList.BTN_TOWER);
                                    standButton = null;
                                }
                                break;
                        }
                    }
                    //스탠딩 상태(터치 후 대기 상태)일 때
                    else {
                        UiButton touchedButton = getTouchedButton(x, y);
                        switch (touchAction) {
                            case MotionEvent.ACTION_POINTER_DOWN:
                            case MotionEvent.ACTION_DOWN:
                                //다른 버튼 터치시 대기 상태 해제
                                if(touchedButton != null) {
                                    uiButton.isStanding = false;
                                    uiButton.initBitmap(BitmapList.BTN_TOWER);
                                    standButton = null;
                                }
                                break;
                            case MotionEvent.ACTION_POINTER_UP:
                            case MotionEvent.ACTION_UP:
                                //해당 버튼을 다시 터치시 대기 상태 해제
                                if(uiButton == touchedButton) {
                                    uiButton.isStanding = false;
                                    uiButton.initBitmap(BitmapList.BTN_TOWER);
                                    standButton = null;
                                } else if (y >= gameScreenRect.top && y <  outScreenRect.top){
                                    // 타워 버튼영역 외의 터치 시
                                    if(gameManager.canBuildTower(currentTowerList, x, y)) {
                                        gameManager.buildTower(uiButton.currentTowerList, x, y);
                                        uiButton.isStanding = false;
                                        uiButton.initBitmap(BitmapList.BTN_TOWER);
                                        standButton = null;
                                    }
                                }
                                break;
                        }
                    }
                }
            });
            //리스트에 해당 버튼을 추가함
            uiList.add(uiButton);
            uiButtonList.add(uiButton);
        }

    }

    public List<UiObject> getUiList() { return uiList; }

    public List<UiButton> getUiButtonList() { return uiButtonList; }

    public RectF getOutScreenRect() { return outScreenRect; }

    //Ui Classes
    interface UiObject { void draw(Canvas c); }

    //-------------------
    //Text Ui
    //-------------------
    class UiText implements UiObject {
        private PointF position = new PointF();
        private String text;
        private Paint paint = new Paint();

        UiText(String str, float x, float y) { this(str, new PointF(x, y)); }
        UiText(String str, PointF pos) {
            text = str;
            position.set(pos);
            paint.setAntiAlias(true);
        }

        public void setPos(float x, float y) { position.set(x, y); }
        public void setPos(PointF pos) { position.set(pos); }

        public void setStr(String str) { text = str; }

        public Paint getPaint() { return paint; }
        public void setPaint(Paint p) { paint = p; }

        public void draw(Canvas c) {
            c.drawText(text, c.getClipBounds().left + position.x, c.getClipBounds().top + position.y, paint);
        }
    }

    //-------------------
    //Image Ui
    //-------------------
    class UiImage extends Sprite implements UiObject {
        UiImage(BitmapList bitmapList) { init(bitmapList); }

        public void draw(Canvas c) {
            super.draw(c);
        }
    }

    //-------------------
    // Button Class
    //-------------------
    interface Event { void execute(UiButton uiButton, int eventAction, float x, float y); }
    class UiButton extends UiImage implements UiObject {
        private boolean touchable = true;
        private RectF touchBound = new RectF();
        private Event touchEvent;
        private boolean isStanding = false;
        private UiText linkedText;
        private TowerList currentTowerList;

        UiButton(BitmapList bitmapList) {
            super(bitmapList);
            touchBound.set(0, 0, getSpriteWidth(), getSpriteHeight());
        }

        @Override
        protected void setSpriteScale(float perW, float perH) {
            super.setSpriteScale(perW, perH);
            touchBound.set(touchBound.left * perW, touchBound.top * perH,
                    touchBound.right * perW, touchBound.bottom * perH);
        }

        @Override
        protected void setSpriteScale(float per) {
            super.setSpriteScale(per);
            touchBound.set(touchBound.left * per, touchBound.top * per,
                    touchBound.right * per, touchBound.bottom * per);
        }

        public RectF getTouchBoundWithPos() {
            return new RectF(getPosX() - getSpriteWidth()/2 + touchBound.left,
                    getPosY() - getSpriteHeight()/2 + touchBound.top,
                    getPosX() - getSpriteWidth()/2 + touchBound.right,
                    getPosY() - getSpriteHeight()/2 + touchBound.bottom);
        }

        //터치 이벤트를 설정
        public void setTouchEvent(Event e){
            touchEvent = e;
        }

        //설정된 터치 이벤트를 실행
        public void executeTouchEvent(int touchAction, float x, float y) {
            if(touchable && touchEvent != null)
                touchEvent.execute(this, touchAction, x, y);
        }

        public void setTouchable(boolean b){
            touchable = b;

            if(b == false) {
                //버튼 흑백 처리
                batchAllLinkedSprites(new Sprite.Batch() {
                    @Override
                    public void function(Sprite currentSprite) {
                        Paint currentPaint = currentSprite.getPaint();
                        ColorMatrix colorMatrix = new ColorMatrix();
                        colorMatrix.setSaturation(0);
                        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
                        currentPaint.setColorFilter(colorMatrixFilter);
                        currentSprite.setPaint(currentPaint);
                    }
                });
            } else {
                //버튼 흑백 해제 처리
                batchAllLinkedSprites(new Sprite.Batch() {
                    @Override
                    public void function(Sprite currentSprite) {
                        Paint currentPaint = currentSprite.getPaint();
                        currentPaint.setColorFilter(null);
                        currentSprite.setPaint(currentPaint);
                    }
                });
            }
        }

        public void draw(Canvas c) {
            super.draw(c);
            if(isEnabled() && linkedText != null)
                linkedText.draw(c);
        }
    }

}
