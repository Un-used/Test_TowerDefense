package com.example.towerdefense;

import android.graphics.PointF;

/**
 * Created by 김재현 on 2017-11-24.
 */

public class Bullet extends Sprite {
    //Bullet의 생성 및 유도 방식에 관한 상수 정의
    public static final int BULLET_ACTION_MISSILE_FOLLOW = 0;
    public static final int BULLET_ACTION_MISSILE_DONT_FOLLOW = 1;
    public static final int BULLET_ACTION_APPEAR_ON_TARGET = 2;

    private BulletList bulletList;

    private int bulletAction;
    private Enemy targetEnemy;
    private PointF targetPos = new PointF();
    private float damage;
    private float maxSpeed;
    private float currentSpeed;
    private boolean canDisable = false;

    Bullet() { }

    Bullet(BulletList bulletList, PointF pos, Enemy target, float dmg) {
        init(bulletList, pos.x, pos.y, target, dmg);
    }

    Bullet(BulletList bulletList, float x, float y, Enemy target, float dmg) {
        init(bulletList, x, y, target, dmg);
    }

    public boolean init(BulletList bulletList, PointF pos, Enemy target, float dmg) {
        return init(bulletList, pos.x, pos.y, target, dmg);
    }

    /**
     * Bullet을 초기화함
     * @param bulletList 초기화할 BulletList
     * @param x 초기화될 X좌표 값
     * @param y 초기화될 Y좌표 값
     * @param target 공격 타겟
     * @param dmg 공격 데미지
     * @return 초기화 성공 여부
     */
    public boolean init(BulletList bulletList, float x, float y, Enemy target, float dmg) {
        //bulletList가 있어야만 초기화 됨
        if(bulletList == null)
            return false;

        //enemyList에 설정된 Bitmap으로 스프라이트 이미지 설정
        init(bulletList.bitmapList, x, y);

        this.bulletList = bulletList;

        bulletAction = bulletList.bulletAction;
        maxSpeed = bulletList.maxSpeed;
        currentSpeed = bulletList.startSpeed;

        //타겟을 설정하고 타겟을 바라보도록 함
        targetEnemy = target;
        targetPos.set(targetEnemy.getPos());
        lookAt(targetEnemy);

        damage = dmg;

        //초기화 되자마자 이동 스크립트 실행
        setScriptType(SCRIPT_MOVE);

        canDisable = false;

        return true;
    }

    /**
     * 현재 설정된 타겟 지점으로 이동함 (스크립트 명령어에 의해 호출됨)
     */
    public void moveToTarget() {

        switch (bulletAction){

            //시전자 위치에서 생성되며 적을 따라감
            case BULLET_ACTION_MISSILE_FOLLOW:
                if(targetEnemy != null)
                    targetPos.set(targetEnemy.getPos());

                lookAt(targetPos);

                if(distanceTo(targetPos) <= currentSpeed) {
                    setPos(targetPos);
                    setScriptType(SCRIPT_DEATH);
                }
                else {
                    if(getScriptType() != SCRIPT_MOVE)
                        setScriptType(SCRIPT_MOVE);

                    moveForward(currentSpeed);
                }
                break;

            //시전자 위치에서 생성되며 적을 따라가지 않음
            case BULLET_ACTION_MISSILE_DONT_FOLLOW:
                if(distanceTo(targetPos) <= currentSpeed) {
                    setPos(targetPos);
                    setScriptType(SCRIPT_DEATH);
                }
                else {
                    if(getScriptType() != SCRIPT_MOVE)
                        setScriptType(SCRIPT_MOVE);

                    moveForward(currentSpeed);
                }
                break;

            //대상 위치에 바로 생성됨 (생성 위치 설정은 Tower에 있음)
            case BULLET_ACTION_APPEAR_ON_TARGET:
                if(getScriptType() != SCRIPT_DEATH){
                    setScriptType(SCRIPT_DEATH);
                }
                break;

            //명시된 방식 외에는 무시함
            default:
                break;
        }

    }

    /**
     * 업데이트시 호출될 메소드
     * @return true 반환 시 오브젝트 풀에서 비활성화 됨
     */
    public boolean update() {
        if(isEnabled()) {
            runScript();

            //Death 스크립트 재생이 아니라면 속도가 조절됨
            if (getScriptType() != SCRIPT_DEATH
                    && maxSpeed != currentSpeed) {
                if (maxSpeed - currentSpeed <= 1f)
                    currentSpeed = maxSpeed;
                else
                    currentSpeed += (maxSpeed/(maxSpeed-currentSpeed));
            }
        }

        return canDisable;
    }

    /**
     * Bullet의 스크립트 명령어 처리 메소드
     * @param inst 스크립트 명령어와 인자 정보
     * @return true 반환 시 스크립트 재생 턴을 마침
     */
    @Override
    public boolean executeScriptInst(Object[] inst){
        switch ((int)inst[0]) {

            case Script.INST_MOVE:
                moveToTarget();
                return false;

            case Script.INST_BULLET_HIT:
                if(targetEnemy != null)
                    targetEnemy.hitDamage(damage);
                return false;

            case Script.INST_END:
                stopScript = true;
                canDisable = true;
                return true;

            default:
                return super.executeScriptInst(inst);
        }
    }

    public BulletList getBulletList() { return bulletList; }

    public float getDamage() { return damage; }

    public void setDamage(float dmg) { damage = dmg; }

}//end Bullet