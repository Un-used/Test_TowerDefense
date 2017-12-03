package com.example.towerdefense;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Sprite extends Script {
    private Sprite prevSprite = null;
    private Sprite nextSprite = null;

    private boolean isEnabled = false;
    private boolean isHidden;
    private boolean isRotatable;

    private PointF position = new PointF(0f, 0f);
    private float direction = 0;

    private BitmapList bitmapList;
    private Bitmap[] bitmap;
    private int bmpWidth, bmpHeight;
    private PointF scale = new PointF(1.0f, 1.0f);
    private Paint paint = new Paint();
    private int frameNum = 0;


    protected Sprite() {}

    /**
     * 스프라이트를 초기화 함
     * @param bitmapList 초기화할 bitmapList
     * @return 초기화 성공 여부
     */
    protected boolean init(BitmapList bitmapList) { return init(bitmapList, 0, 0); }

    protected boolean init(BitmapList bitmapList, PointF pos) { return init(bitmapList, pos.x, pos.y); }

    /**
     * 스프라이트를 초기화 함
     * @param bitmapList 스프라이트에 설정할 BitmapList
     * @param x 스프라이트의 초기 X 좌표
     * @param y 스프라이트의 초기 Y 좌표
     * @return 초기화 성공 여부
     */
    protected boolean init(BitmapList bitmapList, float x, float y){
        if(bitmapList == null)
            return false;

        isEnabled = true;
        isHidden = false;
        isRotatable = true;

        initBitmap(bitmapList);
        paint.setAntiAlias(true);

        position.set(x, y);
        scale.set(1.0f, 1.0f);
        setDirection(0);

        return true;
    }

    /**
     * 비트맵을 설정함 (기존 애니메이션 설정은 초기화)
     * @param bitmapList 설정할 Bitmap의 List
     */
    protected void initBitmap(BitmapList bitmapList) {
        if(bitmapList == null)
            return;

        this.clearBitmap();

        this.bitmapList = bitmapList;

        bitmap = BitmapManager.getBitmap(bitmapList);

        bmpWidth = bitmap[0].getWidth();
        bmpHeight = bitmap[0].getHeight();

        setBitmapScript(bitmapList);
        setScriptType(SCRIPT_IDLE);
    }

    protected BitmapList getBitmapList() { return bitmapList; }

    /**
     * 스프라이트 크기를 설정함 (비트맵 크기를 수정하는 것이 아님)
     * @param perW 설정한 프레임 크기의 가로 비율
     * @param perH 설정한 프레임 크기의 세로 비율
     */
    protected void setSpriteScale(float perW, float perH) { scale.set(perW, perH); }

    /**
     * 스프라이트 크기를 설정함 (비트맵 크기를 수정하는 것이 아님)
     * @param per 설정한 프레임 크기 비율
     */
    protected void setSpriteScale(float per) {
        scale.set(per, per);
    }

    /**
     * 현재 설정된 이미지 비트맵을 제거함
     */
    protected void clearBitmap(){
        bitmap = null;
        frameNum = -1;
    }

    /**
     * 현재 상태의 비트맵을 반환함
     * @return 반환된 비트맵
     */
    protected Bitmap getBitmap() {
        if(bitmap == null)
            return null;

        return bitmap[this.getFrameNum()];
    }

    /**
     * 해당 프레임의 비트맵을 반환함
     * @return 반환된 비트맵
     */
    protected Bitmap getCurrentBitmap(int num){
        if(bitmap == null)
            return null;

        return bitmap[num % bitmap.length];
    }

    /**
     * 이미지의 프레임 넘버 설정
     * @param num 설정할 프레임 넘버
     */
    protected void setFrameNum(int num){
        if(bitmap == null)
            return;

        frameNum = num % bitmap.length;
    }

    /**
     * 현재 프레임 번호를 반환함
     */
    protected int getFrameNum(){
        if(frameNum == -1)
            return 0;

        return frameNum;
    }


    /**
     * 현재 Sprite의 하단에 해당 Sprite를 추가함
     * @param currentSprite 하단에 추가할 Sprite
     */
    protected void addLowerSprite(Sprite currentSprite, boolean alignCenter) {
        if(alignCenter == true){
            currentSprite.setPosWithLinkedSprites(position.x, position.y);
        }
        if(prevSprite != null) { //하단 프레임이 존재할 때
            Sprite currentLowermostSprite = currentSprite.getLowermostSprite();
            prevSprite.nextSprite = currentLowermostSprite;
            currentLowermostSprite.prevSprite = prevSprite;
        }
        Sprite currentUppermostSprite = currentSprite.getUppermostSprite();
        prevSprite = currentUppermostSprite;
        currentUppermostSprite.nextSprite = this;
    }

    /**
     * 최하단에 해당 Sprite를 추가함
     * @param currentSprite 최하단에 추가할 Sprite
     */
    protected void addLowermostSprite(Sprite currentSprite, boolean alignCenter) {
        if(alignCenter == true){
            currentSprite.setPosWithLinkedSprites(position.x, position.y);
        }
        Sprite lowermostSprite = getLowermostSprite();
        Sprite currentUppermostSprite = currentSprite.getUppermostSprite();
        currentUppermostSprite.nextSprite = lowermostSprite;
        lowermostSprite.prevSprite = currentUppermostSprite;
    }

    /**
     * 최하단 Sprite를 구함
     * @return 최하단 Sprite
     */
    protected Sprite getLowermostSprite() {
        Sprite currentSprite = this;
        while (currentSprite.prevSprite != null){
            currentSprite = currentSprite.prevSprite;
        }

        return currentSprite;
    }


    /**
     * 현재 Sprite의 상단에 해당 Sprite를 추가함
     * @param currentSprite 상단에 추가할 Sprite
     */
    protected void addUpperSprite(Sprite currentSprite, boolean alignCenter) {
        if(alignCenter == true){
            currentSprite.setPosWithLinkedSprites(position.x, position.y);
        }
        if(nextSprite != null) { //상단 프레임이 존재할 때
            Sprite currentUppermostSprite = currentSprite.getUppermostSprite();
            nextSprite.prevSprite = currentUppermostSprite;
            currentUppermostSprite.nextSprite = prevSprite;
        }
        Sprite currentLowermostSprite = currentSprite.getLowermostSprite();
        nextSprite = currentLowermostSprite;
        currentLowermostSprite.prevSprite = this;
    }

    /**
     * 최상단에 해당 Sprite를 추가함
     * @param currentSprite 최상단에 추가할 Sprite
     */
    protected void addUppermostSprite(Sprite currentSprite, boolean alignCenter) {
        if(alignCenter == true){
            currentSprite.setPosWithLinkedSprites(position.x, position.y);
        }
        Sprite uppermostSprite = getUppermostSprite();
        Sprite currentLowermostSprite = currentSprite.getLowermostSprite();
        currentLowermostSprite.prevSprite = uppermostSprite;
        uppermostSprite.nextSprite = currentLowermostSprite;
    }

    /**
     * 최상단 Sprite를 구함
     * @return 최상단 Sprite
     */
    protected Sprite getUppermostSprite() {
        Sprite currentSprite = this;
        while (currentSprite.nextSprite != null){
            currentSprite = currentSprite.nextSprite;
        }

        return currentSprite;
    }

    interface Batch { void function(Sprite currentSprite); }
    /**
     * 현재 스프라이트와 연결된 모든 스프라이트에 해당함수를 실행함
     * @param batchFunction 연결된 모든 스프라이트에 대해 실행할 함수
     */
    protected void batchAllLinkedSprites(Batch batchFunction) {
        for(Sprite currentSprite = getLowermostSprite(); currentSprite != null; currentSprite = currentSprite.nextSprite) {
            batchFunction.function(currentSprite);
        }
    }

    protected void setPos(float x, float y){ position.x = x; position.y = y;}
    protected void setPos(PointF p){ position.set(p);}
    protected PointF getPos() { return position; }

    protected void setPosX(float x){ position.x = x; }
    protected float getPosX() { return position.x; }

    protected void setPosY(float y){ position.y = y; }
    protected float getPosY() { return position.y; }

    protected void setPosWithLinkedSprites(PointF p){ setPosWithLinkedSprites(p.x, p.y); }
    protected void setPosWithLinkedSprites(float x, float y){
        final float moveX = x - position.x;
        final float moveY = y - position.y;
        batchAllLinkedSprites(new Batch() {
            @Override
            public void function(Sprite sprite) {
                sprite.move(moveX, moveY);
            }
        });
    }

    protected int getBmpLength() { return bitmap.length; }

    protected int getBmpWidth() { return bmpWidth; }

    protected int getBmpHeight() { return bmpHeight; }

    protected float getScaleX() { return scale.x; }

    protected float getScaleY() { return scale.y; }

    protected float getSpriteWidth() { return (float)bmpWidth * scale.x; }

    protected float getSpriteHeight() { return (float)bmpHeight * scale.y; }

    protected float getDirection() { return direction; }
    protected void setDirection(float dgr) {
        while (dgr < 0)
            dgr += 360f;

        direction = dgr % 360f;
    }

    protected int getScriptType() { return scriptType; }

    protected void setPaint(Paint p) { paint = p; }
    protected Paint getPaint() { return paint; }

    protected void setEnabled(boolean b) { isEnabled = b; }
    protected boolean isEnabled() { return isEnabled; }

    protected void setRotatable(boolean b) { isRotatable = b; }
    protected boolean isRotatable() { return isRotatable; }

    protected void setHidden(boolean b) { isHidden = b; }
    protected boolean isHidden() { return isHidden; }

    /**
     * 스크립트 명령어에 따라 처리함
     */
    @Override
    protected void runScript() {
        if(script != null && stopScript == false) {
            //스크립트 대기 시간이 지날 시 스크립트를 실행함
            if (scriptTimer.getElapsedTime() >= scriptWaitTime) {
                scriptTimer.addWrittenTime(scriptWaitTime);
                scriptWaitTime = 0;

                boolean canBreak = false;
                while (canBreak == false) {
                    canBreak = executeScriptInst(script[scriptType][scriptPointer++]);
                }
            }
        }
    }

    /**
     * 스크립트 명령어에 따른 처리 메소드
     * @param inst 스크립트 명령어
     * @return runScript()의 break 여부
     */
    protected boolean executeScriptInst(Object[] inst) {
        switch ((int)inst[0]){
            //프레임 설정
            case Script.INST_SET_FRAME:
                setFrameNum((int)inst[1]);
                return false;

            //스크립트 대기
            case Script.INST_WAIT:
                scriptWaitTime = (long)inst[1];
                return true;

            //숨김 설정
            case Script.INST_SET_HIDDEN:
                isHidden = true;
                return false;

            //숨김 해제
            case Script.INST_UNSET_HIDDEN:
                isHidden = false;
                return false;

            case Script.INST_ROTATE:
                setDirection(getDirection()+(float)inst[1]);
                return false;

            case Script.INST_SET_DIRECTION:
                setDirection((float)inst[1]);
                return false;

            case Script.INST_SET_DIRECTION_RANDOM:
                setDirection(MathF.random((float)inst[1], (float)inst[2]));
                return false;

            case Script.INST_SET_SCALE:
                setSpriteScale((float)inst[1], (float)inst[2]);
                return false;

            //스크립트 정지
            case Script.INST_STOP:
                stopScript = true;
                return true;

            //스크립트 타입을 변경함
            case Script.INST_JUMP_SCRIPT_TYPE:
                setScriptType((int)inst[1]);
                return false;

            //현재 클래스 단위에서 구현 불가능한 명령어는 무시함
            default:
                return false;
        }
    }

    //해당 스프라이트의 polar값을 가져옴
    protected PointF polarTo(float distance) { return MathF.polar(direction, distance); }

    //해당 스프라이트와 대상(좌표)과의 거리를 구함
    protected float distanceTo(PointF targetPos){ return MathF.distance(getPos(), targetPos); }
    protected float distanceTo(Sprite target){
        return MathF.distance(getPos(), target.getPos());
    }

    //해당 스프라이트 기준 대상(좌표)을 바라보는 방향을 구함
    protected float degreeTo(PointF targetPos) { return MathF.degree(getPos(), targetPos); }
    protected float degreeTo(Sprite target){
        return MathF.degree(getPos(), target.getPos());
    }

    //해당 스프라이트 기준 대상(좌표)을 바라보도록 설정함
    protected void lookAt(PointF targetPos) { setDirection(degreeTo(targetPos)); }
    protected void lookAt(Sprite target) { setDirection(degreeTo(target)); }

    /**
     * 현재 방향의 앞 거리만큼 이동함
     * @param distance 이동할 거리
     */
    protected void moveForward(float distance) { move(polarTo(distance)); }

    /**
     * 이미지 이동
     * @param p 이동할 거리 정보가 담긴 PointF
     */
    protected void move(PointF p) {
        position.x += p.x;
        position.y += p.y;
    }

    /**
     * 이미지 이동
     * @param x 이동할 X 좌표의 크기
     * @param y 이동할 Y 좌표의 크기
     */
    protected void move(float x, float y) {
        position.x += x;
        position.y += y;
    }

    /**
     * 그릴 수 있는 영역을 계산하여 잘라주는 함수
     * @param bound 그릴 수 있는 범위
     * @param src 그릴 이미지 영역
     * @param dst 이미지를 그릴 영역
     */
    protected void cutInBound(RectF bound, Rect src, RectF dst){
        if(dst.left < bound.left)
            src.left += (int)((bound.left - dst.left) / getScaleX());

        if(dst.top < bound.top)
            src.top += (int)((bound.top - dst.top) / getScaleY());

        if(dst.right > bound.right)
            src.right -= (int)((dst.right - bound.right) / getScaleX());

        if(dst.bottom > bound.bottom)
            src.bottom -= (int)((dst.bottom - bound.bottom) / getScaleY());

        dst.setIntersect(dst, bound);
    }


    protected void draw(Canvas c) { draw(c, new RectF(c.getClipBounds())); };

    /**
     * 스프라이트 그리기 요청
     * @param c 그릴 Canvas
     * @param drawableRect 그릴 수 있는 Rect 영역
     */
    protected void draw(Canvas c, RectF drawableRect) {
        //비활성화 상태면 그리지 않음 (링크된 스프라이트 모두 포함)
        if(!isEnabled)
            return;

        getLowermostSprite().drawSprite(c, drawableRect);
    }


    private void drawSprite(Canvas c) { drawSprite(c, new RectF(c.getClipBounds())); }

    /**
     * 스프라이트를 그림
     * @param c 그릴 Canvas
     * @param drawableRect 그릴 수 있는 Rect 영역
     */
    private void drawSprite(Canvas c, RectF drawableRect) {
        //그릴 수 있는 조건이 맞아야 그림
        if(isEnabled || !isHidden || bitmap != null || drawableRect != null) {

            final Rect origRect = c.getClipBounds();
            RectF dst = new RectF(origRect.left + position.x - (bmpWidth * (scale.x / 2)),
                    origRect.top + position.y - (bmpHeight * (scale.y / 2)),
                    origRect.left + position.x + (bmpWidth * (scale.x / 2)),
                    origRect.top + position.y + (bmpHeight * (scale.y / 2)));

            //스프라이트가 회전이 필요한 상태라면
            if (isRotatable && direction != 0f) {
                //Canvas 상태 저장
                c.save();

                //그릴 수 있는 범위로 설정하고 Canvas를 회전함
                c.clipRect(drawableRect);
                c.rotate(direction, origRect.left + position.x, origRect.top + position.y);

                //그리려는 영역이 그릴 수 있는 영역 안에 있으면 그림
                final RectF CanvasRect = new RectF(c.getClipBounds());
                if (RectF.intersects(CanvasRect, dst)) {
                    Rect src = new Rect(0, 0, bmpWidth, bmpHeight);
                    cutInBound(CanvasRect, src, dst);

                    c.drawBitmap(bitmap[this.getFrameNum()], src, dst, paint);
                }

                //Canvas 복구
                c.restore();
            } else {
                //그리려는 영역이 그릴 수 있는 영역 안에 있으면 그림
                if (RectF.intersects(drawableRect, dst)) {
                    Rect src = new Rect(0, 0, bmpWidth, bmpHeight);
                    cutInBound(drawableRect, src, dst);

                    c.drawBitmap(bitmap[this.getFrameNum()], src, dst, paint);
                }


            }

        }

        //다음 스프라이트가 존재하면 그림
        if(nextSprite != null)
            nextSprite.drawSprite(c);
    }

}