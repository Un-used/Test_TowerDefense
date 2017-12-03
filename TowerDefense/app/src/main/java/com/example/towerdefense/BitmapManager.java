package com.example.towerdefense;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by 김재현 on 2017-11-06.
 */

public class BitmapManager {
    //Bitmap 데이터를 저장해 둘 메모리
    private static Bitmap[][] bitmapArray;

    //BitmapList 목록에 있는 모든 리스트들을 불러와 메모리에 올려준다.
    public static void bitmapListInit(Context context) {

        //BitmapList를 가져온다.
        BitmapList[] bitmapList = BitmapList.values();
        //가져온 BitmapList의 길이만큼 메모리를 할당한다.
        bitmapArray = new Bitmap[bitmapList.length][];

        Resources res = context.getResources();

        //BitmapList의 목록 데이터를 통해 Drawable의 이미지 파일을 Bitmap로 변환해준다.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        for(int i = 0; i < bitmapList.length; ++i) {
            BitmapList currentList = bitmapList[i];

            //비트맵의 길이가 1이라면
            if(currentList.length == 1){
                Bitmap bmp = BitmapFactory.decodeResource(res, currentList.resId, options);
                if(currentList.scaleX != 1.0f || currentList.scaleY != 1.0f){
                    bmp = Bitmap.createScaledBitmap(bmp,
                            (int)(bmp.getWidth() * currentList.scaleX),
                            (int)(bmp.getHeight() * currentList.scaleY), true);
                }

                bitmapArray[currentList.ordinal()] = new Bitmap[1];
                bitmapArray[currentList.ordinal()][0] = bmp;
            }
            //비트맵의 길이가 1이 아니라면
            else{
                if(currentList.x < 1 || currentList.y < 1)
                    continue;

                bitmapArray[currentList.ordinal()] = new Bitmap[currentList.length];

                Bitmap bmp = BitmapFactory.decodeResource(res, currentList.resId, options);
                int bmpWidth = bmp.getWidth() / currentList.x;
                int bmpHeight = bmp.getHeight() / currentList.y;
                for(int k = 0; k < currentList.y; ++k) {
                    for (int j = 0; j < currentList.x; ++j) {
                        //만약 비트맵 크기 수정이 필요하다면
                        if(currentList.scaleX != 1.0f || currentList.scaleY != 1.0f){
                            Bitmap cutBmp = Bitmap.createBitmap(bmp, bmpWidth * j, bmpHeight * k, bmpWidth, bmpHeight);
                            cutBmp = Bitmap.createScaledBitmap(cutBmp,
                                    (int)(cutBmp.getWidth() * currentList.scaleX),
                                    (int)(cutBmp.getHeight() * currentList.scaleY), true);
                            bitmapArray[currentList.ordinal()][k * currentList.x + j] = cutBmp;
                        }
                        //크기 수정이 필요없을 때
                        else {
                            bitmapArray[currentList.ordinal()][k * currentList.x + j] =
                                    Bitmap.createBitmap(bmp, bmpWidth * j, bmpHeight * k, bmpWidth, bmpHeight);
                        }
                    }
                }
            }

        }
    }

    public static Bitmap[] getBitmap(BitmapList bitmapList){
        return bitmapArray[bitmapList.ordinal()];
    }
}//end BitmapManager
