package com.example.brickbreaker2;

import android.view.MotionEvent;
//터치 동작 관할 클래스
public class OnTouchEvent {
    public boolean onTouchEvent(GameView v, MotionEvent e) { //화면을 손가락으로 터치시 실행됨 안드로이드 내장 클래스
        if (v.state==-2 && e.getAction()==MotionEvent.ACTION_DOWN) {
            float y=e.getY();
            if (y > 350 && y < 450) {
                v.mode=GameView.normalmode;
                v.gamerun();
                v.state=0;
                return true;//노말모드
            }
            if (y > 520 && y < 620) {
                v.mode=GameView.hardmode;
                v.gamerun();
                v.state=0;
                return true; //하드모드
            }
            v.state=-1;
            return true; //터치하면 로비복귀
        }
        if (v.state==10 && e.getAction()==MotionEvent.ACTION_DOWN) {
            float y=e.getY();
            if (y > 350 && y < 450) {
                MainActivity.bgmSwitch(!MainActivity.bgmToggle());
                return true; //배경음악 토글
            }
            if (y > 520 && y < 620) {
                v.sfxon=!v.sfxon; //효과음 토글
                return true;
            }
            v.state=-1;
            return true; //터치하면 로비로복귀
        }
        if (v.state==11 && e.getAction()==MotionEvent.ACTION_DOWN) {
            v.state=-1;
            return true; }//  터치하면 로비로복귀
        if (v.state==-1 && e.getAction()==MotionEvent.ACTION_DOWN) {
            float x=e.getX();
            float y=e.getY();
            if (y > 400 && y < 500) {
                v.state=-2; // 모드 선택 화면으로
                return true; //게임스타트(난이도선택)
            }
            if (y > 550 && y < 650) {
                v.state=10; // 설정 상태
                return true; //설정창
            }
            if (y > 700 && y < 800) {
                v.state=11; // credit(제작자)
                return true;
            }
        }
        if (v.state==1 && e.getAction()==MotionEvent.ACTION_DOWN) {
            MainActivity.mainBgm();
            v.gamerun();
            return true;
        }
        if (v.state==2 && e.getAction()==MotionEvent.ACTION_DOWN) {
            v.gamerun();
            return true; //게임오버와 클리어 상태에서 터치시 재시작
        }
        float x=e.getX(); //안드로이드 내장 메소드 터치x좌표 반환
        float y=e.getY(); //안드로이드 내장 메소드 터치y좌표 반환
        if (y > v.getHeight()-v.controlh) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: //손가락이 화면에 닿으면(안드로이드 상수)
                case MotionEvent.ACTION_MOVE: //손가락이 화면에서 움직이면(안드로이드 상수)
                    v.movel=(x<v.getWidth() / 2f);   // 왼쪽 영역 누르면 movel=true
                    v.mover=!v.movel;              // 오른쪽 영역 누르면 mover=true
                    if (v.ball.onpaddle) {
                        v.ball.onpaddle=false;  // 이제부터 공 움직임 시작
                        v.ball.dy=-1;           // 공 위로튀김
                        v.bump=15;  // 시작 버그 방지
                    }
                    break;
                case MotionEvent.ACTION_UP://손가락을 화면에서 떼면(안드로이드 상수)
                case MotionEvent.ACTION_CANCEL:// 시스템에의해 터치 취소되면(안드로이드 상수))
                    v.movel=false;
                    v.mover=false; //움직임 멈춤
                    break;
            }
        }
        return true;
    }
}

