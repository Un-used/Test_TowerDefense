package com.example.towerdefense;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by 김재현 on 2017-11-30.
 */

public class StageManager {
    class Field extends Sprite {
        public static final int STATE_EXIST_TOWER = 1;
        public static final int STATE_CANNOT_BUILD_TOWER = 2;

        private int status = 0;

        Field(BitmapList bitmapList) {
            init(bitmapList);

            Paint fPaint = getPaint();
            fPaint.setAntiAlias(false);
            setPaint(fPaint);
        }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
    }

    private RectF gameScreenRect;
    private int stageNum;
    private String[][] mapStr;
    private Field[][] fields;
    private Point mapSize = new Point();
    private PointF tileSize = new PointF();
    private ArrayList<PointF> enemyPathPointF = new ArrayList<PointF>();

    private Bitmap mapFieldBitmap = null;

    public StageManager(){ }

    public void stageInit(int stage, RectF screenRect) {
        enemyPathPointF.clear();

        //스테이지
        stageNum = stage;

        //게임에 표시되는 화면의 Rect 정보
        gameScreenRect = screenRect;

        mapStr = StageList.values()[stage-1].getStageMap();

        //맵의 가로 타일 개수
        mapSize.x = mapStr[0].length;
        //맵의 세로 타일 개수
        mapSize.y = mapStr.length;

        //타일의 width, height 값을 설정
        tileSize.x = gameScreenRect.width() / (float)mapSize.x;
        tileSize.y = gameScreenRect.height() / (float)mapSize.y;

        //맵의 크기에 맞게 field 배열을 선언함
        fields = new Field[mapSize.y][mapSize.x];

        //Field 정보에 맞춰 Path를 생성하는데 필요한 임시 변수들
        class pathFieldInfo {
            public int num;
            public PointF coord;

            pathFieldInfo(int num, PointF coord){
                this.num = num;
                this.coord = coord;
            }
        }
        ArrayList<pathFieldInfo> pathFieldArray = new ArrayList<pathFieldInfo>();
        PointF startPath = null;
        PointF endPath = null;

        //모든 필드에 대해서 bmp 배열에 대입하고 기본 field 상태를 설정함
        for(int y = 0; y < mapSize.y; ++y){
            for(int x = 0; x < mapSize.x; ++x) {

                String fieldStr = mapStr[y][x];
                PointF fieldPos = new PointF(gameScreenRect.left + x * tileSize.x + (tileSize.x/2),
                        gameScreenRect.top + y * tileSize.y + (tileSize.y/2));

                try{
                    //Integer.parseInt() 함수에서 예외가 발생하지 않으면 숫자임
                    int scanNum = Integer.parseInt(fieldStr, 10);

                    fields[y][x] = new Field(BitmapList.TILE_ENEMY_WALKABLE);
                    fields[y][x].status |= Field.STATE_CANNOT_BUILD_TOWER;
                    pathFieldArray.add(new pathFieldInfo(scanNum, fieldPos));
                } catch (NumberFormatException e) {
                    //해당 String이 숫자가 아닌 String일 때 처리됨
                    switch (fieldStr) {
                        case "o": //건설 가능 필드
                            fields[y][x] = new Field(BitmapList.TILE_BUILDABLE);
                            break;
                        case "x": //건설 불가 필드
                            fields[y][x] = new Field(BitmapList.TILE_ENEMY_WALKABLE);
                            fields[y][x].status |= Field.STATE_CANNOT_BUILD_TOWER;
                            break;
                        case "S": //적이 출현할 필드
                            fields[y][x] = new Field(BitmapList.TILE_CREATE_ENEMY);
                            fields[y][x].status |= Field.STATE_CANNOT_BUILD_TOWER;
                            startPath = fieldPos;
                            break;
                        case "E": //적의 최종 목적지 필드
                            fields[y][x] = new Field(BitmapList.TILE_CREATE_ENEMY);
                            fields[y][x].status |= Field.STATE_CANNOT_BUILD_TOWER;
                            endPath = fieldPos;
                            break;
                    }
                } finally {
                    //필드의 좌표와 크기를 설정
                    Field finallyField = fields[y][x];
                    finallyField.setPos(fieldPos);
                    finallyField.setSpriteScale(tileSize.x / finallyField.getBmpWidth(), tileSize.y / finallyField.getBmpHeight());
                }

            }
        }

        Comparator<pathFieldInfo> sortCmp = new Comparator<pathFieldInfo>() {
            public int compare(pathFieldInfo f1, pathFieldInfo f2) {
                return f1.num == f2.num ? 0 : (f1.num > f2.num ? 1 : -1);
            }
        };
        Collections.sort(pathFieldArray, sortCmp);

        if(startPath != null)
            enemyPathPointF.add(startPath);

        for(int i = 0; i < pathFieldArray.size(); ++i)
            enemyPathPointF.add(pathFieldArray.get(i).coord);

        if(endPath != null)
            enemyPathPointF.add(enemyPathPointF.size(), endPath);

    }

    /**
     * 해당 스테이지의 모든 필드를 합친 비트맵을 구함 (GameView 클래스에서 사용함)
     */
    public void createMapFieldBitmap() {
        if(mapFieldBitmap != null) {
            mapFieldBitmap.recycle();
            mapFieldBitmap = null;
        }

        mapFieldBitmap = Bitmap.createBitmap(MainActivity.GAME_SCREEN_SIZE.x, MainActivity.GAME_SCREEN_SIZE.y, Bitmap.Config.ARGB_8888);
        Canvas mapCanvas = new Canvas(mapFieldBitmap);

        for (int y = 0; y < mapSize.y; ++y) {
            for (int x = 0; x < mapSize.x; ++x)
                fields[y][x].draw(mapCanvas);
        }
    }

    public Field getField(Point pos) { return getField(pos.x, pos.y); }
    /**
     * 좌표 번호에 맞는 Field를 가져옴
     * @param x X 번호
     * @param y Y 번호
     * @return 해당 Field
     */
    public Field getField(int x, int y) {
        if(y < 0 || y >= fields.length || x < 0 || x >= fields[y].length)
            return null;

        return fields[y][x];
    }


    public Field getFieldFromPos(PointF pos) { return getFieldFromPos(pos.x, pos.y); }

    /**
     * 좌표에 맞는 Field를 가져옴
     * @param x X 좌표
     * @param y Y 좌표
     * @return 해당 Field
     */
    public Field getFieldFromPos(float x, float y) {
        if(x < gameScreenRect.left || x > gameScreenRect.right
                || y < gameScreenRect.top || y > gameScreenRect.bottom)
            return null;

        int fieldX = (int)((x - gameScreenRect.left) / tileSize.x);
        int fieldY = (int)((y - gameScreenRect.top) / tileSize.y);

        return getField(fieldX, fieldY);
    }

    public Field[][] getFields() { return fields; }

    public PointF getTileSize() { return tileSize; };

    /**
     * 필드들로 만들어진 Bitmap를 반환함 (GameView 클래스에서 사용함)
     * @return
     */
    public Bitmap getMapFieldBitmap() {
        //없으면 생성해서 반환
        if(mapFieldBitmap == null)
            createMapFieldBitmap();

        return mapFieldBitmap;
    }

    public ArrayList<PointF> getEnemyPaths() { return enemyPathPointF; }

}
