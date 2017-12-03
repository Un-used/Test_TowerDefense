package com.example.towerdefense;

import android.support.annotation.Nullable;

/**
 * Created by 김재현 on 2017-11-25.
 */

public class ObjectPool<T> {
    //Template 기능을 위한 Class 객체
    private final Class<T> clazz;

    //ObjectPool에 할당되는 객체
    private final Object[] objectPool;
    private final boolean[] objectEnabled;
    private int enableAmount;

    public ObjectPool(int size, Class<T> cls) {
        clazz = cls;

        //선언한 사이즈만큼 객체를 생성함
        objectPool = new Object[size];
        for(int i = 0; i < objectPool.length; ++i) {
            try {
                objectPool[i] = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        objectEnabled = new boolean[size];

        //오브젝트풀 초기화
        reset();
    }

    public void reset() {
        for(int i = 0; i < objectEnabled.length; ++i){
            objectEnabled[i] = false;
        }
        enableAmount = 0;
    }

    /**
     * @return 비활성화된 오브젝트 넘버를 반환
     */
    public int getDisabledNum() {
        for(int i = 0; i < objectEnabled.length; ++i){
            if(objectEnabled[i] == false)
                return i;
        }

        return -1;
    }


    /**
     * 해당 넘버의 오브젝트를 가져옴
     * @param num 가져올 오브젝트의 넘버
     * @return 해당 넘버의 오브젝트
     */
    public T get(int num){
        return clazz.cast(objectPool[num]);
    }


    /**
     * 오브젝트의 활성화 여부를 설정
     * @param num 활성화 여부가 설정될 오브젝트의 넘버
     * @param b 활성화 여부
     */
    public void setObjectEnabled(int num, boolean b) {

        if(objectEnabled[num] != b) {
            if (b)
                ++enableAmount;
            else
                --enableAmount;

            objectEnabled[num] = b;
        }

    }

    interface Batch {
        void function(Object currentObject, int objNum);
    }
    /**
     * 현재 활성화된 오브젝트에 해당함수를 실행함
     * @param batchFunction 모든 오브젝트에 대해 실행할 함수
     */
    protected void batchEnabled(ObjectPool.Batch batchFunction) {
        if(enableAmount == 0)
            return;

        final int saveAmount = enableAmount;
        int count = 0;
        for(int i = 0; i < objectPool.length; ++i) {

            if(objectEnabled[i]) {
                batchFunction.function(objectPool[i], i);
                ++count;

                if(count >= saveAmount) {
                    break;
                }
            }

        }

    }


    interface Search {
        boolean condition(Object currentObject, @Nullable Object prevObject);
    }
    /**
     * 조건에 맞는 최적의 오브젝트를 반환함
     * @param searchCondition 검색할 오브젝트의 조건
     */
    protected int searchBestObject(ObjectPool.Search searchCondition) {
        if(enableAmount == 0)
            return -1;

        int searched = -1;

        int count = 0;
        for(int i = 0; i < objectPool.length; ++i) {

            if(objectEnabled[i]) {
                boolean replaceable = false;

                if(searched != -1)
                    replaceable = searchCondition.condition(objectPool[i], objectPool[searched]);
                else
                    replaceable = searchCondition.condition(objectPool[i], null);


                if(replaceable)
                    searched = i;

                ++count;

                if(count >= enableAmount)
                    break;
            }

        }

        return searched;
    }

    public int getEnableAmount() { return enableAmount; }
}
