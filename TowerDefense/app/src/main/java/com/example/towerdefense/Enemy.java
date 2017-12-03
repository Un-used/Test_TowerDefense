package com.example.towerdefense;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 김재현 on 2017-11-09.
 */

public class Enemy extends Unit {
    private EnemyList enemyList;

    private int maxHp;
    private int hp;
    private int armor;
    private float moveSpeed;

    private List<PointF> paths;
    private PointF targetPath;
    private int targetPathNumber;

    private int subLife;
    private boolean canDisable = false;

    public Enemy() { }

    public Enemy(EnemyList enemyList, ArrayList<PointF> pathArray) {
        init(enemyList, pathArray);
    }

    /**
     * Enemy를 초기화함
     * @param enemyList 초기화할 EnemyList
     * @param pathArray Enemy에 설정될 Path 정보 배열
     * @return 초기화 성공 여부
     */
    public boolean init(EnemyList enemyList, ArrayList<PointF> pathArray) {
        //enemyList가 있어야만 초기화됨
        if(enemyList == null)
            return false;

        //enemyList에 설정된 Bitmap으로 스프라이트 이미지 설정
        super.init(enemyList.bitmapList);

        this.enemyList = enemyList;
        maxHp = enemyList.hp;
        hp = maxHp;
        armor = enemyList.armor;
        moveSpeed = enemyList.moveSpeed;
        subLife = 0;
        //적 이동 경로가 있으면 설정해줌
        if(pathArray != null) {
            paths = pathArray;
            setPos(paths.get(0));
            targetPathNumber = 1;
            targetPath = paths.get(1);
            lookAt(targetPath);
        } else {
            paths = null;
            targetPathNumber = 0;
        }

        canDisable = false;

        return true;
    }

    public void movePath(float moveDistance) {
        //타겟까지 이동해야 할 거리
        float targetDistance = distanceTo(targetPath);

        //타겟까지의 거리가 이동 가능한 거리보다 짧다면 그 다음 타겟으로 남은 이동거리만큼 더 이동함(반복)
        while (moveDistance >= targetDistance){
            //다음 타겟 경로가 없다면
            if(paths.size() <= targetPathNumber+1) {
                setPos(targetPath);
                targetPath = null;
                setScriptType(Script.SCRIPT_IDLE);

                //감소될 라이프 설정
                subLife = enemyList.subLife;
                canDisable = true;

                return;
            }

            moveDistance -= targetDistance;

            setPos(targetPath);
            targetPath = paths.get(++targetPathNumber);
            targetDistance = distanceTo(targetPath);
        }

        lookAt(targetPath);
        moveForward(moveDistance);
    }

    /**
     * Bullet에 의해 공격받을 때 호출되는 메소드
     * @param dmg 공격받은 데미지
     */
    public void hitDamage(float dmg) {
        dmg -= armor;

        if(dmg > 0) {
            hp -= dmg;
            if (hp <= 0)
                setScriptType(SCRIPT_DEATH);
        }
    }

    /**
     * 업데이트시 호출될 메소드
     * @return true 반환 시 오브젝트 풀에서 비활성화 됨
     */
    public boolean update() {
        if(isEnabled()) {
            runScript();
        }

        return canDisable;
    }

    /**
     * Enemy 스크립트 명령어 처리 메소드
     * @param inst 스크립트 명령어와 인자 정보
     * @return true 반환 시 스크립트 재생 턴을 마침
     */
    @Override
    public boolean executeScriptInst(Object[] inst){
        switch ((int)inst[0]) {

            case Script.INST_MOVE:
                movePath(moveSpeed);
                return false;

            case Script.INST_END:
                stopScript = true;
                canDisable = true;
                return true;

            default:
                return super.executeScriptInst(inst);
        }
    }

    public EnemyList getEnemyList() { return enemyList; }

    public int getHp() { return hp; }

    public int getSubLife() { return subLife; }
}//end Enemy
