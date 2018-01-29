# 실시간 드론 비디오 스트리밍 솔루션 구현

## 구성원

* 김준희 : Connecting Software Module 및 Web Server 구현, Documentation
* 김형민 : Raspberry PI 환경 구축 및 Android Application 구현
* 김지나 : Android UI 및 Android Application 구현
* 최용석 : FFServer 환경 구축 및 Connecting Software Module 구현

## 동영상

* [Youtube](https://www.youtube.com/watch?v=55R1oghmro4&feature=youtu.be)


## 마커
* <a href="#1">연구 배경</a>
* 시스템 설계
* 구현
* 작품 환경 설정
* 작품 사용법
* 참고


<br/><br/>
## <span id="#1">연구 배경</span>

1. 드론 DJI) 드론에 대한 개요

드론은 무선 전파의 유도에 의해서 비행하는 비행기나 헬리콥터 모양의 무인항공기(UAV: unmanned aerial vehicle)를 총칭하는 것이다.
드론은 100년이 넘는 역사를 가지고 있으며 원래 군사용 무기로 개발됐다. 레이더와 무선통신 원리를 적용해 원격조정이 가능한 무인비행기를 만들면 공중전에서 조종사의 인명 피해를 줄일 수 있다는 생각이었다.
현재로 넘어오며 드론은 군사용 측면만이 아닌 다양한 분야에서 사용되고 있다. 드론은 사람이 가지 못하는 곳에 갈 수 있기 때문에 있어 놀라운 장관을 이루는 장면이나 사람이 가기 위험해 우리가 일반적으로 볼 수 없는 곳을 촬영할 수 있다. 이러한 드론에 가능성을 본 DJI는 2006년에 설립되어 10년 동안 무인 항공기 분야의 대표로서 자리 잡았다.
DJI의 드론은 비행과 카메라 안정화 시스템이 구현되어 있어 현재 다양한 산업에 혁신을 가져오고 있다. DJI에서 제공한 SDK를 이용한다면 DJI의 모든 드론에게 적용되어 다양한 소프트웨어를 개발할 수 있다.

2. Raspberry Pi

라즈베리 파이는 영국의 라즈베리파이 재단이 학교에서 기초 컴퓨터 과학교육을 증진시키기 위한 단일 칩을 사용한 저가형 싱글보드 컴퓨터이다. 라즈베리파이는 데비안, 아치 리눅스 및 QtonPi 등의 리눅스 배포판의 운영체제가 탑재 가능하기 때문에 리눅스에서 실행되는 모든 응용프로그램을 실행시킬 수 있어 응용성이 매우높다. 센서나 카메라 등을 연결하기 쉬운 하드웨어 구조를 가지고 있으며, 센서들로부터 값을 읽고 제어할 수 있는 다양한 라이브러리가 지원되기 때문에 개발자는 임베디드 제품이나 IoT 장치를 쉽게 개발할 수 있다.

3. FFmpeg

FFmpeg는 디지털 음성 스트림과 영상 스트림에 대해 수많은 종류의 형태로 기록 및 저장을 해 주는 프로그램이다. FFmpeg는 사용자가 명령어를 입력 받아 작동하는 소프트웨어로서 자유 소프트웨어와 오픈 소스 라이브러리로 구성되어 있다. 여러 라이브러리 중 음성/영상 코덱 라이브러리인 ibavccodec도 들어있으며 libavformat라는 음성/영상 다중화, 역다중화 라이브러리도 들어있으며 그밖에  멀티미디어 컨테이너의 Demuxer, Muxer 라이브러리, 입출력 장치 제어 라이브러리, 미디어필터, 오디오필터 처리 라이브러리 등이 들어있다.
다음 팟 플레이어, 곰플레이어, MXPlayer 등 다수의 유명한 코덱 내장형 동영상 플레이어들이 FFmpeg의 libavcodec을 기반으로 하고 있다.
FFmpeg은 리눅스 기반으로 개발되었지만, 애플, 윈도, 아미가OS 등 대부분의 운영 체제에서 컴파일이 가능하다. 그러한 이유로 다양한 운영체제에서 사용이 가능하다.

