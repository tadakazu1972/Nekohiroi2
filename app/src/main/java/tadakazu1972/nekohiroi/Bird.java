package tadakazu1972.nekohiroi;

import java.util.Random;

/**
 * Created by tadakazu on 2015/09/22.
 */
public class Bird {
    public float x, y;
    public float vx, vy;
    public float ay;
    public float l, r, t, b;
    public double radian;
    public int base_index; //アニメーション基底 0:右向き 2:左向き
    public int index;
    public int move_direction;
    public int ichigo; //いちごもっているかフラグ 0:もっていない 1:もっている
    public int visible;
    public int animal;

    public Bird(){
        Random rndX = new Random();
        Random rndY = new Random();
        int ranX = rndX.nextInt(10);
        int ranY = rndY.nextInt(15);
        x = ranX*32.0f;
        y = ranY*32.0f;
        vy = 0.0f;
        ay = 0.8f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        radian = 0.0d;
        base_index=0;
        index = 0;
        ichigo = 0;
        animal=0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        if ( move_direction==0) { vx= 2.0f; }
        if ( move_direction==1) { vx=-2.0f; }
        visible = 0; //コンストラクタ時点では見えないようにセット
    }

    public void Reset() {
        Random rndX = new Random();
        Random rndY = new Random();
        int ranX = rndX.nextInt(10);
        int ranY = rndY.nextInt(15);
        x = ranX*32.0f;
        y = ranY*32.0f;
        vy = 0.0f;
        ay = 0.8f;
        l = x;
        r = x + 31.0f;
        t = y;
        b = y + 31.0f;
        base_index=0;
        index = 0;
        ichigo = 0;
        animal=0;
        Random rndDirection = new Random();
        move_direction = rndDirection.nextInt(2);
        if ( move_direction==0) { vx= 2.0f; }
        if ( move_direction==1) { vx=-2.0f; }
        visible = 1; //ステージ数で増えるときには見えるようにセット
    }

    public void Move(MyChara m, MainActivity ac, float view_width, float view_height, Map map) {

        if (move_direction == 0) {
            vx = 2.0f+ac.loop;
            base_index=0;
        }
        if (move_direction == 1) {
            vx = -1*(2.0f+ac.loop);
            base_index=2;
        }
        radian = radian + 0.1d;
        if ( radian > 65000.0d ) radian = 0.0d;
        vy = 4.0f*(float)Math.sin(radian);
        //当たり判定移動
        l = x + vx;
        r = x + 31.0f + vx;
        t = y + vy;
        b = y + 31.0f + vy;
        //くまちゃんとの当たり判定
        if ( l < m.r && m.l < r && t < m.b && m.t < b ) {
            ac.PlaySound(3);
            //vx=-3*m.vx;
            //vy=-1*m.vy;
            m.SetVx();
            ac.SetTouch();
            //もし鳥がディスプレイを持っていなくて、くまちゃんがもっていたら鳥がディスプレイをとる
            if ( animal == 0 && m.animal!=0 ) {
                animal = m.animal;
                m.setAnimal(0);
            }
        }
        //かおにゃんとの当たり判定
        //座標更新
        x = x + vx;
        y = y + vy;
        //アニメーションインデックス変更処理
        index++;
        if ( index > 19 ) index =0;
        //画面端判定
        if (x < -31 ) {
            x = 0;
            Random rndY = new Random();
            int ranY = rndY.nextInt(15);
            y = ranY*32.0f;
            move_direction=0;
            //もしディスプレイもっていたらどこかに落とす
            if (animal!=0){
                ac.c[animal].ResetCake(map);
                animal=0;
            }
        }
        if (x > view_width) {
            x = view_width -1;
            Random rndY = new Random();
            int ranY = rndY.nextInt(15);
            y = ranY*32.0f;
            move_direction=1;
            //もしディスプレイもっていたらどこかに落とす
            if (animal!=0){
                ac.c[animal].ResetCake(map);
                animal=0;
            }
        }
        if (y < -31) {
            y = view_height;
        }
        if (y > view_height) {
            y = view_height;
        }
    }
}
