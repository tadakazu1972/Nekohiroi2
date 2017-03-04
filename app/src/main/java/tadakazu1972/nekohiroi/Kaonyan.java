package tadakazu1972.nekohiroi;

import java.util.Random;

/**
 * Created by tadakazu on 2015/09/22.
 */
public class Kaonyan {
    public float x, y;
    public float wx, wy;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int move_direction;
    public int visible;
    public int ranX, ranY;
    public int status; //0:ノーマル 1:フライング中

    public Kaonyan(Map map, MainActivity ac) {
        do {
            Random rndX = new Random();
            Random rndY = new Random();
            ranX = rndX.nextInt(35);
            ranY = rndY.nextInt(13);
        } while (map.MAP[ranY][ranX]>0);
        x = ranX*32.0f;
        y = ranY*32.0f;
        wx = x;
        wy = y;
        vx = 0.0f;
        vy = -6.0f;
        ay = 0.6f;
        l = wx;
        r = wx + 28.0f;
        t = wy;
        b = wy + 28.0f;
        base_index=0;
        index = 0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        visible = 1; //
        status = 0;
    }

    public void Reset(Map map, MainActivity ac) {
        do {
            Random rndX = new Random();
            Random rndY = new Random();
            ranX = rndX.nextInt(35);
            ranY = rndY.nextInt(2);
        } while (map.MAP[ranY][ranX]>0);
        x = ranX*32.0f;
        y = ranY*32.0f;
        wx = x;
        wy = y;
        vx = 0.0f;
        vy = 1.0f;
        ay = 0.6f;
        l = wx;
        r = wx + 28.0f;
        t = wy;
        b = wy + 28.0f;
        base_index=0;
        index = 0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        visible = 1; //ステージ数で増えるときには見えるようにセット
        status = 0;
    }

    public void move(MyChara m, MainActivity ac, float view_width, float view_height, Map map, Star s, SmallBaloon sb) {
        switch (status) {
            case 0:
                if (move_direction == 0) {
                    vx = 1.0f;
                    base_index = 0;
                }
                if (move_direction == 1) {
                    vx = -1.0f;
                    base_index = 2;
                }
                if (move_direction == 2) {
                    vx = 1.0f;
                    base_index = 0;
                }
                if (move_direction == 3) {
                    vx = -1.0f;
                    base_index = 2;
                }
                //加速度処理
                vy = vy + ay;
                if ( vy > 6.0f ) vy = 6.0f;
                //当たり判定用マップ座標算出
                int x1=(int)(wx+2.0f+vx)/32; if (x1<0) x1=0; if (x1>39) x1=39;
                int y1=(int)(wy+2.0f+vy)/32; if (y1<0) y1=0; if (y1>14) y1=14;
                int x2=(int)(wx+14.0f+vx)/32; if (x2>39) x2=39; if (x2<0) x2=0;
                int y2=(int)(wy+14.0f+vy)/32; if (y2>14) y2=14; if (y2<0) y2=0;
                //カベ判定
                if (map.MAP[y1][x1]>0||map.MAP[y1][x2]>0||map.MAP[y2][x1]>0||map.MAP[y2][x2]>0) {
                    vx = 0.0f;
                    vy = -6.0f;
                    //でも頭上に障害物あったら落下させる
                    int x3=(int)(wx+2.0f+vx)/32; if (x3<0) x3=0; if (x3>39) x3=39;
                    int y3=(int)(wy+0.0f+vy)/32; if (y3<0) y3=0; if (y3>14) y3=14;
                    int x4=(int)(wx+14.0f+vx)/32; if (x4>39) x4=39; if (x4<0) x4=0;
                    int y4=(int)(wy+0.0f+vy)/32; if (y4>14) y4=14; if (y4<0) y4=0;
                    //カベ判定
                    if (map.MAP[y3][x3] > 0 || map.MAP[y4][x4] > 0) {
                        vy = 1.0f;
                    }
                    Random rndDirection = new Random();
                    move_direction = rndDirection.nextInt(2);
                }
                //ワールド座標更新
                wx=wx+vx;
                if (wx<0.0f) {wx=0.0f; this.Reset(map, ac);}
                if (wx>39*32.0f) {wx=39*32.0f; this.Reset(map, ac);}
                wy=wy+vy;
                if (wy<0.0f) {wy=0.0f; this.Reset(map, ac); }
                if (wy>14*32.0f) {wy=14*32.0f; this.Reset(map, ac); }
                //ワールド当たり判定移動
                l = wx+ 2.0f+vx;
                r = wx+14.0f+vx;
                t = wy+ 2.0f+vy;
                b = wy+14.0f+vy;
                //プレイヤーとの当たり判定
                if ( l < m.r && m.l < r && t < m.b && m.t < b ) {
                    //サウンド
                    if (ac.gs!=0) ac.PlaySound(3);
                    //反転
                    if (move_direction != 0) {
                        move_direction = 0;
                    } else {
                        move_direction = 1;
                    }
                    Random rndDirection = new Random();
                    move_direction = rndDirection.nextInt(2);
                    //プレイヤーダメージ表現フラグON
                    m.damage = 1;
                    //プレイヤーの座標をいじわるする
                    m.vx = m.vx*-3.0f;
                    m.vy = -16.0f;
                }
                //ショットとの当たり判定
                for (int i=0;i<16;i++){
                    if ( l < ac.shot[i].r && ac.shot[i].l < r && t < ac.shot[i].b && ac.shot[i].t < b ) {
                        //サウンド
                        if (ac.gs!=0) ac.PlaySound(3);
                        //星表示セット
                        if (s.visible==0){
                            s.Set(x+ac.mapx, y+ac.mapy);
                            ac.star_counter++;
                            if (ac.star_counter>ac.SN-1) ac.star_counter = 0;
                        }
                        //フライングのフラグON
                        status = 1;
                        //小バルーンの位置セット
                        sb.Set(x+ac.mapx, y+ac.mapy);
                        //ショット消去と初期化
                        ac.shot[i].Reset(m);
                    }
                }
                break;

            //フライング中
            case 1:
                //左右にぶれる
                Random leftright = new Random();
                Boolean _leftright = leftright.nextBoolean();
                if (_leftright) {
                    vx = 1.0f;
                } else {
                    vx = -1.0f;
                }
                //上昇
                vy = -1.0f;

                //画面上で消えたらリセット
                if ( y < -16.0f) {
                    //サウンド
                    if (ac.gs!=0) ac.PlaySound(3);
                    //星表示セット
                    if (s.visible==0){
                        s.Set(x+ac.mapx, y+ac.mapy);
                        ac.star_counter++;
                        if (ac.star_counter>ac.SN-1) ac.star_counter = 0;
                    }
                    //位置初期化
                    this.Reset(map, ac);
                    //小バルーン初期化
                    sb.Reset();
                }
                break;
            default:
                break;

        }

        //座標更新
        x = x + vx;
        y = y + vy;
        //アニメーションインデックス変更処理
        index++;
        if ( index > 19 ) index =0;
    }
}