![FFserver](/img/ffmpeg_working_principle.png)<br/>
-그림 1. FFServer의 내부 작동 원리
Fig. 1. Internal working principle of FFServer

FFServer는 2016 년 7 월 10 일에 사람들이 이해하기 어려운 혼란스러운 파일 구성 구문이 많아 지원 종료를 결정 하였다.
FFServer는 다양한 비디오, 오디오 스트림 및 인코딩 옵션을 지정할 수 있으며 FFmpeg와 연결을 통한 라이브 피드나 파일을 스트리밍 할 수 있다.
FFServer는 HTTP 서버 역할을 하고 요청을 수락 시 FFmpeg에게 스트림을 얻은 후 RTSP 클라이언트 또는 HTTP 클라이언트가 스트림 미디어 컨텐츠로 요청을 제공하는 역할을 한다.


<br/><br/>
## 시스템 설계

1. 시스템구성

![Structure](/img/structure.png)<br/>
-그림 2. 전체 시스템구성
Fig. 2. System View


안드로이드 앱은 DJI SDK를 이용해 드론 컨트롤러에서 실시간으로 H.264포맷인 비디오 데이터를 받아온다. H.264 포맷의 약 4000byte로 받아온 데이터를 30kbyte로 Framming 한 뒤 2가지 블로킹 큐에 담는다. 그 중 한 가지는 화면에 출력하기 위한 데이터를 담는 큐이고 다른 하나는  방송 송출을 위한 비디오 데이터를 전송하기 위한 큐이다. 화면에 출력하기 위한 데이터는 안드로이드 Media Codec을 이용해 화면에 스트리밍 하게 되고 방송 송출을 위한 비디오 데이터는 웹 페이지에 스트리밍 하기 위해 라즈베리 파이의 FFmpeg Video Converter에서 WebM으로 Converting 과정을 거친 후 FFServer에 feed로 준다. 그 후 FFServer에서 아파치 웹 서버에 올린 웹 페이지에 접속해 실시간으로 스트리밍 되는 영상을 받아온다.
Raspberry PI를 Start 하게 되면 Connecting Software Module이 FFServer와 FFmpeg Video Converter를 실행시킨다.

전체적인 알고리즘은 다음과 같다.

![Structure](/img/algorithm.png)<br/>
-그림 3. 안드로이드 앱과 라즈베리 파이 사이의 통신 알고리즘
Fig. 3. Data communications algorism between Android App and Raspberry PI

2. 드론 비디오 스트림 인터페이스

안드로이드 앱은 총 3개의 스레드로 구성되었으며 각각 시스템 통합 제어 프로그램과의 통신, 앱의 surface view에 드론의 카메라 영상을 출력, 비디오 데이터를 방송 송출 시스템으로 전송하는 기능을 담당한다. 안드로이드 앱에는 두개의 큐를 구현했는데 한 가지는 화면에 출력하기 위한 데이터를 담을 큐이고 다른 하나는 방송 송출을 위해 비디오 데이터를 전송 하는 용도이다. 안드로이드 앱이 실행되면 MainActivity에서 main thread가 동작하기 시작한다. main thread는 드론으로부터 DJI SDK를 이용해 받은 H.264포맷의 비디오 데이터를 실시간으로 화면 에 출력한다. Message thread와 Streaming thread는 방송 송출이 시작될 때 동작한다. Message thread는 라즈베리파이의 시스템 통합제어 프로그램과 TCP통신 방식의 handshake과정을 거쳐 비디오 데이터를 전송할 URL을 받아온다. Streaming thread는 전달받은 URL로 UDP 방식으로 비디오 데이터 전송을 시작한다.


![Structure](/img/app_structure.png)<br/>
-그림 4. 드론으로 부터 비디오 스트림을 받는 안드로이드 앱의 구조
Fig. 4. Structure of Android app that receives video stream from drones

3. 시스템 통합제어 프로그램

