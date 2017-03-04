package tadakazu1972.nekohiroi;

/**
 * Created by tadakazu on 2017/03/04.
 */

public class SmallBaloon {
    public float x, y;
    public int index;
    public boolean visible;

    public SmallBaloon(){
        x = 0.0f;
        y = 0.0f;
        index = 0;
        visible = false;
    }

    public void Reset(){
        x = 0.0f;
        y = 0.0f;
        index = 0;
        visible = false;
    }

    public void Set(float x1, float y1){
        x = x1;
        y = y1 - 16.0f;
        index = 0;
        visible = true;
    }

    public void Move(float x1, float y1){
        x = x1;
        y = y1 - 16.0f;
        //アニメーションインデックス変更処理
        index=index+4;
        if ( index > 39 ) {
            index = 0;
        }
    }
}
