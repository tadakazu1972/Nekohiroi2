package tadakazu1972.nekohiroi;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Toast;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Integer.parseInt;


public class MainActivity extends Activity implements GLSurfaceView.Renderer {

    final float VIEW_WIDTH = 320;
    final float VIEW_HEIGHT = 480;
    private float lastPointX, lastPointY;
    public int touch_direction;

    private tadakazu1972.nekohiroi.TouchWatcher m_TouchWatcher;
    private tadakazu1972.nekohiroi.TouchManager m_TouchMngr;
    private tadakazu1972.nekohiroi.TouchButton m_Button;

    private GLSurfaceView m_View;
    private tadakazu1972.nekohiroi.DrawDevice m_DrawDevice;
    private tadakazu1972.nekohiroi.AppResource m_AppRes;
    private tadakazu1972.nekohiroi.TextureResource m_TexRes;

    private tadakazu1972.nekohiroi.Model[] m_sMap = new tadakazu1972.nekohiroi.Model[6];
    private tadakazu1972.nekohiroi.Model[] m_sBg = new tadakazu1972.nekohiroi.Model[2];
    public Map m_MAP;
    public float mapx;
    public float mapy;

    protected tadakazu1972.nekohiroi.MyChara m;
    private tadakazu1972.nekohiroi.Model[] m_sKumako = new tadakazu1972.nekohiroi.Model[12];
    protected tadakazu1972.nekohiroi.Shot[] shot = new tadakazu1972.nekohiroi.Shot[16];
    private tadakazu1972.nekohiroi.Model[] m_sShot = new tadakazu1972.nekohiroi.Model[4];
    public static final int KN = 20;
    protected tadakazu1972.nekohiroi.Kaonyan[] k = new tadakazu1972.nekohiroi.Kaonyan[KN];
    private tadakazu1972.nekohiroi.Model[] m_sKaonyan = new tadakazu1972.nekohiroi.Model[4];
    public int kaonyan_number; //かおにゃん登場数
    protected SmallBaloon[] sb = new SmallBaloon[KN]; //小バルーンはねこと同数作成
    private Model[] m_sSmallBaloon = new Model[4];
    public static final int SN = 5;
    protected tadakazu1972.nekohiroi.Star[] s = new tadakazu1972.nekohiroi.Star[SN];
    private tadakazu1972.nekohiroi.Model[] sStar = new tadakazu1972.nekohiroi.Model[7]; //7パターンの絵
    public int star_number;
    public int star_counter;
    public static final int BN = 10;
    protected tadakazu1972.nekohiroi.Bird[] bird = new tadakazu1972.nekohiroi.Bird[BN];
    private tadakazu1972.nekohiroi.Model[] m_sBird = new tadakazu1972.nekohiroi.Model[4];
    public int bird_number;
    public static final int CN = 35;
    protected tadakazu1972.nekohiroi.Cake[] c = new tadakazu1972.nekohiroi.Cake[CN];
    protected tadakazu1972.nekohiroi.Baloon b;
    private tadakazu1972.nekohiroi.Model m_sBaloon;
    private tadakazu1972.nekohiroi.Model[] m_sRibbon = new tadakazu1972.nekohiroi.Model[2];
    protected tadakazu1972.nekohiroi.Cloud[] cloud = new tadakazu1972.nekohiroi.Cloud[3];
    private tadakazu1972.nekohiroi.Model[] m_sCloud = new tadakazu1972.nekohiroi.Model[3];
    public static final int IN = 100;
    private tadakazu1972.nekohiroi.Model m_sTitle;
    private tadakazu1972.nekohiroi.Model m_sEndroll;
    private float endY;
    public int counter; //ケーキゲット数
    public int stage; //ステージ数
    public int gs; //ゲームステータス
    public int loop; //周回数記憶用
    private SoundPool mSoundPool;
    private int mSoundId1;
    private int mSoundId2;
    private int mSoundId3;
    private int mSoundId4;
    private int mSoundId5;
    private int mSoundId6;

