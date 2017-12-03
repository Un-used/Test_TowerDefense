package com.example.towerdefense;

import android.support.annotation.Nullable;

/**
 * Created by 김재현 on 2017-11-21.
 */

public enum TowerList {
    BOWGUN                            (BitmapList.TOWER_BOWGUN, 20, 10, 250000000L, 200f, BulletList.BULLET_BOWGUN),
    TANK                               (BitmapList.TOWER_TANK, 25, 35, 1000000000L, 275f, BulletList.BULLET_TANK),
    MACHINEGUN                        (BitmapList.TOWER_MACHINEGUN, 25, 8, 175000000L, 250f, BulletList.BULLET_MACHINEGUN),
    ;

    public final BitmapList bitmapList;
    public final int reqCoins;
    public final int power;
    public final long attackCooldown;
    public final float attackRange;
    public final int bulletListOrdinal;

    TowerList(BitmapList bitmapList, int reqCoins, int power, long attackCooldown, float attackRange, @Nullable BulletList bulletList) {
        this.bitmapList = bitmapList;
        this.reqCoins = reqCoins;
        this.power = power;
        this.attackCooldown = attackCooldown;
        this.attackRange = attackRange;
        if(bulletList != null)
            this.bulletListOrdinal = bulletList.ordinal();
        else
            this.bulletListOrdinal = -1;
    }
}