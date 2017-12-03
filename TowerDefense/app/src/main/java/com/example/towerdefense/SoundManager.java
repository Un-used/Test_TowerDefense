package com.example.towerdefense;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * SoundManager의 구현이 완벽하지 않음
 * 아직 이 게임에서 Sound를 사용하지 않음
 */
public class SoundManager {
    private SoundPool soundPool;

    private final int soundStreamChannel = 16;
    private static int maxSound = SoundList.values()[SoundList.values().length-1].num+1;
    private static int[] soundIdList = new int[maxSound];

    SoundManager(Context context) {
        soundInit(context);
    }

    /**
     * 사운드를 메모리에 올림
     */
    public void soundInit(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(soundStreamChannel).build();
        } else {
            soundPool = new SoundPool(soundStreamChannel, AudioManager.STREAM_NOTIFICATION, 0);
        }

        SoundList[] soundList = SoundList.values();
        for(int i = 0; i < soundList.length; ++i) {
            soundIdList[soundList[i].num] = soundPool.load(context, soundList[i].resId, 1);
        }
    }

    /**
     * 로드했던 사운드 메모리를 모두 해제함
     */
    public void soundRelease() {
        for(int i = 0; i < maxSound; ++i){
            soundPool.unload(soundIdList[i]);
        }
        soundPool.release();
        soundPool = null;
    }

    /**
     * 사운드를 재생함
     * @param currentSound 재생할 사운드
     * @return 재생된 사운드의 streamId 값
     */
    public int playSound(SoundList currentSound){
        return this.playSound(currentSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * 사운드를 재생함
     * @param currentSound 재생할 사운드
     * @param leftVolume 왼쪽 볼륨
     * @param rightVolume 오른쪽 볼륨
     * @param priority 우선 순위
     * @param loop 반복 수
     * @param rate 재생 속도
     * @return 재생된 사운드의 streamId 값
     */
    public int playSound(SoundList currentSound, float leftVolume, float rightVolume, int priority, int loop, float rate){
        if(currentSound.soundId == -1)
            return -1;
        return soundPool.play(currentSound.soundId, leftVolume, rightVolume, priority, loop, rate);
    }

    // *** 작성시 주의사항 ***
    // num 값은 중복되면 안됨
    // num 값이 가장 큰 원소는 반드시 맨 밑에 위치해야 함
    public enum SoundList {

        ;

        private final int num;
        public final int resId;
        public final int soundId;

        SoundList(int num, int resId) {
            this.num = num;
            this.resId = resId;
            soundId = soundIdList[num];
        }
    }
}