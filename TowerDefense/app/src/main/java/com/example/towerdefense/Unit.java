package com.example.towerdefense;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by 김재현 on 2017-11-03.
 */

/**
 * Tower, Enemy의 베이스로 사용될 클래스
 */
public abstract class Unit extends Sprite {
    //충돌 범위
    private RectF bound = new RectF();

    protected Unit() { }

    protected Unit(BitmapList bitmapList) { init(bitmapList); }

    protected Unit(BitmapList bitmapList, PointF pos) { init(bitmapList, pos); }

    protected Unit(BitmapList bitmapList, float x, float y) { init(bitmapList, x, y);}

    @Override
    protected void initBitmap(BitmapList bitmapList) {
        super.initBitmap(bitmapList);
        bound.set(0f, 0f, (float)getBmpWidth(), (float)getBmpHeight());
    }

    @Override
    protected void setSpriteScale(float perW, float perH) {
        super.setSpriteScale(perW, perH);
        bound.set(bound.left * perW, bound.top * perH, bound.right * perW, bound.bottom * perH);
    }

    @Override
    protected void setSpriteScale(float per) {
        super.setSpriteScale(per);
        bound.set(bound.left * per, bound.top * per, bound.right * per, bound.bottom * per);
    }

    //충돌 범위 관련 메소드
    protected RectF getBound() { return bound; }
    protected void setBound(RectF r){ bound.set(r); }

    /**
     * 현재 위치를 가져와 충돌 범위를 화면 좌표 기준으로 반환함
     * @return 충돌 범위(화면 좌표 기준)
     */
    protected RectF getBoundWithPos() {
        return new RectF(getPosX() - getSpriteWidth()/2 + bound.left,
                getPosY() - getSpriteHeight()/2 + bound.top,
                getPosX() - getSpriteWidth()/2 + bound.right,
                getPosY() - getSpriteHeight()/2 + bound.bottom); }
}
