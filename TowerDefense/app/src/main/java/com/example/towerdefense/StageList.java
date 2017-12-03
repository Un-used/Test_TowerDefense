package com.example.towerdefense;

/**
 * Created by 김재현 on 2017-11-30.
 */

public enum StageList {
    STAGE_001           (

            new String[][] {
                    {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
                    {"o", "S", "x", "1", "o", "o", "o", "o", "o", "o", "o", "o"},
                    {"o", "o", "o", "x", "o", "4", "x", "x", "5", "o", "o", "o"},
                    {"o", "o", "o", "x", "o", "x", "o", "o", "x", "o", "o", "o"},
                    {"o", "o", "o", "2", "x", "3", "o", "o", "6", "x", "E", "o"},
                    {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
            },

            new Object[]{
                    150L, 10000000000L
            },

            new TowerList[]{
                    TowerList.BOWGUN,
                    TowerList.TANK,
                    TowerList.MACHINEGUN
            },

            new Object[][]{
            {
                EnemyList.STAGE001_01_RED, 20, 30999999999L, 20
            },
            {
                EnemyList.STAGE001_02_YELLOW, 20, 30999999999L, 30
            },
            {
                EnemyList.STAGE001_03_GRAY, 20, 30999999999L, 40
            },
            {
                EnemyList.STAGE001_BOSS, 1, 45000000000L, 50
            }}
    ),

    STAGE_002           (

            new String[][] {
                    {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
                    {"o", "4", "x", "x", "x", "x", "x", "x", "x", "x", "E", "o"},
                    {"o", "x", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
                    {"o", "3", "x", "x", "x", "x", "x", "x", "x", "x", "2", "o"},
                    {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "x", "o"},
                    {"o", "S", "x", "x", "x", "x", "x", "x", "x", "x", "1", "o"},
                    {"o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o"},
            },

            new Object[]{
                    200L, 10000000000L
            },

            new TowerList[]{
                    TowerList.BOWGUN,
                    TowerList.TANK,
                    TowerList.MACHINEGUN
            },

            new Object[][]{
                    {
                            EnemyList.STAGE001_01_RED, 20, 30999999999L
                    },
                    {
                            EnemyList.STAGE001_02_YELLOW, 20, 30999999999L
                    },
                    {
                            EnemyList.STAGE001_03_GRAY, 20, 30999999999L
                    },
                    {
                            EnemyList.STAGE001_BOSS, 1, 45000000000000L
                    }}
    ),
    ;

    private final String[][] map;
    private final Object[] stageInfo;
    private final TowerList[] buildableTowerList;
    private final Object[][] levelInfo;
    public final int levelLength;

    StageList(String[][] map, Object[] stageInfo, TowerList[] buildableTowerList, Object[][] levelInfo) {
        this.map = map;
        this.stageInfo = stageInfo;
        this.buildableTowerList = buildableTowerList;
        this.levelInfo = levelInfo;
        levelLength = levelInfo.length;
    }

    public String[][] getStageMap() {
        return map;
    }

    public Long getStageFirstCoins() {
        return (Long) stageInfo[0];
    }

    public Long getStageFirstLimitTime() {
        return (Long) stageInfo[1];
    }

    public TowerList[] getBuildableTowerList() { return buildableTowerList; }

    public EnemyList getStageEnemy(int level) {
        return (EnemyList) levelInfo[level][0];
    }

    public int getStageEnemyAmount(int level) {
        return (int) levelInfo[level][1];
    }

    public long getStageTime(int level) {
        return (long) levelInfo[level][2];
    }
}