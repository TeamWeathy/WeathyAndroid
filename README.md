# WeathyAndroid
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-3-blue.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->


## 기여자들 ✨

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/dingding-21"><img src="https://avatars3.githubusercontent.com/u/63945197?v=4" width="100px;" alt=""/><br /><sub><b>dingding-21</b></sub></a><br /><a href="https://github.com/TeamWeathy/WeathyAndroid/commits?author=dingding-21" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/kmebin"><img src="https://avatars3.githubusercontent.com/u/72112845?v=4" width="100px;" alt=""/><br /><sub><b>kmebin</b></sub></a><br /><a href="https://github.com/TeamWeathy/WeathyAndroid/commits?author=kmebin" title="Code">💻</a></td>
    <td align="center"><a href="https://www.mjstudio.net/"><img src="https://avatars0.githubusercontent.com/u/33388801?v=4" width="100px;" alt=""/><br /><sub><b>MJ Studio</b></sub></a><br /><a href="https://github.com/TeamWeathy/WeathyAndroid/commits?author=mym0404" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

## 안드로이드 툴 세팅

### Gradle 설정

- 컴파일 SDK 버전: `30`
- 빌드툴 버전: `29.0.3`
- 최소 SDK 버전: `23`
- 타겟 SDK 버전: `30`
- 릴리즈 빌드 설정
  - 안쓰는 리소스 제거: `true`
  - 코드 난독화 및 최적화: `true`
```groovy
release {
    signingConfig signingConfigs.release
    shrinkResources true
    minifyEnabled true
    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
}
```
- 컴파일 옵션
  - 자바 호환성 버전: `1.8`
  - 코어라이브러리 desugar: `true`
```groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
    coreLibraryDesugaringEnabled true
}
```
- 코틀린 옵션
  - JVM 타겟 버전: `1.8`
```groovy
kotlinOptions {
    jvmTarget = '1.8'
}
```
- 빌드 기능 추가
  - 데이터 바인딩: `true`
  - 뷰 바인딩: `true`
```groovy
buildFeatures {
    dataBinding true
    viewBinding true
}
```

### 코드 컨벤션

- 안드로이드 스튜디오 [Preferences] - [Editor] - [Code Style] - [Kotlin] 에서 Kotlin style guide 를 적용하고 `Reformat with code` 로 코드 포매팅

