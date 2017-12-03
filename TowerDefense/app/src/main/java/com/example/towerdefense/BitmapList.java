package com.example.towerdefense;

import android.support.annotation.Nullable;

// *** 작성시 주의사항 ***
// Sprite 클래스에서 사용시 SprScript가 적용됨
public enum BitmapList {
    TOWER_BOWGUN                            (R.drawable.tower000, 2, 1, 3.5f, 3.5f, ScriptList.TOWER_BOWGUN),
    TOWER_TANK                              (R.drawable.tower001, 6, 1, 3.5f, 3.5f, ScriptList.TOWER_TANK),
    TOWER_MACHINEGUN                       (R.drawable.tower002, 3, 1, 3.5f, 3.5f, ScriptList.TOWER_MACHINEGUN),

    BULLET_BOWGUN                            (R.drawable.bullet000, 1, 1, 3.5f, 3.5f, ScriptList.BULLET_BOWGUN),
    BULLET_TANK                              (R.drawable.bullet001, 7, 1, 3.5f, 3.5f, ScriptList.BULLET_TANK),
    BULLET_MACHINEGUN                       (R.drawable.bullet002, 7, 1, 2.5f, 2.5f, ScriptList.BULLET_MACHINEGUN),

    ENENMY_RED                                (R.drawable.enemy000, 3, 1, 3.5f, 3.5f,  ScriptList.ENEMY_RED),
    ENENMY_YELLOW                             (R.drawable.enemy001, 3, 1, 3.5f, 3.5f,  ScriptList.ENEMY_RED),
    ENENMY_GRAY                               (R.drawable.enemy002, 3, 1, 3.5f, 3.5f,  ScriptList.ENEMY_RED),
    ENENMY_TURTLE                             (R.drawable.enemy003, 9, 1, 3.5f, 3.5f,  ScriptList.ENEMY_TURTLE),

    TILE_BUILDABLE                          (R.drawable.field000, 1, 1, 1.0f, 1.0f,  null),
    TILE_ENEMY_WALKABLE                     (R.drawable.field001, 1, 1, 1.0f, 1.0f,  null),
    TILE_CREATE_ENEMY                       (R.drawable.field002, 1, 1, 1.0f, 1.0f,  null),

    BTN_TOWER_BACKGROUND                    (R.drawable.button_tower_backgnd, 1, 1, 1.0f, 1.0f,  null),
    BTN_TOWER                                 (R.drawable.button_tower, 1, 1, 1.0f, 1.0f,  null),
    BTN_TOWER_SELECT                        (R.drawable.button_tower_select, 1, 1, 1.0f, 1.0f,  null),
    ;

    public final int resId;
    public final int x;
    public final int y;
    public final float scaleX;
    public final float scaleY;
    public final int length;
    public final int scriptListOrdinal;

    BitmapList(int resId, int x, int y, float scaleX, float scaleY, @Nullable ScriptList scriptList) {
        this.resId = resId;
        this.x = x;
        this.y = y;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.length = this.x * this.y;
        if(scriptList != null)
            this.scriptListOrdinal = scriptList.ordinal();
        else
            this.scriptListOrdinal = -1;
    }
}//end BitmapList