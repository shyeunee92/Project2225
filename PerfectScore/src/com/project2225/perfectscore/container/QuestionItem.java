package com.project2225.perfectscore.container;

public class QuestionItem {
	public int idx;//idx
	public String type;//문제타입
	public String category;//카테고리
	public String question;//문제타이틀
	public String selection;//보기
	public String answer;//정답
	public String time;
	public int user;//user 1 : 내꺼, 2 : 다른 사람꺼
	public int isUsed;//isused 1,0
	public boolean isChecked=false;
}
