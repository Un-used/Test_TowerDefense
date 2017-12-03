package com.example.towerdefense;

/**
 * Created by 김재현 on 2017-09-21.
 */

public class NanoTimer {
    //타이머 속도 관련 static 정의
    private static float timerSpeed = 1.0f;
    public static float getTimerSpeed() { return timerSpeed; }
    public static void setTimerSpeed(float speed) { timerSpeed = speed; }


    //NanoTimer 객체 함수
    private long writtenTime;

    public NanoTimer() { writeTime(); }

    /**
     * 현재시간을 저장함
     */
    public void writeTime() { writtenTime = System.nanoTime(); }

    /**
     * 저장된 시간에 지정 값을 더함
     */
    public void addWrittenTime(long time) { writtenTime += (long)(time / timerSpeed); }

    /**
     * 저장 시간을 반환함
     * @return 저장되었던 시간
     */
    public long getWrittenTime() {
        return writtenTime;
    }

    /**
     * 이 함수가 호출된 시점의 시간과 저장되었던 시간의 차를 반환함
     * @return 경과 시간
     */
    public long getElapsedTime() {
        return (long) ((System.nanoTime() - writtenTime) * timerSpeed);
    }

}
