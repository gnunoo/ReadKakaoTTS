# -ReadKakaoTTS-

#  팀명

### PaulFrank

# 프로젝트 소개

-생활 속에서 사용자가 운전을 하는 등 스마트폰을 직접 보지 못하는 상황이 있다.이런 상황에서 해당 앱에서 카카오톡 메세지를 확인해 TTS 엔진을 통해 메세지를 음성으로 재생해준다. 또는 블루투스 기기가 연결 되어 있을 시 기기를 통해 소리가 재생 된다.스마트폰을 만질 수 없는 상황이거나 볼 수 없는 상황에서 메세지를 읽어줌으로써 사용자에게 편리함을 제공한다.


# 프로젝트 구성


![image](https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/e60037ae-48fe-4bf3-9151-d8f553249991)

# 개발 툴 및 사용 언어
개발 툴: &nbsp; <img alt="Android" src ="https://img.shields.io/badge/Android-3DDC84.svg?&style=for-the-badge&logo=Android&logoColor=black"/><br/>
언어: &nbsp; <img alt="kotlin" src ="https://img.shields.io/badge/kotlin-7F52FF.svg?&style=for-the-badge&logo=kotlin&logoColor=black"/>



# 맡은 역할
김연수: 리더 및 기능 구현<br/>
-배속 기능 만들기<br/>
-블루투스 설정 적용<br/>
이유경: 레이아웃 구성 및 디자인<br/>
-Logo: <br/>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/55912475-4a04-452a-b73f-5b110332c016' width='50px' heigth='50px'> <br/>
-설정 큰 배경, 기능 배경<br/>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/adf24531-7221-4767-a4d5-e82f443cafca' width='50px' height='50px'>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/39525e1b-6725-47f8-a1c0-983e8ffff2dd' width='50px' height='50px'> <br/>
-Swith 배경,thumb,On,Off:<br/>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/e9a309ff-f6de-4927-8d49-a45fb32f859c' width='50px' height='50px'>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/778a5854-e6fa-4bd1-a7b7-1c54c96575c7' width='50px' height='50px'>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/2c7fcfd8-9be9-4e0e-9aff-09eaa2a72b28' width='50px' height='50px'>
<img src='https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/01bf0d54-858f-4339-9def-222cb06ca480' width='50px' height='50px'> <br/>
임건우: 기능 구현<br/>
-KakaTalk 알림 받아오기<br/>
-TTS엔진 적용<br/>
최유안: 레이아웃 구성<br/>

# 코드 설명
-인트로 화면: &nbsp; postDelayed 함수를 이용해서 레이아웃 딜레이<br/>
-MyNotificationService: 상대방의 카카오톡 알림을 받아온다.<br/>
 1.NotificationListenerService()를 상속받아서 구현 한다.<br/>
 2.알림을 받아와서<br/>
 3. intent 와 ACTION_NOTIFICATION_BROADCAST 속성을 이용해서 메인 엑티비트로 전달<br/>
-MyAccessibilityService: 나와의 채팅을 받아온다.(테스트용)<br/>
-MainActivity: MyNotificationService 나 MyAccessibilityService를 받아와서 알림을 받아와서 tts객체를 이용해서 읽어온다. 블루투스 사용을 위해서 Settings.ACTION_BLUETOOTH_SETTINGS 속성 사용<br/>
# 결과물 

-레이아웃<br/>
<img src="https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/bdde89fb-7893-497e-bde6-5aabf335314b" width="200" height="400">
<img src="https://github.com/gnunoo/ReadKakaoTTS/assets/97424506/0f34f4df-1856-4b6c-8bf8-540fcda7d132" width="200" height="400">

-기능<br/>
test시 소리 잘 나옵니다<br/>
![KakaoTalk_20230629_211452401](https://github.com/gnunoo/library_system/assets/97424506/9a25d019-b911-4d25-a3c8-fce992e41213)



