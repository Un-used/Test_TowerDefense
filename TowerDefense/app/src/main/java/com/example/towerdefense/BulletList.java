package com.example.towerdefense;

/**
 * Created by 김재현 on 2017-11-24.
 */

public enum BulletList {
    BULLET_BOWGUN                   (BitmapList.BULLET_BOWGUN, Bullet.BULLET_ACTION_MISSILE_DONT_FOLLOW, 67f, 67f),
    BULLET_TANK                     (BitmapList.BULLET_TANK, Bullet.BULLET_ACTION_MISSILE_DONT_FOLLOW, 60f, 75f),
    BULLET_MACHINEGUN               (BitmapList.BULLET_MACHINEGUN, Bullet.BULLET_ACTION_APPEAR_ON_TARGET, 0f, 0f),
    ;

    public final BitmapList bitmapList;
    public final int bulletAction;
    public final float startSpeed;
    public final float maxSpeed;

    BulletList(BitmapList bitmapList, int bulletAction, float startSpeed, float maxSpeed) {
        this.bitmapList = bitmapList;
        this.bulletAction = bulletAction;
        this.startSpeed = startSpeed;
        this.maxSpeed = maxSpeed;
    }
}//end BulletList