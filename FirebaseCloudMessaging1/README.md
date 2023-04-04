# FCM

## 알림 메시지
1. 구현 쉬움
2. 백그라운드에 있을 시 처리 곤란
3. 유저가 포그라운드에서 있을 시 앱 자체에서 처리
<br />(앱이 백그라운드 상태이면 알림 메시지가 알림 목록으로 전송됩니다. 포그라운드 상태의 앱인 경우 콜백 함수가 메시지를 처리합니다.)
<br />Ex)
<br />{
  <br />"message":{
    <br />"token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
    <br />"notification":{
      <br />"title":"Portugal vs. Denmark",
      <br />"body":"great match!"
    <br />}
  <br />}
<br />}

## 데이터 메시지
1. 포그라운드에 있든 백그라운드에 있든 앱 자체에서 처리
<br />Ex) 
<br />{
  <br />"message":{
    <br />"token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
    <br />"data":{
      <br />"Nick" : "Mario",
      <br />"body" : "great match!",
      <br />"Room" : "PortugalVSDenmark"
    <br />}
  <br />}
<br />}

## 데이터 페이로드가 포함된 알림 메시지
1. 백그라운드 상태인 경우 알림 페이로드가 앱의 알림 목록에 수신되며 사용자가 알림을 탭한 경우에만 앱이 데이터 페이로드를 처리합니다.
2. 포그라운드 상태인 경우 앱에서 페이로드가 둘 다 제공되는 메시지 객체를 수신합니다.
<br />Ex)
<br />{
  <br />"message":{
    <br />"token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
    <br />"notification":{
      <br />"title":"Portugal vs. Denmark",
      <br />"body":"great match!"
    <br />},
    <br />"data" : {
      <br />"Nick" : "Mario",
      <br />"Room" : "PortugalVSDenmark"
    <br />}
  <br />}
<br />}

## 메시지 처리는 어디에서 진행되는지? 
https://firebase.google.com/docs/cloud-messaging/android/receive?hl=ko#handling_messages

## 토큰이 갱신되는 시기
1. 새 기기에서 앱 복원
2. 사용자가 앱 제거/재설치
3. 사용자가 앱 데이터 소거

## 안드로이드 8.0부터는 알림 채널을 꼭 만들어 줘야한다.
https://developer.android.com/training/notify-user/build-notification

## 알림 우선순위
https://developer.android.com/training/notify-user/channels?hl=ko#importance

