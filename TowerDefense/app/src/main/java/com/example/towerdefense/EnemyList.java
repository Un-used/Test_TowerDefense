package com.example.towerdefense;

import android.graphics.Paint;

public enum EnemyList {
    STAGE001_01_RED                                 (BitmapList.ENENMY_RED, 180, 0, 12.0f, 1, 3),
    STAGE001_02_YELLOW                              (BitmapList.ENENMY_YELLOW, 250, 0, 12.0f, 1, 4),
    STAGE001_03_GRAY                                (BitmapList.ENENMY_GRAY, 330, 0, 12.0f, 1, 5),
    STAGE001_BOSS                                    (BitmapList.ENENMY_TURTLE, 3500, 2, 5.0f, 5, 50)
    ;

    public final BitmapList bitmapList;
    public final int hp;
    public final int armor;
    public final float moveSpeed;
    public final int subLife;
    public final long reward;

    EnemyList(BitmapList bitmapList, int hp, int armor, float moveSpeed, int subLife, long reward) {
        this.bitmapList = bitmapList;
        this.hp = hp;
        this.armor = armor;
        this.moveSpeed = moveSpeed;
        this.subLife = subLife;
        this.reward = reward;
    }
}