    public float[] nx =new float[100], ny=new float[100];
    public float[] nwx=new float[100], nwy=new float[100];
    public float[] nvx=new float[100], nvy=new float[100];
    public float[] nl=new float[100], nr=new float[100], nt=new float[100], nb=new float[100];
    public int[] nbase_index=new int[100]; //アニメーション基底 0:右向き 2:左向き
    public int[] nindex=new int[100];
    public int[] nmove_direction=new int[100];
    public int[] nvisible=new int[100];
    public int[] nranX=new int[100], nranY=new int[100];
    //タイマー
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private Handler mHandler = new Handler();

    //タイマータスク派生クラス run()に定周期で処理したい内容を記述
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post (new Runnable() {
                public void run() {
                    if (gs == 1) { //ゲーム中なら以下を実行
                        //ショットが発射されていなかったら発射
                        if (shot[m.shotIndex].visible!=1){
                            shot[m.shotIndex].visible=1;
                            shot[m.shotIndex].base_index = m.base_index;
                            if (m.base_index!=2){
                                shot[m.shotIndex].vx = 8.0f;
                            } else {
                                shot[m.shotIndex].vx = -8.0f;
                            }
                            m.shotIndex++;
                            if (m.shotIndex>15) m.shotIndex=0;
                        }
                        //プレイヤーダメージ表現フラグOFF
                        m.damage=0;
                    }
                }
            });
        }
    }

    public MainActivity() {
        super();
        m_View = null;
        m_DrawDevice = null;

        m_TouchWatcher  = null;
        m_TouchMngr     = null;
        m_Button        = null;
        m_AppRes        = null;
        m_TexRes        = null;

        m = null;
        m_MAP = new Map();
        mapx=0.0f;
        mapy=0.0f;
        counter = 0;
        stage = 0;
        kaonyan_number= 20;
        star_number=5;
        star_counter=0;
        bird_number=0;
        gs=0;
        endY=480.0f;
        loop=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タッチ関係初期設定
        lastPointX=0.0f; lastPointY=0.0f;
        touch_direction=0;
        //アーサー生成
        m = new tadakazu1972.nekohiroi.MyChara();
        //ショット生成
        for (int i=0;i<16;i++) shot[i] = new tadakazu1972.nekohiroi.Shot(m);
        //ねこ生成
        for (int i=0;i<KN;i++) k[i]=new tadakazu1972.nekohiroi.Kaonyan(m_MAP,this);
        //小バルーン生成
        for (int i=0;i<KN;i++) sb[i]=new SmallBaloon();
        //星生成
        for (int t=0;t<SN;t++) s[t]=new tadakazu1972.nekohiroi.Star();
        //鳥生成
        for (int k=0;k<BN;k++) bird[k]=new tadakazu1972.nekohiroi.Bird();
        //バルーン生成
        b = new tadakazu1972.nekohiroi.Baloon();
        //雲生成
        for ( int q=0;q<3;q++) cloud[q]=new tadakazu1972.nekohiroi.Cloud(q);
        m_View = new GLSurfaceView( this );
        m_View.setRenderer( this );
        m_DrawDevice = new tadakazu1972.nekohiroi.DrawDevice();
        m_TouchWatcher  = new tadakazu1972.nekohiroi.TouchWatcher();
        m_TouchMngr     = new tadakazu1972.nekohiroi.TouchManager( m_TouchWatcher );
        m_Button        = new tadakazu1972.nekohiroi.TouchButton( m_TouchMngr );
        {
            m_Button.SetRect( 0, 0, ( int ) tadakazu1972.nekohiroi.DrawDevice.DRAW_WIDTH, ( int ) tadakazu1972.nekohiroi.DrawDevice.DRAW_HEIGHT );
        }
        m_AppRes        = new tadakazu1972.nekohiroi.AppResource( this );
        m_TexRes        = new tadakazu1972.nekohiroi.TextureResource( m_AppRes, m_DrawDevice );
        setContentView( m_View );
        //マップ生成
        loadCSV("stage01.csv");
        //タイマー
        mainTimer = new Timer();
        mainTimerTask = new MainTimerTask();
        mainTimer.schedule(mainTimerTask, 150, 150);
    }

    public void onResume() {
        super.onResume();
        //隙間時間に音声データ読み込み
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundId1 = mSoundPool.load(getApplicationContext(), R.raw.get2, 0);
        mSoundId2 = mSoundPool.load(getApplicationContext(), R.raw.get, 0);
        mSoundId3 = mSoundPool.load(getApplicationContext(), R.raw.pote01, 0);
        mSoundId4 = mSoundPool.load(getApplicationContext(), R.raw.clear, 0);
        mSoundId5 = mSoundPool.load(getApplicationContext(), R.raw.key, 0);
        mSoundId6 = mSoundPool.load(getApplicationContext(), R.raw.baloon, 0);
        //others
        m_View.onResume();
    }

    public void onPause() {
        m_View.onPause();
        super.onPause();
        mSoundPool.release();
    }

    @Override
    public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
        m_DrawDevice.Create(gl);
    }

    @Override
    public void onSurfaceChanged( GL10 gl, int width, int height) {
        Rect recr = new Rect();
        m_View.getWindowVisibleDisplayFrame( recr );

        m_DrawDevice.UpdateDrawArea(gl, width, height, recr.top);

    }

    @Override
    public boolean  onTouchEvent( MotionEvent event )
    {
        final float orgTouchPosX    = event.getX();
        final float orgTouchPosY    = event.getY();
        PointF touchPos        = m_DrawDevice.GetSprite().ScreenPosToDrawPos( new PointF( orgTouchPosX, orgTouchPosY ));

        float touchedX = event.getX();
        float touchedY = event.getY();
        //前回タッチとの差分を算出
        float temp_vx, temp_vy;
        temp_vx = Math.abs(lastPointX-touchedX);
        temp_vy = Math.abs(lastPointY-touchedY);

        tadakazu1972.nekohiroi.TouchWatcher.ACTION action;

        switch( event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //タイトルのとき
                if (gs==0) {
                    this.PlaySound(2);
                    gs=1;
                }
                //武器屋
                if (gs==3) {
                    if (touchPos.x>180 && touchPos.y>400) {
                        touch_direction=3;
                        gs=1;
                    }
                }
                action  = tadakazu1972.nekohiroi.TouchWatcher.ACTION.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE: //タッチされた
                if (temp_vx > temp_vy) {
                    if (lastPointX - touchedX < 0) {
                        touch_direction = 1; //right
                    } else {
                        touch_direction = 2; //left
                    }
                } else {
                    if (lastPointY - touchedY < 0) {
                        touch_direction = 3; //down
                    } else {
                        touch_direction = 4; //up
                    }
                }
                //タッチ座標更新
                lastPointX = touchedX;
                lastPointY = touchedY;
                action  = tadakazu1972.nekohiroi.TouchWatcher.ACTION.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                action  = tadakazu1972.nekohiroi.TouchWatcher.ACTION.ACTION_UP;    break;
            default:
                return true;
        }

        if( !m_TouchWatcher.AddAction( action, touchPos ))
        {
            System.err.println( "TouchEventOverFlow!!" );
        }

        return true;
    }

    @Override
    public void onDrawFrame( GL10 gl ) {
        //シーンの更新
        m_DrawDevice.Begin( gl );
        {
            m_TouchMngr.Update();

            //画像読み込み
            if (m_sKumako[0] == null) {
                loadImage();
            }
            //描画
            //*****************************************************************************
            //タイトル
            if (gs==0) {
                //背景
                m_sBg[0].Draw(0, 0, m_DrawDevice);
                //雲
                for (int q=0;q<3;q++) {
                    m_sCloud[q].Draw(cloud[q].x,cloud[q].y, m_DrawDevice);
                    cloud[q].Move1(this);
                }
                //タイトル
                m_sTitle.Draw(0, 0, m_DrawDevice);
                //アーサー
                int index = m.base_index + m.index / 10;
                m_sKumako[index].Draw(m.x, m.y, m_DrawDevice);
                //移動
                //アーサー
                m.move(touch_direction, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, this);

                //********************************************************************************
                //通常ゲーム
            } else if (gs==1) {
                //背景
                m_sBg[0].Draw(0, 0, m_DrawDevice);
                //MAP
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 40; j++) {
                        int mapid = m_MAP.MAP[i][j];
                        if (mapid!=0) {
                            m_sMap[mapid].Draw(j * 32 + mapx, i * 32 + mapy, m_DrawDevice);
                        }
                    }
                }
                //バルーン
                m_sBaloon.Draw(b.x, b.y, m_DrawDevice);
                int b_index = b.index / 10;
                m_sRibbon[b_index].Draw(b.x, b.y + 200.0f, m_DrawDevice);
                //ねこ＋小バルーン
                for (int i1 = 0; i1 < kaonyan_number; i1++) {
                    tadakazu1972.nekohiroi.Kaonyan i = k[i1];
                    if (i.visible==1) {
                        int k_index = i.base_index + i.index / 10;
                        m_sKaonyan[k_index].Draw(i.x+mapx, i.y+mapy, m_DrawDevice);
                    }
                    SmallBaloon _sb = sb[i1];
                    if (_sb.visible) {
                        int sb_index = _sb.index / 10;
                        m_sSmallBaloon[sb_index].Draw(_sb.x, _sb.y, m_DrawDevice);
                    }
                }
                //鳥
                for (int k1 = 0; k1 < bird_number; k1++) {
                    tadakazu1972.nekohiroi.Bird k = bird[k1];
                    int bird_index = k.base_index + k.index / 10;
                    m_sBird[bird_index].Draw(k.x, k.y, m_DrawDevice);
                }
                //ショット
                for (int i=0;i<16;i++) {
                    int shotIndex = shot[i].base_index;
                    m_sShot[shotIndex].Draw(shot[i].x, shot[i].y, m_DrawDevice);
                }
                //くまちゃん
                int index;
                if (m.damage==0) {
                    index = m.base_index + m.index / 10;
                } else {
                    index = 8 + m.base_index + m.index / 10; //ダメージ表現スプライトのIndexで8を足しておく
                }
                m_sKumako[index].Draw(m.x, m.y, m_DrawDevice);
                //星
                for (int t1 = 0; t1 < star_number; t1++) {
                    tadakazu1972.nekohiroi.Star i = s[t1];
                    if (i.visible==1) {
                        int k_index = i.index / 10;
                        sStar[k_index].Draw(i.x, i.y, m_DrawDevice);
                    }
                }
                //雲
                for (int q=0;q<3;q++) {
                    m_sCloud[q].Draw(cloud[q].x,cloud[q].y, m_DrawDevice);
                    cloud[q].Move1(this);
                }
                //**********************************************************************
                //移動
                //くまちゃん
                m.move(touch_direction, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, this);
                //ショット
                for (int i=0;i<16;i++) {
                    shot[i].move(m, this, m_MAP);
                }
                //ねこ+小バルーン
                for (int i2 = 0; i2 < kaonyan_number; i2++) {
                    tadakazu1972.nekohiroi.Kaonyan i = k[i2];
                    if (i.visible==1) {
                        i.move(m, this, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, s[star_counter], sb[i2]);
                    }
                    SmallBaloon _sb = sb[i2];
                    if (_sb.visible){
                        _sb.Move(i.x+mapx, i.y+mapy);
                    }
                }
                //星
                for (int t2 = 0; t2 < star_number; t2++) {
                    tadakazu1972.nekohiroi.Star i = s[t2];
                    if (i.visible==1) {
                        i.Move();
                    }
                }
                //鳥
                for (int k2 = 0; k2 < bird_number; k2++) {
                    bird[k2].Move(m, this, VIEW_WIDTH, VIEW_HEIGHT, m_MAP);
                }
                //バルーン
                b.Move(m, this);

                //エンディング
            } else if (gs==2) {
                //背景
                m_sBg[1].Draw(0, 0, m_DrawDevice);
                //雲
                for (int q=0;q<3;q++) {
                    m_sCloud[q].Draw(cloud[q].x,cloud[q].y, m_DrawDevice);
                    cloud[q].Move2(this);
                }
                //バルーン
                m_sBaloon.Draw(b.x, b.y, m_DrawDevice);
                int b_index = b.index / 10;
                m_sRibbon[b_index].Draw(b.x, b.y + 200.0f, m_DrawDevice);
                //リボンのアニメーション処理
                b.index++;
                if ( b.index > 19 ) b.index =0;
                //くまちゃん
                int index = m.base_index + m.index / 10;
                m_sKumako[index].Draw(b.x+84, b.y+200, m_DrawDevice);
                //エンドロール
                m_sEndroll.Draw(0, endY, m_DrawDevice);
                endY=endY-0.6f;
                b.y=b.y-0.3f; //バルーンの上昇速度はエンドロールより遅め
                if (endY<-960.0f) {
                    endY=480.0f;
                    //バルーン画面上の待機位置へ
                    b.SetY(-260.0f);
                    gs=0;
                }
                //武器屋
            } else if (gs==3) {

            }
        }
        m_DrawDevice.End();

        //  タッチイベント監視人更新
        m_TouchWatcher.Update();
    }

    public void loadImage(){
        m_sKumako[0] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[1] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[2] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[3] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[4] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[5] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[6] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[7] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[8] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[9] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[10] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[11] = new tadakazu1972.nekohiroi.Model();
        m_sKumako[0].Create("kumako01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[1].Create("kumako02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[2].Create("kumako03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[3].Create("kumako04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[4].Create("kumako01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[5].Create("kumako02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[6].Create("kumako03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[7].Create("kumako04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[8].Create("arthur03damage", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[9].Create("arthur04damage", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[10].Create("arthur05damage", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[11].Create("arthur06damage", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sShot[0] = new tadakazu1972.nekohiroi.Model();
        m_sShot[1] = new tadakazu1972.nekohiroi.Model();
        m_sShot[2] = new tadakazu1972.nekohiroi.Model();
        m_sShot[3] = new tadakazu1972.nekohiroi.Model();
        m_sShot[0].Create("baloon01", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[1].Create("baloon02", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[2].Create("baloon03", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[3].Create("baloon04", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sKaonyan[0] = new tadakazu1972.nekohiroi.Model();
        m_sKaonyan[1] = new tadakazu1972.nekohiroi.Model();
        m_sKaonyan[2] = new tadakazu1972.nekohiroi.Model();
        m_sKaonyan[3] = new tadakazu1972.nekohiroi.Model();
        for (int i=0;i<6;i++){
            m_sMap[i] = new tadakazu1972.nekohiroi.Model();
        }
        m_sMap[0].Create("black", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[1].Create("brick", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[2].Create("redbrick01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[3].Create("rock01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[4].Create("pillar01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[5].Create("grass", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);

        m_sKaonyan[0].Create("neko03", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sKaonyan[1].Create("neko04", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sKaonyan[2].Create("neko01", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sKaonyan[3].Create("neko02", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);

        for (int i=0;i<4;i++) {
            m_sSmallBaloon[i] = new Model();
        }
        m_sSmallBaloon[0].Create("baloon01", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[1].Create("baloon02", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[2].Create("baloon03", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[3].Create("baloon04", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);

        for (int i=0;i<7;i++){
            sStar[i] = new tadakazu1972.nekohiroi.Model();
        }
        sStar[0].Create("star01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[1].Create("star02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[2].Create("star03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[3].Create("star04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[4].Create("star05", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[5].Create("star06", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[6].Create("star07", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[0] = new tadakazu1972.nekohiroi.Model();
        m_sBird[1] = new tadakazu1972.nekohiroi.Model();
        m_sBird[2] = new tadakazu1972.nekohiroi.Model();
        m_sBird[3] = new tadakazu1972.nekohiroi.Model();
        m_sBird[0].Create("bird03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[1].Create("bird04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[2].Create("bird01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[3].Create("bird02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBg[0] = new tadakazu1972.nekohiroi.Model();
        m_sBg[1] = new tadakazu1972.nekohiroi.Model();
        m_sBg[0].Create("bg_stage01", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sBg[1].Create("bg_ending", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sBaloon = new tadakazu1972.nekohiroi.Model();
        m_sBaloon.Create("bigbaloon", 0.0f, 0.0f, 0, 0, 200, 200, m_TexRes);
        m_sRibbon[0] = new tadakazu1972.nekohiroi.Model();
        m_sRibbon[1] = new tadakazu1972.nekohiroi.Model();
        m_sRibbon[0].Create("baloonribbon01", 0.0f, 0.0f, 0, 0, 200, 96, m_TexRes);
        m_sRibbon[1].Create("baloonribbon02", 0.0f, 0.0f, 0, 0, 200, 96, m_TexRes);
        m_sCloud[0] = new tadakazu1972.nekohiroi.Model();
        m_sCloud[1] = new tadakazu1972.nekohiroi.Model();
        m_sCloud[2] = new tadakazu1972.nekohiroi.Model();
        m_sCloud[0].Create("cloud01", 0.0f, 0.0f, 0, 0, 120, 60, m_TexRes);
        m_sCloud[1].Create("cloud02", 0.0f, 0.0f, 0, 0, 120, 60, m_TexRes);
        m_sCloud[2].Create("cloud03", 0.0f, 0.0f, 0, 0, 96, 96, m_TexRes);
        m_sTitle = new tadakazu1972.nekohiroi.Model();
        m_sTitle.Create("title", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sEndroll = new tadakazu1972.nekohiroi.Model();
        m_sEndroll.Create("endroll", 0.0f, 0.0f, 0, 0, 320, 960, m_TexRes);
    }

    public void SetTouch() {
        if (touch_direction==1) {
            touch_direction=2;
        } else {
            touch_direction=1;
        }
    }

    public void GotoNextStage() {
        //エンディングへ
        stage=9;
        //ループ数増加
        loop++;
        if (loop>30) loop=30;
        //ループすると鳥増える
        bird_number++;
        if (bird_number > BN ) bird_number=BN;
        //バルーンのY座標を画面下に設定
        b.SetY(480.0f);
        //雲のvyを下に行くように変更
        for(int q=0;q<3;q++) {
            Random r = new Random();
            cloud[q].SetVY((float)r.nextInt(3)+1.0f);
        }
        //エンドロールへ
        gs=2;

        //マップ再セット
        //ケーキ再セット
        counter=0;
        for (tadakazu1972.nekohiroi.Cake i:c) {
            i.ResetCake(m_MAP);
        }
        //鳥再セット
        for ( int k=0;k<bird_number;k++) {
            bird[k].Reset();
        }
        //くまちゃん再セット
        m.reset();
        stage=9;
        //マップ再セット
    }

    public void PlaySound(int id){
        mSoundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void setStage(int i) {
        stage=stage+i;
        //マップ再セット
        //m_MAP.SetStage(stage);
    }

    public void setGs(int i){
        gs=i;
    }

    public void setStar_counter(int i) { star_counter=i; }

    public void setTouch_direction(int i) { touch_direction=i; }

    public void gotoNextLevel(){
        //キー再セット
        for (tadakazu1972.nekohiroi.Cake i:c) {
            i.ResetCake(m_MAP);
        }
        //モンスター再セット
        for (int i=0;i<kaonyan_number;i++) {
            k[i].Reset(m_MAP,this);
        }

    }

    public void loadCSV(String filename){
        InputStream is = null;
        try {
            try {
                //assetsフォルダ内のcsvファイル読込
                is = getAssets().open(filename);
                InputStreamReader ir = new InputStreamReader(is, "UTF-8");
                CSVReader csvreader = new CSVReader(ir, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);//0行目から
                String[] csv;
                int y = 0;
                while((csv = csvreader.readNext()) != null){
                    for (int x=0;x<40;x++){
                        //読み込んだデータをある程度のレンジに変換して格納
                        int data = 0;
                        int _data = parseInt(csv[x]);
                        m_MAP.MAP[y][x] = _data;
                    }
                    y++; if (y>15){ y=0;}
                }
                //データ代入
            } finally {
                if (is != null) is.close();
                Toast.makeText(this, "マップデータ読込完了", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            Toast.makeText(this, "CSV読込エラー", Toast.LENGTH_SHORT).show();
        }
    }
}
