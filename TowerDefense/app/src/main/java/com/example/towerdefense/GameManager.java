package com.example.towerdefense;

import android.content.Context;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 김재현 on 2017-09-21.
 */

public class GameManager {
    private SoundManager soundManager;
    private UiManager uiManager;
    private GameView gameView;
    private RectF gameScreenRect;
    private StageManager stageManager;

    private GameThread gameThread = new GameThread();

    //건설 가능한 타워 리스트
    private List<TowerList> buildableTowers = Collections.synchronizedList(new ArrayList<TowerList>());

    //예상 최대사용 수를 고려해 적절한 길이만큼 오브젝트풀 생성
    private ObjectPool<Bullet> bullets = new ObjectPool<Bullet>(256, Bullet.class);
    private ObjectPool<Tower> towers = new ObjectPool<Tower>(128, Tower.class);
    private ObjectPool<Enemy> enemies = new ObjectPool<Enemy>(128, Enemy.class);

    private final ObjectPool.Batch BULLET_UPDATE = new ObjectPool.Batch() {
        @Override
        public void function(Object currentObject, int objNum) {
            final Bullet currentBullet = (Bullet)currentObject;

            if(currentBullet.update())
                //제거된 Bullet은 오브젝트 풀에서 비활성화
                bullets.setObjectEnabled(objNum, false);
        }
    };
    private final ObjectPool.Batch TOWER_UPDATE  = new ObjectPool.Batch() {
        @Override
        public void function(Object currentObject, int objNum) {
            final Tower currentTower = (Tower)currentObject;

            if(currentTower.update()) {
                //제거된 Tower는 오브젝트 풀에서 비활성화
                towers.setObjectEnabled(objNum, false);
                //Tower가 제거됨에 따라 해당 필드에 타워 존재여부를 다시 수정함
                StageManager.Field fieldUnderTower = stageManager.getFieldFromPos(currentTower.getPos());
                fieldUnderTower.setStatus(fieldUnderTower.getStatus() & ~StageManager.Field.STATE_EXIST_TOWER);
            }
        }
    };
    private final ObjectPool.Batch ENEMY_UPDATE  = new ObjectPool.Batch() {
        @Override
        public void function(Object currentObject, int objNum) {
            final Enemy currentEnemy = (Enemy)currentObject;

            if(currentEnemy.update()) {
                //subLife가 0이면 이동 중 저지된 것으로 인식
                if(currentEnemy.getSubLife() == 0)
                    coins += currentEnemy.getEnemyList().reward;
                //subLife가 0이 아니라면 목적지점까지 이동한 것으로 인식하고 life가 소모됨
                else
                    lives -= currentEnemy.getSubLife();
                //제거된 Enemy는 오브젝트 풀에서 비활성화
                enemies.setObjectEnabled(objNum, false);

                //타워 중에서 삭제된 적을 목표 대상으로 삼고 있었다면 다른 목표로 재설정함
                towers.batchEnabled(new ObjectPool.Batch() {
                    @Override
                    public void function(Object currentObject, int num) {
                        Tower currentTower = (Tower)currentObject;
                        if(currentTower.getTargetEnemy() == currentEnemy)
                            currentTower.setTargetAuto();
                    }
                });
            }
        }
    };

    private NanoTimer enemyTimer = new NanoTimer();
    private NanoTimer gameTimer = new NanoTimer();
    private long limitTime = 0;
    private int stage = 1;
    private int stageLevel = 0;
    private EnemyList stageEnemyList = null;
    private int stageEnemyAmount = 0;
    private long coins = 0;
    private int lives = 10;

    public GameManager(SoundManager soundMgr, UiManager UiMgr){
        Tower.setGameManager(this);
        soundManager = soundMgr;
        uiManager = UiMgr;
        uiManager.setGameManager(this);
        gameScreenRect = new RectF(0, MainActivity.GAME_SCREEN_SIZE.y/12, MainActivity.GAME_SCREEN_SIZE.x, MainActivity.GAME_SCREEN_SIZE.y*5/6);

        stageManager = new StageManager();
        stageManager.stageInit(stage, gameScreenRect);

        //게임 속도 설정
        //setGameSpeed(1.0f);

        // 시험용 스테이지 생성 코딩
        gameInit(stage);
        //

        gameTimer.writeTime();

        gameThread.setRunning(true);
        gameThread.start();
    }

    /**
     * View로부터 터치 이벤트를 수신할 시 실행되는 메소드
     */
    public void reactTouchEvent(int touchAction, float x, float y){
        uiManager.reactTouchEvent(touchAction, x, y);
    }