라즈베리파이 보드에는 세가지의 주요 소프트웨어가 작동한다. (하나는 FFmpeg 모듈로 안드로이드 앱이 보내오는 h.264형식의 비디오 데이터를 FFServer가 입력으로 받는 포맷인 .ffm 형식으로 변환하는 기능과 다른 하나는 FFServer 모듈로 .ffm 파일을 웹으로 실시간 스트리밍 하는 기능)FFmpeg,와 FFServer, 그리고 앞의 두 모듈과 안드로이드 앱을 제어하는 시스템 통합제어 프로그램이다. 통합제어 시스템은 자바로 작성되어 있는 프로그램이다. 시스템 통합제어 프로그램은 안드로이드 앱과 통신하기 위해 TCP방식을 사용한다. 통신은 안드로이드 앱이 스트리밍 시작을 요청하는 메시지와 비디오 데이터를 받을 FFmpeg의 URL을 응답하는 메시지로 구성된다. 안드로이드 앱으로 부터 스트리밍 시작 메시지를 받으면 FFmpeg를 구동시킨 뒤 그 URL을 안드로이드 앱에 전송한다. 메시지 통신이 끝나면 안드로이드 앱은 전달받은 FFmpeg URL로 비디오 데이터를 전송하기 시작한다. 실시간으로 스트리밍을 하는 도중 네트워크의 상태가 불안정하거나 안드로이드 앱이 갑자기 종료되는 등의 접속이 끊어지는 사건이 발생할 수 있다. 이러한 경우  FFmpeg는 종료되는 것이 아니라 대기상태에 들어가게 되는데 이는 전체 시스템을 마비시킨다. 따라서 시스템 통합제어 프로그램은 AliveChecking 메소드를 통해 FFmpeg와 앱이 통신을 지속하고 있는지 실시간으로 확인하고 그렇지 않은 경우 종료시키는 기능을 가진다.

4. FFmpeg와 비디오 소스의 스트림 통신

드론과 연결 되어있는 스마트폰이 한 프레임씩 큐에서 가져온 h.264 방식의 Raw Video 데이터를 UDP방식으로 보낸다.
FFmpeg 모듈에서 데이터를 받아 WebM방식으로 인코딩 과정을 거친다.
FFServer는 .config 설정파일을 이용해 시작하며 클라이언트를 받을 준비를 한다.
FFmpeg가 인코딩 과정을 거친 데이터를 .ffm형태의 feed로 FFServer에게 보내게 된다.
클라이언트가 Apache 웹서버에 올라가 있는 웹 페이지에 접속하게 되면 웹 페이지 내의 Video태그 url을 통해 FFServer에 접속한다.
FFServer는 클라이언트의 접속을 받으면 HTTP프로토콜을 이용하여 WebM형태로 스트리밍 데이터를 보내준다.

![Structure](/img/ffmpeg_working_principle.png)<br/>
-그림 5. FFmpeg의 내부 작동 원리
Fig. 5. Internal working principle of FFmpeg


<br/><br/>
## 구현

1. 시스템 구현

![Structure](/img/drone.png)
![Structure](/img/controler.png)
-그림 6. Phantom 4 pro와 조종기
Fig. 6. Phantom 4 pro and controller

그림 6은 DJI사의 Phantom 4 pro와 조종기다. 기체에 탑제된 카메라는 1인치 20메가픽셀 CMOS센서를 탑제하고, DJI사에서 직접 제작한 7군8매의 렌즈를 갖추었다. 영상 시스템은 H.264 4K/60f와 H.265 4K/30fps 동영상을 100Mbps속도로 촬영한다.

![Structure](/img/application1.png)
![Structure](/img/application2.png)
-그림 7. 안드로이드 앱
Fig. 7. Android Application

그림 7은 DJI SDK를 활용하여 제작한 안드로이드 어플이다. DJI사에서는 개발자들이 활용하여 개발할 수 있는 안드로이드SDK를 제공한다. DJI SDK는 자바문법으로 구성되어 있고 Native 메소드들이 동작하여 디바이스들을 제어한다. SDK는 FC(Flight Control),Mission Control, 카메라 등 안드로이드에서 드론을 제어할 수 있는 여러 기능을 제공하는데 HIFLY 프로젝트는 카메라를 제어하는 메소드들을 활용했다.

