package com.example.towerdefense;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by 김재현 on 2017-11-09.
 */

public class Tower extends Unit {
    private static GameManager gameManager;

    private TowerList towerList;
    private int reqCoins;
    private int power;
    private BulletList bulletList;
    private long attackCooldown;
    private boolean waitCooldown = true;
    private float attackRange;
    private Enemy targetEnemy = null;
    private NanoTimer attackTimer = new NanoTimer();
    private boolean attackRepeat = true;

    private boolean canDisable = false;

    public Tower() { }

    public Tower(TowerList towerList, PointF pos) {
        init(towerList, pos.x, pos.y);
    }

    public Tower(TowerList towerList, float x, float y) {
        init(towerList, x, y);
    }

    public boolean init(TowerList towerList, PointF pos) {
        return init(towerList, pos.x, pos.y);
    }

    /**
     * 타워를 초기화 함
     * @param towerList 초기화할 타워 종류
     * @param x 초기화 될 X 좌표
     * @param y 초기화 될 Y 좌표
     * @return 초기화 성공 여부
     */
    public boolean init(TowerList towerList, float x, float y) {
        if(towerList == null)
            return false;

        init(towerList.bitmapList, x, y);

        this.towerList = towerList;

        if(this.towerList.bulletListOrdinal != -1)
            this.bulletList = BulletList.values()[towerList.bulletListOrdinal];
        else
            this.bulletList = null;

        reqCoins = towerList.reqCoins;
        power = towerList.power;
        attackCooldown = towerList.attackCooldown;
        attackRange = towerList.attackRange;

        setDirection(MathF.random(0f, 360f));

        canDisable = false;

        return true;
    }

    //최적의 적을 찾는 메소드 (사거리 내의 가장 가까운 Enemy를 반환)
    protected Enemy searchBestTarget() {
        ObjectPool<Enemy> enemies = gameManager.getEnemies();

        int searchEnemyNum = enemies.searchBestObject(new ObjectPool.Search() {
            @Override
            public boolean condition(Object currentObject, @Nullable Object prevObject) {
                final Enemy currentEnemy = (Enemy)currentObject;

                if(canAttackTarget(currentEnemy)){
                    if(prevObject == null || distanceTo(currentEnemy) < distanceTo((Enemy)prevObject))
                        return true;
                }

                return false;
            }
        });

        if (searchEnemyNum == -1)
            return null;

        return enemies.get(searchEnemyNum);
    }

    /**
     * 해당 적이 공격가능한지 확인하는 메소드
     * @param target 공격할 Enemy
     * @return 공격 가능 여부
     */
    protected boolean canAttackTarget(Enemy target) {

        if(target.getHp() <= 0)
            return false;

        if(distanceTo(target) > attackRange)
            return false;


        return true;
    }

    /**
     * 적을 공격하는 메소드 (스크립트 명령어에서 호출됨)
     * @param attackDistance Bullet이 생성될 Polar Distance
     */
    protected void attack(float attackDistance) {
        if(targetEnemy == null)
            return;

        if(!waitCooldown)
            return;

        if(bulletList.bulletAction == Bullet.BULLET_ACTION_APPEAR_ON_TARGET)
            gameManager.createBullet(bulletList, targetEnemy.getPosX(), targetEnemy.getPosY(), targetEnemy, power);
        else
            gameManager.createBullet(bulletList, getPosX() + polarTo(attackDistance).x,
                    getPosY() + polarTo(attackDistance).y, targetEnemy, power);

        waitCooldown = false;
        attackTimer.writeTime();
    }

    /**
     * 업데이트시 호출될 메소드
     * @return true 반환 시 오브젝트 풀에서 비활성화 됨
     */
    protected boolean update() {
        if(isEnabled()) {
            //쿨타임이 다 되면 공격 가능 설정
            if (!waitCooldown) {
                if (attackTimer.getElapsedTime() >= attackCooldown)
                    waitCooldown = true;
            }

            //현재 목표 타겟이 없거나 공격 불가능 하면 다시 설정
            if (attackRepeat) {
                if (targetEnemy == null || !canAttackTarget(targetEnemy)) {
                    //if (getScriptType() != SCRIPT_IDLE)
                    //    setScriptType(SCRIPT_IDLE);
                    targetEnemy = searchBestTarget();
                    attackRepeat = true;
                }
            }

            //최종적으로 목표한 타겟이 존재하면
            if (targetEnemy != null) {
                //타겟 쪽으로 방향을 돌린다
                setDirection(degreeTo(targetEnemy));
                //공격 가능한 조건이 갖춰졌다면 공격 스크립트로 넘어감
                if (attackRepeat && waitCooldown) {
                    setScriptType(SCRIPT_ATTACK);
                    attackRepeat = false;
                }
            }

            runScript();
        }

        return canDisable;
    }

    /**
     * Tower의 스크립트 명령어 처리 메소드
     * @param inst 스크립트 명령어와 인자 정보
     * @return true 반환 시 스크립트 재생 턴을 마침
     */
    @Override
    protected boolean executeScriptInst(Object[] inst){
        switch ((int)inst[0]) {

            case Script.INST_ATTACK:
                attack((float)inst[1]);
                return false;

            case Script.INST_ATTACK_REPEAT:
                attackRepeat = true;
                return false;

            case Script.INST_END:
                stopScript = true;
                canDisable = true;
                return true;

            default:
                return super.executeScriptInst(inst);
        }
    }

    public TowerList getTowerList() { return towerList; }

    public void setTargetAuto() { targetEnemy = searchBestTarget(); }

    public void setTargetEnemy(Enemy enemy) { targetEnemy = enemy; }
    public Enemy getTargetEnemy() { return targetEnemy; }

    //타워 클래스에서 GameManager의 데이터를 가져와야 하므로 static으로 초기 설정
    public static void setGameManager(GameManager gameMgr) { gameManager = gameMgr; }
}
