package com.example.towerdefense;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

/**
 * Created by 김재현 on 2017-11-21.
 */

public abstract class Script {
    //Script Type 구분 상수
    public static final int SCRIPT_IDLE = 0;
    public static final int SCRIPT_DEATH = 1;
    public static final int SCRIPT_MOVE = 2;
    public static final int SCRIPT_ATTACK = 3;

    //*****************************************************
    //Animation 명령어
    //명령어에 관한 처리 코드는 Sprite 클래스에 있음
    //작성 예시: 1 프레임을 출력한다 => {INST_PLAY_FRAME, 1}

    //프레임을 설정한다. [인자 1개: (int)설정할 프레임]
    public static final int INST_SET_FRAME = 0;

    //대기한다. [인자 1개: (long)대기할 NanoTime]
    public static final int INST_WAIT = 1;

    //숨김을 설정한다. [인자 없음]
    public static final int INST_SET_HIDDEN = 2;

    //숨김 설정을 해제한다. [인자 없음]
    public static final int INST_UNSET_HIDDEN = 3;

    //이동속도만큼 이동한다. (Enemy 전용) [인자 없음]
    public static final int INST_MOVE = 4;

    //적을 공격한다. (Tower 전용) [인자 없음]
    public static final int INST_ATTACK = 5;

    //적을 공격한다. (Tower 전용) [인자 1개: (float)Bullet이 생성될 Polar Distance 거리]
    public static final int INST_ATTACK_REPEAT = 6;

    //적에게 데미지를 준다. (Bullet 전용) [인자 없음]
    public static final int INST_BULLET_HIT = 7;

    //지정 값만큼 회전한다. [인자 1개: (float)회전할 각도]
    public static final int INST_ROTATE = 8;

    //회전 값을 설정한다. [인자 1개: (float)설정할 Direction 값]
    public static final int INST_SET_DIRECTION = 9;

    //일정 값 사이의 난수만큼 회전 값을 설정한다.
    //[인자 2개: (float)설정할 Direction 최소 값, (float)설정할 Direction 최대 값]
    public static final int INST_SET_DIRECTION_RANDOM = 10;

    //크기를 조절한다.
    //[인자 2개: (float)설정할 scaleX 값, (float)설정할 scaleY 값]
    public static final int INST_SET_SCALE = 11;

    //스크립트 실행을 중단한다. [인자 없음]
    public static final int INST_STOP = 61;

    //해당 타입의 스크립트를 실행한다. [인자 1개: (int)실행할 SCRIPT TYPE]
    public static final int INST_JUMP_SCRIPT_TYPE = 62;

    //스프라이트를 제거한다. [인자 없음]
    public static final int INST_END = 63;

    //*****************************************************

    protected Object[][][] script;
    protected int scriptType = SCRIPT_IDLE;
    protected int scriptPointer = 0;
    protected NanoTimer scriptTimer = new NanoTimer();
    protected long scriptWaitTime = 0;
    protected boolean stopScript = false;

    /**
     * 해당 BitmapList에 맞는 스크립트를 설정함
     * @param bitmapList 스크립트를 가져올 BitmapList
     */
    protected void setBitmapScript(BitmapList bitmapList) {
        if(bitmapList.scriptListOrdinal != -1)
            script = ScriptList.values()[bitmapList.scriptListOrdinal].getScript();
        else
            script = null;

        setScriptType(SCRIPT_IDLE);
    }

    /**
     * 애니메이션 타입을 설정함
     * @param scriptType 설정할 애니메이션 타입(public static 선언 값 참고)
     */
    protected void setScriptType(int scriptType) {
        //스크립트가 유효하지 않으면 무시함
        if(script == null || script.length <= scriptType ||
                script[scriptType] == null || script[scriptType].length == 0)
            return;

        this.scriptType = scriptType;
        scriptPointer = 0;
        scriptWaitTime = 0;
        stopScript = false;
        scriptTimer.writeTime();
    }

    //스크립트 명령어에 맞게 처리할 코드를 구성
    //Script 상속 시 반드시 오버라이드 할 것
     protected void runScript() {}
}
