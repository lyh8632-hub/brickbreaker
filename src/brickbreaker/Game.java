import java.util.ArrayList;
import java.util.Random;

public class Game {
    float BALLSIZE=20f;
    float ITEMSIZE=30f;
    int bump = 0; //시작직후 버그 방지
    float screenW = 3120;
    float screenH = 1440;
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
    int state = 0; // 0=게임중 1=게임오버 2=클리어
    
    public void gamerun() {
        float W = screenW;
        float H = screenH;  //화면의 가로 세로 크기 가져오기
        state=0;
        items.clear();
        life=3;
        controlh=H * 0.15f; //게임기본 설정
        paddle.width=W / 8;
        paddle.height=20;
        paddle.x=W / 2;
        paddle.y=H - controlh - 40; //패들높이설정
        ball.x=paddle.x;
        ball.y=paddle.y - 20;
        ball.onpaddle = true;
        ball.speed=9; // 공 속도 설정
        float bw=W / 10f;
        float bh=45;      // 벽돌 높이
        for (int r=0; r<5; r++) { //2차원배열로 5*10 벽돌생성
            for (int c=0; c<10; c++) {
                Brick b=new Brick();
                b.x=c * bw;
                b.y=80 + r * bh;
                b.w=bw;
                b.h=bh;
                if (r<2)      b.hp=3; // 내구도3
                else if (r<4) b.hp=2; // 내구도2
                else            b.hp=1; // 내구도1
                bricks[r][c] = b;
            }
        }
    }
    
    public void update() {
        if (state==1) return;
        if (movel)  paddle.x-= paddlespeed;
        if (mover) paddle.x+= paddlespeed;
        if (paddle.x < paddle.width/2) paddle.x=paddle.width/2; //패들 화면밖으로 나감 방지
        if (paddle.x > screenW-paddle.width/2) paddle.x=screenW-paddle.width/2;//패들 화면밖으로 나감 방지
        if (ball.onpaddle) {
            ball.x=paddle.x;
            ball.y=paddle.y - 20; //시작전 패들위에 공 놓기
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
        ball.x +=ball.dx * ball.speed;
        ball.y +=ball.dy * ball.speed;
        if (ball.x<0 || ball.x>screenW) ball.dx=-ball.dx;
        if (ball.y<0) ball.dy=-ball.dy;
        if (bump<=0) { //시작하자마자 충돌 방지
            if (ball.y>=paddle.y - paddle.height &&
                    ball.x>=paddle.x - paddle.width / 2 &&
                    ball.x<=paddle.x + paddle.width / 2) {
            	ball.dy=-ball.dy;
            }
        }
        if (bump>0) bump--;
        if (ball.y>screenH) { //공이 바닥에 떨어짐
            life--;
            ball.onpaddle=true; //다시 패들위에 공올림
            bump=15;
            if (life<=0) state=1; //목숨다쓰면게임오버
        }
        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                Brick b = bricks[r][c];
                if (b.hp>0 &&
                        ball.x>b.x && ball.x<b.x+b.w &&
                        ball.y>b.y && ball.y<b.y+b.h) {
                    ball.dy=-ball.dy;
                    b.hp--;  //벽돌에 공 충돌 로직
                    if (b.hp==0 && rand.nextInt(100)<10) { //벽돌깨지면 10%확률로 아이템
                        Item it=new Item();
                        it.x=b.x + b.w/2;
                        it.y=b.y; //아이템을 깨진벽돌 중앙위치에 생성해서 떨어지게 만듦
                        it.type=rand.nextInt(5);
                        items.add(it);
                    }
                }
            }
        }
        for (int i=0; i<items.size(); i++) {
            Item a=items.get(i);
            if (!a.active)
                continue;
            a.y+= 4;   //아이템 떨어짐
            if (a.y>paddle.y - paddle.height &&
                    a.x>paddle.x - paddle.width / 2 &&
                    a.x<paddle.x + paddle.width / 2) { //아이템 충돌 판정
                getitem(a.type); // 아이템 효과 처리
                a.active=false; // 먹은 아이템 처리
            }
        }
    }
    private void getitem(int type) { //아이템 효과들
        switch(type){
            case 0: paddle.width +=60; break;//패들크기증가
            case 1: if (paddle.width>80) paddle.width -=60; break; //패들크기감소
            case 2:
                if (ball.speed<12 )
                    ball.speed+= 3; //공속도 증가
                break;
            case 3:
                if (ball.speed>6)
                    ball.speed-= 3;//공속도감소
                break;
            case 4: life++; break; //목숨증가
        }
    }
}

