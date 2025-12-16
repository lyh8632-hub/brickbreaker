package com.example.brickbreaker2;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
public class MainActivity extends AppCompatActivity { //앱 실행될때 가장먼저 실행되는 메인 액티비티
    private static MediaPlayer bgmMain; //게임중,로비에서 재생되는 배경음악
    private static MediaPlayer bgmGameOver;//게임오버 배경음악
    private static boolean bgmOn=true;//배경음악 온오프 토글
    GameView gameView;
    @Override //AppCompatActivity (안드로이드 제공)클래스 상속 이하 오버라이드도 마찬가지
    protected void onCreate(Bundle savedInstanceState) {//화면구성, 배경음악 초기설정,앱실행시 처음호출되는 메서드
        super.onCreate(savedInstanceState);
        gameView=new GameView(this);
        setContentView(gameView);
        if (bgmMain==null) {
            bgmMain=MediaPlayer.create(this, R.raw.background);
            bgmMain.setLooping(true);
            bgmMain.setVolume(0.6f, 0.6f);
        }
        if (bgmGameOver==null) {
            bgmGameOver=MediaPlayer.create(this, R.raw.gameover);
            bgmGameOver.setLooping(true);
            bgmGameOver.setVolume(0.6f, 0.6f);
        }
        mainBgm(); //앱시작시 브금실행
        bgmSwitch(bgmOn);//브금 온오프 설정 저장
    }
    public static void mainBgm() {//배경음악 재생 메서드
        if (!bgmOn) return;
        if (bgmGameOver!=null && bgmGameOver.isPlaying())
            bgmGameOver.pause();
        if (bgmMain!=null && !bgmMain.isPlaying()) {
            bgmMain.start();
        }
    }
    public static void gameoverBgm() {//게임오버 전용 배경음악
        if (!bgmOn) return;
        if (bgmMain!=null && bgmMain.isPlaying())
            bgmMain.pause();//메인배경음악 정지
        if (bgmGameOver!=null && !bgmGameOver.isPlaying()) {//게임오버 음악 재생
            bgmGameOver.seekTo(0);
            bgmGameOver.start();
        }
    }
    @Override
    protected void onPause() {//앱 백그라운드로 이동할때
        super.onPause();
        if (bgmMain!=null && bgmMain.isPlaying()) bgmMain.pause();
        if (bgmGameOver!=null && bgmGameOver.isPlaying()) bgmGameOver.pause();//재생중인 모든 배경음악 정지
    }
    @Override
    protected void onDestroy() {//앱종료시 MediaPlayer 자원해제
        super.onDestroy();
        if (bgmMain!=null) {
            bgmMain.release();
            bgmMain=null;
        }
        if (bgmGameOver!=null) {
            bgmGameOver.release();
            bgmGameOver=null;
        }
    }
    public static void bgmSwitch(boolean on) {//배경음악 온오프 제어
        bgmOn=on;//기본은 배경음악 ON
        if (!bgmOn) {
            if (bgmMain!=null && bgmMain.isPlaying()) bgmMain.pause();
            if (bgmGameOver!=null && bgmGameOver.isPlaying()) bgmGameOver.pause();
        } else {
            if (bgmMain!=null && !bgmMain.isPlaying()) {
                bgmMain.start();
            }
        }
    }
    @Override
    public void onBackPressed() {//뒤로가기 버튼처리
        if (gameView!=null && gameView.onBackPressed()) {
            return; // GameView가 처리함 (로비로 이동)
        }
        super.onBackPressed(); // 앱 종료
    }
    public static boolean bgmToggle() {
        return bgmOn;
    }//배경음악 온오프 상태 반환
}

