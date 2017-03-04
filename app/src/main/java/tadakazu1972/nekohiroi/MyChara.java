package tadakazu1972.nekohiroi;

/**
 * Created by tadakazu on 2015/09/22.
 */
public class MyChara {
    public float x, y;
    public float wx, wy;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public int hp;
    public int str;
    public int def;
    public int exp;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int second_jump; //2段ジャンプフラグ
    public int ichigo; //いちごフラグ 0:もっていない 1:もっている
    public int status; //0:ノーマル 1:
    public int animal; //動物ディスプレイのナンバー
    public int stairway; //階段フラグ 0:ない 1:階段の上にいる
    public int shotIndex; //ショットの順番
    public int damage; //ダメージ表示フラグ

    public MyChara() {
        x = 3*32.0f;
        y = 13*32.0f;
        wx = 3*32.0f;
        wy = 13*32.0f;
        vx = 0.0f;
        vy = -8.0f;
        ay = 0.6f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        hp = 100;
        str= 10;
        def= 10;
        exp= 0;
        base_index=0;
        index = 0;
        second_jump=0;
        ichigo=0;
        status=0;
        animal=0;
        stairway=0;
        shotIndex=0;
        damage=0;
    }

    public void reset() {
        x = 3*32.0f;
        y = 13*32.0f;
        wx = 3*32.0f;
        wy = 13*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        hp = 100;
        str= 10;
        def= 10;
        exp= 0;
        index = 0;
        ichigo=0;
        status=0;
        animal=0;
        stairway=0;
        shotIndex=0;
        damage=0;
    }

    public void move(int touch_direction, float view_width, float view_height, Map map, MainActivity ac) {
        if ( status ==0 ) {
            if (touch_direction == 0) {
                vy = 0.0f;
                vx = 0.0f;
                base_index = 0;
            }
            if (touch_direction == 1) {
                vx = 3.0f;
                base_index = 0;
            }
            if (touch_direction == 2) {
                vx = -3.0f;
                base_index = 2;
            }
            if (touch_direction == 3) {
                //vy = 0.0f;
                vx = 0.0f;
                base_index = 0;
            }
            if (touch_direction == 4) {
                //vy = -3.0f;
                //vx = 0.0f;
                base_index = 0;
            }
            //加速度処理
            vy = vy + ay;
            if (vy > 6.0f) vy = 6.0f;
            //当たり判定用マップ座標算出
            int x1=(int)(wx+4.0f+vx)/32; if (x1<0) x1=0; if (x1>39) x1=39;
            int y1=(int)(wy+4.0f+vy)/32; if (y1<0) y1=0; if (y1>14) y1=14;
            int x2=(int)(wx+28.0f+vx)/32; if (x2>39) x2=39; if (x2<0) x2=0;
            int y2=(int)(wy+28.0f+vy)/32; if (y2>14) y2=14; if (y2<0) y2=0;
            //カベ判定
            if (map.MAP[y1][x1] > 0 || map.MAP[y1][x2] > 0 || map.MAP[y2][x1] > 0 || map.MAP[y2][x2] > 0) {
                vx = 0.0f;
                vy = -8.0f;
                //でも頭上に障害物あったら落下させる
                int x3=(int)(wx+4.0f+vx)/32; if (x3<0) x3=0; if (x3>39) x3=39;
                int y3=(int)(wy+0.0f+vy)/32; if (y3<0) y3=0; if (y3>14) y3=14;
                int x4=(int)(wx+28.0f+vx)/32; if (x4>39) x4=39; if (x4<0) x4=0;
                int y4=(int)(wy+0.0f+vy)/32; if (y4>14) y4=14; if (y4<0) y4=0;
                //カベ判定
                if (map.MAP[y3][x3] > 0 || map.MAP[y4][x4] > 0) {
                    vy = 1.0f;
                }
            }
            //ワールド座標更新
            wx=wx+vx;
            if (wx<0.0f) wx=0.0f;
            if (wx>39*32.0f) wx=39*32.0f;
            wy=wy+vy;
            //if (wy<-64.0f) wy=-64.0f;
            if (wy>14*32.0f) wy=14*32.0f;
            //ワールド当たり判定移動
            l = wx+ 4.0f+vx;
            r = wx+28.0f+vx;
            t = wy+ 4.0f+vy;
            b = wy+28.0f+vy;
            //画面座標更新
            x = x + vx;
            if (x>view_width-32.0f*4) {
                x=view_width-32.0f*4;
                ac.mapx=ac.mapx-vx;
                if (ac.mapx<-39*32+6*32.0f) ac.mapx=-39*32+6*32.0f;
            }
            if (x<64.0f) {
                x=64.0f;
                ac.mapx=ac.mapx-vx;
                if (ac.mapx>64.0f) ac.mapx=64.0f;
            }
            y = y + vy;
            //アニメーションインデックス変更処理
            index++;
            if (index > 19) index = 0;
        } else if (status ==1) {

        }
    }

    public void SetVx() {
        if (base_index==0) {
            base_index=2;
        } else {
            base_index=0;
        }
    }

    public void SetStatus(int s) { status=s; }

    public void SetStatus1() { status=1; }

    public void SetXY(float dx, float dy) {
        x = dx;
        y = dy;
    }

    public void setAnimal(int i){
        animal=i;
    }

    public void setHp(int n) { hp=hp+n; }
}