![Structure](/img/hardware.png)
-그림 8. 하드웨어 구성
Fig. 8. Hardware Configuration

그림 8의 라즈베리파이는 라즈베리파이 모델3B로 데비안 기반의 라즈비안 리눅스가 설치되었다. 소프트웨어는 비디오 데이터를 변환하는 FFmpeg, 변환된 데이터를 송출하는 FFserver, 전체 시스템을 제어하는 시스템 통합제어 프로그램, 시청자들에게 제공되는 아파치 웹서버가 탑제되었다.
라즈베리파이와 전원을 공급하는 배터리, 네트워크를 구성할 AP를 담기 위해 3D프린터를 이용하여 직접 맞춤 케이스를 제작했다. 아래 그림()에서 보이는 것처럼 케이스에는 라즈베리파이의 온도를 조절하기 위한 환풍구와, 턱을 만들어 각 하드웨어들을 고정 시킬 수 있도록 모델링 했다.

![Structure](/img/streamming_screen.png)
-그림 9. 안드로이드 스트리밍 화면
Fig. 9. Android Streamming

그림 9는 드론 컨트롤러에 연결되는 안드로이드 앱이다. 컨트롤러와 연결이 시작되면 드론의 카메라가 촬영한 비디오 데이터를 화면에 출력한다. 사용자가 좌측의 스트리밍 시작(“Start Streaming”)버튼을 누르면 앱은 라즈베리 파이의 방송 송출시스템으로 받아온 비디오 데이터를 전송하기 시작한다.


<br/><br/>
## 작품 환경 설정

1. Raspberry pi 3 기본 사양

Broadcom BCM2837 64비트 ARM Cortex-A53 쿼드 코어 프로세서 SoC(속도 1.2GHz)<br/>
RAM 1GB<br/>
10/100 이더넷(RJ45)<br/>
BCM43143 와이파이 내장<br/>
BLE(저전력 블루투스) 보드 내장<br/>
전력 요구량: 마이크로 USB 통해 5V(2.4A)

2. FFServer Config 설정

```Format webm```<br/>
```NoAudio```<br/>
```VideoSize 160x120```<br/>
```VideoFrameRate 15```<br/>
```AVOptionVideo qmin 1```<br/>
```AVOptionVideo qmax 31```  품질을 최소 1에서 31로 고정한다.(1이 가장 좋은 품질)<br/>
```PreRoll 0```  몇 초 동안 버퍼에 쌓는지 결정(delay) realtime이기 때문에 0초로 설정한다.<br/>
```StartSendOnKey```  첫 번째 키 프레임을 얻을 때까지 스트림을 보내지 않는다.<br/>

3. FFmpeg Config 설정

<pre>
ffmpeg –i udp://@IP:PORT –vcodec libvpx yuv420p –threads 8 –cpu-used 5 –deadline realtime –framerate 15 –preset ultrafast –an http://FFserverIP:PORT/feed1.ffm
</pre>


4. Raspberry pi 3 수정 Config 설정

```temp_limit=85```  Throttling의 온도를 85도로 지정한다.<br/>
CPU 오버클럭을 설정한다.<br/>
```arm_freq=900```    ARM CPU 주파수 값<br/>
```gpu_freq=200```    GPU 주파수 값<br/>
```core_freq=250```    GPU Processer Core의 주파수<br/>
```sdram_freq=450```  MHz단위의 sdram 주파수<br/>
```force_turbo=1```    ARM 코어가 사용 중이 아닌 경우에도 터보 모드 주파수를 강제<br/>
```over_voltage=2```   CPU GPU Core 전압 조정<br/>


<br/><br/>
## 작품 사용법




<br/><br/>
## 참고

* <https://www.ffmpeg.org/>
* <https://en.wikipedia.org/wiki/FFmpeg>
* <https://www.dji.com/kr>
* <http://interactive.donga.com/drone/index.php?mid=Drone_page_22>
* <https://www.raspberrypi.org/documentation/configuration/config-txt/overclocking.md>
* <https://www.ffmpeg.org/>
* <https://www.ffmpeg.org/ffserver.html>