    /**
     * 스테이지를 기준으로 게임을 초기화함
     * @param stage 초기화할 스테이지
     */
    public void gameInit(int stage) {
        buildableTowers.clear();
        towers.reset();
        bullets.reset();
        enemies.reset();

        StageList stageInfo = StageList.values()[stage - 1];

        TowerList[] buildableTowerList = stageInfo.getBuildableTowerList();
        for(int i = 0; i < buildableTowerList.length; ++i)
            buildableTowers.add(buildableTowerList[i]);

        stageLevel = 0;
        stageManager.stageInit(stage, gameScreenRect);
        stageManager.createMapFieldBitmap();
        coins = stageInfo.getStageFirstCoins();
        limitTime = stageInfo.getStageFirstLimitTime();

        uiManager.refreshUi();

        gameTimer.writeTime();
    }

    /**
     * 타워 건설이 가능한 지 확인하는 메소드
     * @param towerList 건설할 타워 종류
     * @param x 건설 X 좌표
     * @param y 건설 Y 좌표
     * @return 건설 가능 여부
     */
    public boolean canBuildTower(TowerList towerList, float x, float y){
        //목록에 없는 타워의 건설 요청인지 확인
        boolean check = false;
        for(int i = 0; i < buildableTowers.size(); ++i){
            if(buildableTowers.get(i) == towerList) {
                check = true;
                break;
            }
        }
        if(check == false)
            return false;

        //건설 좌표가 게임화면을 벗어났는지 확인
        if(!gameScreenRect.contains(x, y)){
            return false;
        }

        //해당 좌표의 필드 상태가 건설 가능인지 확인
        int currentFieldState = stageManager.getFieldFromPos(x, y).getStatus();
        if((currentFieldState
                & (StageManager.Field.STATE_EXIST_TOWER | StageManager.Field.STATE_CANNOT_BUILD_TOWER)) != 0) {
            return false;
        }

        //타워 건설에 필요한 코인이 부족한지 확인
        if(coins < towerList.reqCoins)
            return false;

        return true;
    }

    /**
     * 타워 건설 메소드
     * @param towerList 건설할 타워 종류
     * @param x 건설 X 좌표
     * @param y 건설 Y 좌표
     * @return 건설된 타워 객체
     */
    public Tower buildTower(TowerList towerList, float x, float y) {
        //건설 요구조건이 맞지 않으면 무시함
        if(!canBuildTower(towerList, x, y))
            return null;

        //건설 가능한 것이 확인되면 타워를 추가함

        //오브젝트 풀에 여분의 객체가 없으면 생성하지 못함
        int emptyNum = towers.getDisabledNum();
        if(emptyNum == -1)
            return null;

        //건설될 좌표를 설정함
        float buildX = x - ((x - gameScreenRect.left) % stageManager.getTileSize().x) + stageManager.getTileSize().x/2;
        float buildY = y - ((y - gameScreenRect.top) % stageManager.getTileSize().y) + stageManager.getTileSize().y/2;

        //타워 건설에 필요한 비용만큼 코인 감소
        coins -= towerList.reqCoins;

        //오브젝트 풀에서 가져온 객체(Tower) 초기화
        Tower tower = towers.get(emptyNum);
        tower.init(towerList, buildX, buildY);
        towers.setObjectEnabled(emptyNum, true);

        //타워가 지어진 위치의 필드 속성에 타워 건설불가를 설정
        StageManager.Field currentField = stageManager.getFieldFromPos(buildX, buildY);
        currentField.setStatus(currentField.getStatus() | StageManager.Field.STATE_EXIST_TOWER);

        return tower;
    }

    /**
     * Enemy 생성 (이동 경로는 현재 스테이지에 설정된 경로를 사용함)
     * @param enemyList 생성할 Enemy 종류
     * @return 생성된 Enemy 객체
     */
    public Enemy createEnemy(EnemyList enemyList){
        //Enemy 오브젝트풀에서 사용되지 않은 객체의 번호를 가져옴
        int emptyNum = enemies.getDisabledNum();

        //없으면 null 반환
        if(emptyNum == -1)
            return null;

        //오브젝트풀의 비활성화 객체를 가져옴
        Enemy enemy = enemies.get(emptyNum);
        //가져온 객체를 초기화함
        enemy.init(enemyList, stageManager.getEnemyPaths());
        //해당 객체가 활성화 되었다고 설정해줌
        enemies.setObjectEnabled(emptyNum, true);

        //활성화 된 객체를 반환
        return enemy;
    }