- 인터넷에 코틀린을 사용하는 [스타일 가이드를 잘 정리해둔 글](https://medium.com/mj-studio/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%9D%B4%EB%A0%87%EA%B2%8C-%EC%9E%91%EC%84%B1%ED%95%98%EC%8B%9C%EB%A9%B4-%EB%90%A9%EB%8B%88%EB%8B%A4-94871a1fa646)을 우연히 발견해 이 글에서 여러가지를 배워서 사용

### git 사용 전략

#### main(default) 브랜치

- `main` 브랜치가 default 브랜치
- `main` 에의 직접적인 `push`를 지양
- force push 는 Github branch protection 으로 막아둠

#### 기능(feature) 추가 방식

1. 기능별로 브랜치를 로컬 저장소에서 생성
2. 로컬 저장소에서 작업이 완료되면 그 브랜치를 원격 저장소(`origin`) 으로 `push`
3. Pull Request 생성 및 팀원에게 공유
4. Pull Request 병합할 때 `Create merge commit` 옵션을 사용
5. Pull Request 가 병합되면(main에) 팀원들에게 새로운 기능이 `main` 에 추가되었으니 로컬 저장소에서 `pull` 을 해서 동기화 하라고 알려줌

### Github action & Slack Bot

- 깃허브 액션으로 `push`할 때마다, 자동으로 릴리즈 빌드가 되어 팀의 슬랙 채널에 apk 파일을 전달해줌

```yml
name: Android Build
on: [push]
defaults:
  run:
    shell: bash
    working-directory: .

jobs:
  build:
    runs-on: ubuntu-latest
    name: build debug
    if: "!contains(toJSON(github.event.commits.*.message), '[skip action]') && !startsWith(github.ref, 'refs/tags/')"
    steps:
      - name: Checkout repository
        uses: actions/checkout@v2
      - name: Gradle cache
        uses: actions/cache@v2
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
          restore-keys: |
            ${{ runner.os }}-gradle-
      - name: Build Release
        if: ${{ contains(github.ref, 'main') }}
        run: ./gradlew assembleRelease

      - name: Build Debug
        if: ${{ !contains(github.ref, 'main') }}
        run: ./gradlew assembleDebug

      - name: Archive artifacts
        uses: actions/upload-artifact@v2
        with:
          path: app/build/outputs
      - name: Update Release apk name
        if: ${{ success() && contains(github.ref, 'main') }}
        run: |
          mv app/build/outputs/apk/release/app-release.apk 웨디-릴리즈.apk
          echo 'apk=웨디-릴리즈.apk' >> $GITHUB_ENV
      - name: Upload APK
        if: ${{ success() && contains(github.ref, 'main') }}
        run: |
          curl -X POST \
          -F file=@$apk \
          -F channels=${{secrets.SLACK_CHANNEL_ID}} \
          -H "Authorization: Bearer ${{secrets.SLACK_BOT_API_TOKEN}}" \
          https://slack.com/api/files.upload
      - name: On success, Notify with Slack
        if: ${{ success() && contains(github.ref, 'main') }}
        uses: rtCamp/action-slack-notify@master
        env:
          SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK_ANDROID }}
          SLACK_TITLE: '안드로이드 릴리즈 빌드 성공 ✅'
          MSG_MINIMAL: true
          SLACK_MESSAGE: 'apk가 생성되었습니다'
      - name: On fail, Notify with Slack
        if: ${{ failure() }}
        uses: rtCamp/action-slack-notify@master
        env:
          SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK_ANDROID }}
          SLACK_TITLE: '안드로이드 빌드 실패 ❌'
          MSG_MINIMAL: false
          SLACK_MESSAGE: '에러를 확인해주세요'
```

## 사용 라이브러리와 목적

- Glide: url 형태의 이미지를 다운받아 `ImageView`에 표시해주는 용도. 캐시도 자동으로 해줌
- Retrofit: `OkHttp3`를 내부적으로 사용하여 Rest API를 사용하게 해줄 수 있는 어노테이션 프로세서를 이용한 자바 라이브러리. 서버와의 통신에 사용
- Material Design Component: 구글이 개발한 매터리얼 디자인을 안드로이드에서 쉽게 사용할 수 있는 구현체들을 제공하는 라이브러리. UI에 적용
- SwipeRefreshLayout: 당겨서 리프레시 할 수 있는 레이아웃을 제공.
- AAC Lifecycle: LiveData, Lifecycle, ViewModel 등 안드로이드 생명주기와 연동된 컴포넌트들과 클래스들을 제공
- Kotlin Standard Library: 코틀린의 확장 클래스들을 제공해주고 컬렉션을 손쉽게 사용할 수 있게 해줌
- Jetpack Activity: 안드로이드의 `Activity`를 계속 발전시켜 새로운 기능들을 사용할 수 있게 해줌
- Jetpack Fragment: 안드로이드의 `Fragment`를 계속 발전시켜 새로운 기능들을 사용할 수 있게 해줌
- Jetpack Core KTX
- ConstraintLayout: `ConstraintLayout`을 사용하여 뷰들을 레이아웃에 손쉽게 배치해주게 해줌. 그리고 `MotionLayout`도 제공
- Dexter: 권한 허용을 쉽게 도와주는 라이브러리. 위치 권한 허용에 사용
- Google Mobile Service Location: 현재 위치를 받아오거나 지오코딩 하는데 사용
- Dynamic animation: Spring 물리를 이용한 애니메이션을 구현하는 데 사용
- Desugar JDK Library: `java.time` 패키지 유틸리티들을 사용하기 위한 desugaring에 사용
- Flipper: 디버깅에 사용


## 사용한 기술 스택

- AAC DataBinding, ViewModel
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        ...
    }
```
- 비동기 작업 - Coroutine
```kotlin
override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    initialJob = GlobalScope.launch(Dispatchers.Main) {
        ...
    }
}

override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    initialJob.cancel()
}
```
- 날짜 처리 - `java.time.LocalDate`, `java.util.Calendar`
  - Java 8의 패키지이기 때문에 사용하려면 desugaring을 해주어야 함
```kotlin
val LocalDate.weekOfMonth: Int
    get() {
        val gc = GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault()))
        gc.firstDayOfWeek = SUNDAY
        gc.minimalDaysInFirstWeek = 1
        return gc[WEEK_OF_MONTH]
    }
```



## 각자 맡은 부분 및 역할 작성

## 프로젝트 구조

## 핵심 기능 구현 방법 코드

## 구현 화면

## 회의록 주소
