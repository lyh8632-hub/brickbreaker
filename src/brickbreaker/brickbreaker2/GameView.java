package com.example.brickbreaker2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Random;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    //GameView는 화면에 그림그릴 수 있는 SurfaceView SurfaceHolder.Callback 으로 처리 안드로이드 내장 클래스
    float BALLSIZE=20f;
    float ITEMSIZE=30f;
    int bump=0; //게임시작 직후 버그방지
    int rows=5, cols=10;
    int life=3;
    float paddlespeed=20f; // 패들 속도
    float controlh; //조작판높이
    boolean movel=false;
    boolean mover=false;
    Brick[][] bricks=new Brick[rows][cols];
    Paddle paddle=new Paddle();
    Ball ball=new Ball();
    ArrayList<Item> items=new ArrayList<>();
    Random rand=new Random();
    Paint paint=new Paint();
    SoundPool spsfx; //효과음 재생위한 내장클래스
    static final int normalmode=0;
    static final int hardmode=1;
    boolean gameOverBgmPlayed=false;
    int mode=normalmode;
    long lastdrop=0;
    static final long dropcycle=30_000; // 30초
    int shit, sbreak, spaddleup, spaddledown, sballfast, sballslow, slifeup; //효과음
    boolean sfxon=true;
    Draw runDraw=new Draw();
    OnTouchEvent runOnTouchEvent=new OnTouchEvent();
    int state=-1; // -1 로비창 0=게임중 1=게임오버 2=클리어
    void gamerun() {
        float W=getWidth();
        float H=getHeight();  //화면의 실제 가로 세로 크기 가져오기
        state=0;
        items.clear();
        life=3;
        controlh=H*0.15f; //게임기본 설정
        paddle.width=W / 8;
        paddle.height=20;
        paddle.x=W / 2;
        paddle.y=H-controlh-40; //패들높이설정
        ball.x=paddle.x;
        ball.y=paddle.y-20;
        ball.onpaddle=true; //공위에있으면 시작 전
        ball.speed=9; // 공 속도 설정
        lastdrop=System.currentTimeMillis(); //하드모드 벽돌 떨어뜨리는시간 현재시각으로 초기화
        gameOverBgmPlayed=false; //게임오버 배경음악 초기화
        float bw=W / 10f;
        float bh=45;      // 벽돌 높이
        for (int r=0; r<5; r++) { //2차원배열로 5*10 벽돌생성
            for (int c=0; c<10; c++) {
                Brick b=new Brick();
                b.x=c*bw;
                b.y=80+r*bh;
                b.w=bw;
                b.h=bh;
                if (r<2)      b.hp=3; // 내구도3
                else if (r<4) b.hp=2; // 내구도2
                else            b.hp=1; // 내구도1
                bricks[r][c]=b;
            }
        }
    }
    private void update() {
        if (state!=0) return;
        if (movel)  paddle.x-= paddlespeed;
        if (mover) paddle.x+= paddlespeed;
        if (paddle.x < paddle.width/2) paddle.x=paddle.width/2; //패들 화면밖으로 나감 방지
        if (paddle.x > getWidth()-paddle.width/2) paddle.x=getWidth()-paddle.width/2; //패들 화면밖으로 나감 방지
        if (ball.onpaddle) {
            ball.x=paddle.x;
            ball.y=paddle.y-20; //시작전 패들위에 공 놓기
            return;
        }
        boolean clear=true;
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                if (bricks[r][c].hp>0) clear=false; //벽돌 다깨면 클리어
            }
        }
        if (clear) {
            state=2;
        }
        ball.x +=ball.dx*ball.speed;
        ball.y +=ball.dy*ball.speed;
        if (ball.x<0 || ball.x>getWidth()) ball.dx=-ball.dx;
        if (ball.y<0) ball.dy=-ball.dy;
        if (bump<=0) { //시작하자마자 충돌 방지
            if (ball.y>=paddle.y-paddle.height &&
                    ball.x>=paddle.x-paddle.width / 2 &&
                    ball.x<=paddle.x+paddle.width / 2) {
                float hit=(ball.x-paddle.x) / (paddle.width / 2); //벽돌이 패들에 자연스럽게 튕기는 반사각 설정식
                ball.dx=hit*1.2f; //1에서 반사각을 더 높히기위해 변경
                ball.dy=-1; //공 패들 충돌 판정 로직
                play(shit);
            }
        }
        if (bump>0) bump--;
        if (ball.y>getHeight()) { //공이 바닥에 떨어짐
            life--;
            ball.onpaddle=true; //다시 패들위에 공올림
            bump=15;
            if (life<=0){
                state=1; //목숨다쓰면게임오버
                MainActivity.gameoverBgm();
                }
        }
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                Brick b=bricks[r][c];
                if (b.hp>0 &&
                        ball.x>b.x && ball.x<b.x+b.w &&
                        ball.y>b.y && ball.y<b.y+b.h) {
                    ball.dy=-ball.dy;
                    b.hp--;  //벽돌에 공 충돌 로직
                    if (b.hp==0) {
                        play(sbreak);
                        if (rand.nextInt(100) < 50) {
                            Item it=new Item();
                            it.x=b.x+b.w / 2;
                            it.y=b.y;
                            it.type=rand.nextInt(5);
                            items.add(it);
                        }
                    } else {
                        play(shit);
                    }

                }
            }
        }
        for (int i=0; i<items.size(); i++) {
            Item a=items.get(i);
            if (!a.active)
                continue;
            a.y+= 4;   //아이템 떨어짐
            if (a.y > getHeight()) { //바닥에 떨어지면 효과제거
                a.active=false;
                continue;
            }
            if (a.y>paddle.y-paddle.height &&
                    a.x>paddle.x-paddle.width / 2 &&
                    a.x<paddle.x+paddle.width / 2) { //아이템 충돌 판정
                getitem(a.type); // 아이템 효과 처리
                a.active=false; // 먹은 아이템 처리
            }
        }
        if (state==0 && mode==hardmode) {//하드모드
            long now=System.currentTimeMillis();
            if (now-lastdrop>=dropcycle) {//시간 지났는지 확인
                dropBricksOneStep(); //모든벽돌 하강
                lastdrop=now;//시간 갱신
            }
        }
    }
    private void getitem(int type) { //아이템 효과들
        switch(type){
            case 0: paddle.width +=60; //패들크기증가
            play(spaddleup);
            break;
            case 1: if (paddle.width>80) paddle.width -=60;  //패들크기감소
            play(spaddledown);
            break;
            case 2:
                if (ball.speed<12 )
                    ball.speed+= 3; //공속도 증가
                    play(sballfast);
                break;
            case 3:
                if (ball.speed>6)
                    ball.speed-= 3;//공속도감소
                    play(sballslow);
                break;
            case 4: life++;  //목숨증가
                play(slifeup);
                break;
        }
    }
    private void dropBricksOneStep() {//하드모드 전용
        float bh=45; //벽돌 높이랑 동일하게
        for (int r=rows-1; r>=0; r--) {
            for (int c=0; c < cols; c++) {
                Brick b=bricks[r][c];
                if (b.hp<=0) continue;
                b.y += bh; // 한 칸 아래로
                if (b.y+b.h>=paddle.y) {// 벽돌이 패들 라인 근처까지 내려오면 게임오버 처리
                    state=1;
                    if (!gameOverBgmPlayed) {
                        MainActivity.gameoverBgm();
                        gameOverBgmPlayed=true;
                    }
                    return;
                }
            }
        }
    }
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint.setAntiAlias(true); //계단현상 방지 코드
        AudioAttributes attrs=new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        spsfx=new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();
        shit=spsfx.load(context, R.raw.bounce, 1);//각 효과음 처리.res 에있는 mp3파일 사용
        sbreak=spsfx.load(context, R.raw.broken, 1);
        spaddleup=spsfx.load(context, R.raw.longp, 1);
        spaddledown=spsfx.load(context, R.raw.shortp, 1);
        sballfast=spsfx.load(context, R.raw.speedup, 1);
        sballslow=spsfx.load(context, R.raw.speeddown, 1);
        slifeup=spsfx.load(context, R.raw.getpower, 1);
    }
    private void play(int id) {
        if (!sfxon) return;
        if (spsfx!=null) spsfx.play(id, 1f, 1f, 1, 0, 1f);
    }
    Thread gameThread; // 게임루프실행 스레드
    boolean running=false; //스레드 반복여부
    @Override //안드로이드 내장 인터페이스 상속 이하동일
    public void surfaceCreated(SurfaceHolder holder) {
        running=true;
        gameThread=new Thread(() -> loop());
        gameThread.start();
    }
    private void loop() {//게임실행중인동안 계속반복되는 메인루프
        while (running) {
            update(); //게임 상태 계산
            draw(); //화면에 그림그림
            try { Thread.sleep(16); } catch (Exception e) {} //이거 없으면 너무빨리 움직임 60프레임 유지
        }
    }
    private void draw() {//캔버스로 화면에 출력
        Canvas canvas=getHolder().lockCanvas();
        runDraw.draw(canvas, this);
        getHolder().unlockCanvasAndPost(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) { //화면을 손가락으로 터치시 실행됨 안드로이드 내장 클래스
        return runOnTouchEvent.onTouchEvent(this, e);
    }
    public boolean onBackPressed() {//뒤로가기 동작 처리 여부 판단 내장클래스 아님!!
        if (state!=-1) {
            state=-1;                // 로비로 이동
            MainActivity.mainBgm(); // 메인 BGM 복귀
            return true;               // 뒤로가기 처리 완료
        }
        return false;                  // 이미 로비 → Activity가 종료 처리
    }
    @Override public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {}
    //SurfaceView의 화면이 변화될 때 자동으로 호출되는 안드로이드 내장 클래스 이 벽돌깨기는 가로화면만 지원하기에 안씀
    @Override public void surfaceDestroyed(SurfaceHolder holder) { //안드로이드 내장클래스 상속
        running=false;//게임 루프 멈춤
        if (spsfx!=null) {
            spsfx.release();
            spsfx=null;
        }
    }
}