    /**
     * Bullet 생성
     * @param bulletList 생성할 Bullet 종류
     * @param x Bullet이 생성될 X 좌표
     * @param y Bullet이 생성될 Y 좌표
     * @param target Bullet이 삼을 타겟
     * @param dmg Bullet에 설정될 데미지
     * @return 생성된 Bullet 객체
     */
    public Bullet createBullet(BulletList bulletList, float x, float y, Enemy target, float dmg){
        int emptyNum = bullets.getDisabledNum();
        if(emptyNum == -1)
            return null;

        Bullet bullet = bullets.get(emptyNum);
        bullet.init(bulletList, x, y, target, dmg);
        bullets.setObjectEnabled(emptyNum, true);

        return bullet;
    }

    //--------------------
    //게임 스레드
    //--------------------
    public class GameThread extends Thread {
        //1.0배속 기준 약 0.0167초(1/60s)에 한번씩 스레드가 실행됨
        private final long UPDATE_TIME = 16666666L;
        private boolean mRun = false;
        private NanoTimer timer = new NanoTimer();

        public GameThread() {}

        @Override
        public void run() {
            while (mRun) {
                //게임 루프 타임에 맞춰 스레드를 정지해준 후 진행한다.
                long sleepTime = UPDATE_TIME - timer.getElapsedTime();
                if(sleepTime > 0) {
                    try {
                        this.sleep(sleepTime / 1000000L, (int)(sleepTime % 1000000L));
                    } catch (InterruptedException e) { }
                }

                //해당 스레드를 동기화한 후 게임 흐름을 진행한다.
                synchronized (this) {

                    //게임 진행 코드

                    //제한 시간이 다 되면
                    if(getRemainTime() <= 0){
                        StageList stageInfo = StageList.values()[stage - 1];

                        //다음 레벨이 없을 시 모두 초기화하고 다음 스테이지로 넘어감
                        if(stageLevel >= stageInfo.levelLength) {
                            gameInit(++stage);
                        }
                        //다음 레벨이 있다면 다음 레벨로 넘어감
                        else {
                            stageEnemyList = stageInfo.getStageEnemy(stageLevel);
                            stageEnemyAmount = stageInfo.getStageEnemyAmount(stageLevel);
                            limitTime = stageInfo.getStageTime(stageLevel);

                            ++stageLevel;

                            enemyTimer.writeTime();
                            enemyTimer.addWrittenTime(-1000000000L);

                            gameTimer.writeTime();
                        }
                    }

                    //적 생성
                    if(stageEnemyAmount != 0 && enemyTimer.getElapsedTime() >= 1000000000L) {
                        createEnemy(stageEnemyList).setScriptType(Script.SCRIPT_MOVE);
                        --stageEnemyAmount;
                        enemyTimer.addWrittenTime(1000000000L);
                    }

                    //탄환 업데이트
                    bullets.batchEnabled(BULLET_UPDATE);

                    //타워 업데이트
                    towers.batchEnabled(TOWER_UPDATE);

                    //적 업데이트
                    enemies.batchEnabled(ENEMY_UPDATE);

                }

                timer.addWrittenTime(UPDATE_TIME);
            }
        }

        public void setRunning(boolean b){
            mRun = b;
        }
    }

    public GameThread getGameThread() { return gameThread; }

    //게임 스피드를 설정
    // 주의! NanoTimer의 시간 배속을 설정하는 관계로 모든 NanoTimer의 반환 시간이 배속에 의해 수정되어 반환됨
    // 즉 게임 외의 구간에서 사용될 NanoTimer에도 영향을 미침
    public boolean setGameSpeed(float speed) {
        if(speed <= 0)
            return false;

        NanoTimer.setTimerSpeed(speed);
        if(gameView != null)
            gameView.setViewFPS(Math.min(60f*speed, 60f));

        return true;
    }

    public StageManager getStageManager() { return stageManager; }

    public RectF getGameScreenRect() { return gameScreenRect; }

    public List<TowerList> getBuildableTowerList() { return buildableTowers; }

    public ObjectPool<Bullet> getBullets() { return bullets; }

    public ObjectPool<Tower> getTowers() { return towers; }

    public ObjectPool<Enemy> getEnemies() { return enemies; }

    public long getRemainTime() { return Math.max(0, limitTime - gameTimer.getElapsedTime()); }

    public long getCoins() { return coins; }

    public int getLives() { return lives; }

    public void setGameView(GameView gameV) { gameView = gameV; }
}
