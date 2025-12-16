package com.example.brickbreaker2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//캔버스로 UI 그림
public class Draw {

    private Paint paint=new Paint();

    public void draw(Canvas canvas, GameView v) {

        if (canvas==null) return; //캔버스 준비
        canvas.drawColor(Color.BLACK);

        if (v.state==-1) { //로비화면 출력
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, v.getWidth(), v.getHeight(), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            canvas.drawText("BRICK BREAKER", v.getWidth()/2-260, 200, paint);
            paint.setTextSize(60);
            canvas.drawRect(300, 400, v.getWidth()-300, 500, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("GAME START", 360, 470, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(300, 550, v.getWidth()-300, 650, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("SETTING", 420, 620, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(300, 700, v.getWidth()-300, 800, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("CREDIT", 430, 770, paint);
            return;
        }
        if (v.state==-2) { //게임스타트 눌렀을때 노말,하드 난이도선택
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, v.getWidth(), v.getHeight(), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText("SELECT MODE", v.getWidth()/2f-200, 200, paint);
            paint.setTextSize(55);
            paint.setColor(Color.WHITE);// NORMAL 버튼
            canvas.drawRect(300, 350, v.getWidth()-300, 450, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("NORMAL", v.getWidth()/2f-110, 420, paint);
            paint.setColor(Color.WHITE);// HARD 버튼
            canvas.drawRect(300, 520, v.getWidth()-300, 620, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("HARD ", v.getWidth()/2f-90, 590, paint);
            paint.setColor(Color.WHITE);// 터치하면 돌아갑니다 출력
            paint.setTextSize(40);
            canvas.drawText("Tap to Back", v.getWidth()/2f-110, 750, paint);
            return;
        }
        if (v.state==1) { //게임오버화면
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            canvas.drawText("GAME OVER", v.getWidth()/2-200, v.getHeight()/2, paint);
            paint.setTextSize(40);
            canvas.drawText("Tap to Restart", v.getWidth()/2-120, v.getHeight()/2+60, paint);
            return; //게임오버시 게임오버 화면 출력
        }
        if (v.state==2) { //스테이지클리어화면
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            canvas.drawText("STAGE CLEAR!", v.getWidth()/2-230, v.getHeight()/2, paint);
            paint.setTextSize(40);
            canvas.drawText("Tap to Restart", v.getWidth()/2-120, v.getHeight()/2+60, paint);
            return; //클리어시 클리어 화면 출력
        }
        if (v.state==10) { //설정화면
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, v.getWidth(), v.getHeight(), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText("SETTING", v.getWidth()/2f-140, 200, paint);
            paint.setTextSize(50);
            canvas.drawRect(300, 350, v.getWidth()-300, 450, paint);// BGM 버튼
            paint.setColor(Color.BLACK);
            canvas.drawText("BGM : "+(MainActivity.bgmToggle() ? "ON" : "OFF"), 380, 420, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(300, 520, v.getWidth()-300, 620, paint);  // SFX 버튼
            paint.setColor(Color.BLACK);
            canvas.drawText("SFX : "+(v.sfxon ? "ON" : "OFF"), 380, 590, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Tap to Back", v.getWidth()/2f-110, 750, paint);
            return;
        }
        if (v.state==11) { //크레딧화면
            paint.setColor(Color.BLACK);
            canvas.drawRect(0,0,v.getWidth(),v.getHeight(),paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText("CREDIT", v.getWidth()/2-100, 200, paint);
            paint.setTextSize(50);
            canvas.drawText("Developer : 이윤혁,홍주석", 300, 350, paint);
            paint.setTextSize(40);
            canvas.drawText("Tap to Back", 350, 600, paint);
            return;
        }
//(state==0) 일때 실제게임화면
        for (int r=0; r<v.rows; r++) { //벽돌,경계선 그리기
            for (int c=0; c<v.cols; c++) {
                Brick b=v.bricks[r][c];
                if (b.hp>0) {
                    if (b.hp==3) paint.setColor(Color.YELLOW);
                    else if (b.hp==2) paint.setColor(Color.rgb(255,140,0));
                    else paint.setColor(Color.RED);
                    canvas.drawRect(b.x, b.y, b.x+b.w, b.y+b.h, paint);//벽돌그리는과정
                    paint.setStyle(Paint.Style.STROKE);//테두리그리기모드
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(b.x, b.y, b.x+b.w, b.y+b.h, paint);
                    paint.setStyle(Paint.Style.FILL); //벽돌 테두리 그리는 과정
                }
            }
        }
        paint.setColor(Color.WHITE);
        canvas.drawRect(v.paddle.x-v.paddle.width/2, v.paddle.y,
                v.paddle.x+v.paddle.width/2, v.paddle.y+v.paddle.height, paint);
        canvas.drawCircle(v.ball.x, v.ball.y, v.BALLSIZE, paint); //패들과 공 그리기
        for (int i=0; i<v.items.size(); i++) {
            Item a=v.items.get(i);
            if (!a.active) //아이템 먹혔으면 건너뜀
                continue;
            switch(a.type){
                case 0: paint.setColor(Color.RED); break;
                case 1: paint.setColor(Color.BLUE); break;
                case 2: paint.setColor(Color.YELLOW); break;
                case 3: paint.setColor(Color.WHITE); break;
                case 4: paint.setColor(Color.GREEN); break;
            }
            canvas.drawRect(a.x-v.ITEMSIZE/2, a.y-v.ITEMSIZE/2,
                    a.x+v.ITEMSIZE/2, a.y+v.ITEMSIZE/2, paint);
        }
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("LIFE: "+v.life, 40, v.paddle.y-40, paint); //목숨표시
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(0, v.getHeight()-v.controlh, v.getWidth(), v.getHeight(), paint);
        paint.setColor(Color.WHITE); //조작 UI
        paint.setTextSize(80);
        canvas.drawText("◀", v.getWidth()*0.20f, v.getHeight()-v.controlh/2, paint);
        canvas.drawText("▶", v.getWidth()*0.70f, v.getHeight()-v.controlh/2, paint); //조작버튼 그리기
    }
}


