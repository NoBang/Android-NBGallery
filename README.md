# NPhotoGallery

제가 사용하고 있는 안드로이드 스마트폰에 있는 갤러리를 불러오는 샘플입니다.
도움이 되실려나 싶어서 올려드립니다. ^ㅡ^; 버그또는... 기타 예기치 못한 오류는 알려주시면 수정해 보겠습니다.

##LG G2, 4.4.2 테스트 했습니다.

#ScreenShot

![device-2015-02-25-100612](https://cloud.githubusercontent.com/assets/11173089/6363025/9e85eb94-bcd6-11e4-876c-1b7810df87f9.png)
![device-2015-02-25-100658](https://cloud.githubusercontent.com/assets/11173089/6363031/a306fafa-bcd6-11e4-958f-db148b0ac55e.png)
![device-2015-02-25-100717](https://cloud.githubusercontent.com/assets/11173089/6363034/a5a9f56e-bcd6-11e4-80d9-c72b5d5faf2f.png)

## Usage

### Step 1

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.CAMERA"/>

    <uses-feature android:name="android.hardware.camera"/>
    <uses-feature android:name="android.hardware.camera.autofocus"/>
    

### Step 2
전체사진, 폴더별 사진으로 볼 수 있습니다.

다중선택을 가능하게 해두었습니다.

LoadPhotoActivity - IMAGE_MAX

기본은 1이고 원하는값을 넣어서 사용하시면 될꺼 같습니다.

LoadPhotoActivity - SELECT_IMAGE_COUNT 

기본은 0이고 기존에 선택한 이미지 갯수를 넣어주시면 IMAGE_MAX값을 넘지 못합니다.


IMAGE_MAX 3이고 SELECT_IMAGE_COUNT 2이면 이미지는 1장밖에 선택이 안됩니다.

잘모르시면 그냥.. 무족권 새로 불러다가 쓰시고 아니면 저에게 연락을 주세요.


## Thanks
- [Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
