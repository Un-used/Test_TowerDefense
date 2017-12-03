package com.example.towerdefense;

/**
 * Created by 김재현 on 2017-11-21.
 */

public enum ScriptList {
    TOWER_BOWGUN            (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_STOP}
            },


            //3.ATTACK
            {
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_ATTACK, 16f},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_ATTACK_REPEAT},
                    {Script.INST_STOP}
            },
    }),

    TOWER_TANK              (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_STOP}
            },

            //3.ATTACK
            {
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_ATTACK, 10f},
                    {Script.INST_WAIT, 35000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_WAIT, 35000000L},
                    {Script.INST_SET_FRAME, 3},
                    {Script.INST_WAIT, 35000000L},
                    {Script.INST_SET_FRAME, 4},
                    {Script.INST_WAIT, 75000000L},
                    {Script.INST_SET_FRAME, 5},
                    {Script.INST_WAIT, 40000000L},
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_ATTACK_REPEAT},
                    {Script.INST_STOP}
            },
    }),

    TOWER_MACHINEGUN              (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_STOP}
            },

            //3.ATTACK
            {
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_ATTACK, 0f},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_ATTACK_REPEAT},
                    {Script.INST_WAIT, 25000000L},
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },
    }),

    BULLET_BOWGUN           (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_BULLET_HIT},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_JUMP_SCRIPT_TYPE, Script.SCRIPT_MOVE}
            },
    }),

    BULLET_TANK             (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_SET_SCALE, 1.5f, 1.5f},
                    {Script.INST_SET_DIRECTION_RANDOM, 0f, 360f},
                    {Script.INST_BULLET_HIT},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 3},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 4},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 5},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 6},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_JUMP_SCRIPT_TYPE, Script.SCRIPT_MOVE}
            },
    }),


    BULLET_MACHINEGUN             (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_SET_DIRECTION_RANDOM, 0f, 360f},
                    {Script.INST_BULLET_HIT},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 3},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 4},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 5},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_SET_FRAME, 6},
                    {Script.INST_WAIT, 50000000L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_JUMP_SCRIPT_TYPE, Script.SCRIPT_MOVE}
            },
    }),

    ENEMY_RED                (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 66666667L},
                    {Script.INST_SET_FRAME, 1},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 66666667L},
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 66666667L},
                    {Script.INST_SET_FRAME, 2},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 66666667L},
                    {Script.INST_JUMP_SCRIPT_TYPE, Sprite.SCRIPT_MOVE}
            },
    }),

    ENEMY_TURTLE            (new Object[][][] {
            //0.IDLE
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_STOP}
            },

            //1.DEATH
            {
                    {Script.INST_SET_FRAME, 0},
                    {Script.INST_WAIT, 33333333L},
                    {Script.INST_END}
            },

            //2.MOVE
            {
                    {Script.INST_SET_FRAME, 4},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 5},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 6},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 7},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 8},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_SET_FRAME, 9},
                    {Script.INST_MOVE},
                    {Script.INST_WAIT, 100000000L},
                    {Script.INST_JUMP_SCRIPT_TYPE, Sprite.SCRIPT_MOVE}
            },
    }),
    ;

    private Object[][][] script;

    ScriptList(Object[][][] script){
        this.script = script;
    }

    protected Object[][][] getScript() { return script; }
}