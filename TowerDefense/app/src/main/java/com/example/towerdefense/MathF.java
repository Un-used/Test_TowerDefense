package com.example.towerdefense;

import android.graphics.PointF;

import java.util.Random;

/**
 * Created by 김재현 on 2017-11-22.
 */

public class MathF {
    //랜덤 클래스를 미리 생성
    private static Random r = new Random();

    //좌표와 좌표 사이의 거리를 구함
    public static float distance(PointF pos, PointF target) {
        return (float) Math.sqrt((pos.x - target.x) * (pos.x - target.x) + (pos.y - target.y) * (pos.y - target.y));
    }

    //현재 좌표 기준으로 상대 좌표의 방향을 구함 (degree값으로 반환)
    // 주의! 북쪽 방향을 기준으로 0도, 오른쪽 방향으로 값이 증가함
    public static float degree(PointF pos, PointF target) {
        return (float) Math.toDegrees(Math.atan2(target.y - pos.y, target.x - pos.x)) + 90f;
    }

    //해당 방향에 해당 거리만큼 이동 시의 좌표를 구함
    public static PointF polar(float degree, float distance){
        return new PointF((float) Math.cos( Math.toRadians(degree - 90f)) * distance,
                (float) Math.sin( Math.toRadians(degree - 90f)) * distance);
    }

    //min 이상, max 이하의 랜덤 값을 구함
    public static float random(float min, float max){
        if(min == max)
            return min;

        //min과 max 값 위치가 바뀌었으면 자동으로 설정함
        if(min > max){
            float tmp = min;
            min = max;
            max = tmp;
        }

        r.setSeed(System.nanoTime());

        return min + r.nextFloat()*(max-min);
    }

}
