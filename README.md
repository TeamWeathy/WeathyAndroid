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

- 홈: 현지
- 검색: 명주
- 날씨추가: 희빈
- 캘린더: 명주
- 설정: 현지

## 프로젝트 구조(패키지 분류 이미지)

## 구현 화면

## 회의록 주소

## 핵심 기능 구현 방법 코드

- 현재 위치 받아오기 (`LocationService`, `FusedLocationProviderClient`, `Geocoder`)
***LocationUtil.kt***
```kotlin
class LocationUtil (private val app: Application) : DefaultLifecycleObserver {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
    private val geoCoder = Geocoder(app, Locale.KOREA)

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val _lastLocation = MutableLiveData<Location>()
    val lastLocation: LiveData<Location> = _lastLocation
    private val _isLocationAvailable = MutableLiveData(false)
    val isLocationAvailable: LiveData<Boolean> = _isLocationAvailable


    override fun onStart(owner: LifecycleOwner) {
        registerLocationListener()
    }

    override fun onStop(owner: LifecycleOwner) {
        unregisterLocationListener()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            _lastLocation.value = result.lastLocation
        }

        override fun onLocationAvailability(result: LocationAvailability?) {
            result ?: return
            _isLocationAvailable.value = result.isLocationAvailable
        }
    }

    private fun registerLocationListener() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun unregisterLocationListener() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun reverseGeocode() {
        lastLocation.value?.let { location ->
            debugE(geoCoder.getFromLocation(location.latitude, location.longitude, 1).first())
        }
    }
}
```

- 커스텀 뷰(`WeathyCardView`)
***WeathyCardView.kt***
```kotlin
class WeathyCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val defaultShadowColor = Color.BLACK

    var radius by OnChangeProp(35.dpFloat) {
        updateUI()
    }
    var shadowColor by OnChangeProp(defaultShadowColor) {
        updateUI()
    }
    var disableShadow by OnChangeProp(false) {
        updateUI()
    }
    var strokeColor by OnChangeProp(Color.TRANSPARENT) {
        updateUI()
    }
    var strokeWidth by OnChangeProp(0f) {
        updateUI()
    }

    init {
        if (attrs != null) {
            getStyleableAttrs(attrs)
        }

        updateUI()
    }

    private fun getStyleableAttrs(attr: AttributeSet) {
        context.theme.obtainStyledAttributes(attr, R.styleable.WeathyCardView, 0, 0).use { arr ->
            radius = arr.getDimension(R.styleable.WeathyCardView_weathy_radius, 35.dpFloat)
            shadowColor = arr.getColor(R.styleable.WeathyCardView_weathy_shadow_color, defaultShadowColor)
            disableShadow = arr.getBoolean(R.styleable.WeathyCardView_weathy_disable_shadow, false)
            strokeColor = arr.getColor(R.styleable.WeathyCardView_weathy_stroke_color, Color.TRANSPARENT)
            strokeWidth = arr.getDimension(R.styleable.WeathyCardView_weathy_stroke_width, 0f)
        }
    }

    private fun updateUI() {
        background = MaterialShapeDrawable(ShapeAppearanceModel().withCornerSize(radius)).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
            strokeWidth = this@WeathyCardView.strokeWidth
            strokeColor = ColorStateList.valueOf(this@WeathyCardView.strokeColor)
        }
        setShadowColorIfAvailable(shadowColor)

        elevation = if (disableShadow) 0f else px(8).toFloat()


        invalidate()
    }
}
```

- 캘린더 뷰 LocalDate two-way data binding
```kotlin
@BindingAdapter("curDate")
fun CalendarView.setCurDate(date: LocalDate) {
    if (curDate != date) curDate = date
}

@InverseBindingAdapter(attribute = "curDate")
fun CalendarView.getCurDate(): LocalDate = curDate

@BindingAdapter("curDateAttrChanged")
fun CalendarView.setListener(attrChange: InverseBindingListener) {
    onDateChangeListener = OnDateChangeListener {
        attrChange.onChange()
    }
}
```

```xml
<team.weathy.view.calendar.CalendarView
    curDate="@={vm.curDate}"
    android:id="@+id/calendarView"
    android:layout_width="match_parent"
    android:layout_height="220dp"
    app:layout_constraintTop_toTopOf="parent" />
```

- 캘린더 뷰(`CalendarView`)
***CalendarView.kt***
```kotlin
class CalendarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val today = LocalDate.now()

    var curDate: LocalDate by OnChangeProp(LocalDate.now()) {
        updateUIWithCurDate()
        onDateChangeListener?.onChange(it)
        scrollToTop()
        invalidate()
    }
    var onDateChangeListener: OnDateChangeListener? = null

    private val isTodayInCurrentMonth
        get() = curDate.year == today.year && curDate.month == today.month
    private val isTodayInCurrentWeek
        get() = isTodayInCurrentMonth && curDate.weekOfMonth == today.weekOfMonth


    private val animLiveData = MutableLiveData(0f)
    private var animValue by OnChangeProp(0f) {
        adjustUIsWithAnimValue()
        animLiveData.value = it
    }

    private val scrollEnabled = MutableLiveData(false)
    private val onScrollToTop = MutableLiveData<Once<Unit>>()

    private val collapsedHeight
        get() = px(MIN_HEIGHT_DP)
    private val expandedHeight
        get() = screenHeight - px(EXPAND_MARGIN_BOTTOM_DP)

    private val paddingHorizontal = px(24)

    private val dateText = TextView(context).apply {
        id = ViewCompat.generateViewId()
        textSize = 25f
        if (!isInEditMode) typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        setTextColor(getColor(R.color.main_grey))
        gravity = Gravity.CENTER

        layoutParams = LayoutParams(0, WRAP_CONTENT).apply {
            topToTop = parentId
            leftToLeft = parentId
            rightToRight = parentId
            topMargin = px(16)
        }
    }

    private val todayButton = ImageButton(context).apply {
        setImageResource(R.drawable.ic_today)
        scaleType = FIT_CENTER

        val outValue = TypedValue()
        context.theme.resolveAttribute(attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)

        setOnDebounceClickListener {
            curDate = today
        }

        layoutParams = LayoutParams(px(32), px(32)).apply {
            setPadding(px(6), px(6), px(6), px(6))
            topToTop = dateText.id
            bottomToBottom = dateText.id
            rightToRight = parentId
            rightMargin = px(30)
        }
    }

    private val topDivider = View(context).apply {
        id = ViewCompat.generateViewId()
        setBackgroundColor(getColor(R.color.sub_grey_5))

        layoutParams = LayoutParams(MATCH_PARENT, px(1)).apply {
            topToBottom = dateText.id
            topMargin = px(16)
        }
    }

    private val weekTextLayout = LinearLayout(context).apply {
        id = ViewCompat.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        weightSum = 7f

        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topToBottom = topDivider.id
            topMargin = px(20)
        }
    }
    private val weekTexts = (0..6).map {
        TextView(context).apply {
            id = ViewCompat.generateViewId()
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            text = listOf("일", "월", "화", "수", "목", "금", "토")[it]
            gravity = Gravity.CENTER

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)
        }
    }


    private val viewPagerLayoutParams = {
        LayoutParams(MATCH_PARENT, 0).apply {
            topToBottom = weekTextLayout.id
            bottomToBottom = parentId
            bottomMargin = px(32)
        }
    }

    private var isExpanded = false
    private fun expand() {
        isExpanded = true

        enableScroll()
        enableTouchMonthlyPagerOnly()

        AnimUtil.runSpringAnimation(animValue, 1f, 500f) {
            animValue = it
        }

        scrollToTop()
        invalidate()
    }

    private fun collapse() {
        isExpanded = false
        disableScroll()
        enableTouchWeeklyPagerOnly()

        AnimUtil.runSpringAnimation(animValue, 0f, 500f) {
            animValue = it
        }

        scrollToTop()
        invalidate()
    }

    private val monthlyViewPagerGenerator = {
        ViewPager2(context).apply {
            layoutParams = viewPagerLayoutParams()

            adapter = MonthlyAdapter(animLiveData, scrollEnabled, onScrollToTop)
            setCurrentItem(MonthlyAdapter.MAX_ITEM_COUNT, false)
            alpha = 0f

            setPageTransformer { page, position ->
                page.pivotX = if (position < 0) page.width.toFloat() else 0f
                page.pivotY = page.height * 0.5f
                page.rotationY = 35f * position
            }

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val newDate = convertMonthlyIndexToDate(position)
                    if (isExpanded && curDate != newDate) {
                        curDate = newDate
                    }
                }
            })
        }
    }
    private var monthlyViewPager: ViewPager2? = null
    private val weeklyViewPager = ViewPager2(context).apply {
        layoutParams = viewPagerLayoutParams()

        adapter = WeeklyAdapter(animLiveData)
        setCurrentItem(WeeklyAdapter.MAX_ITEM_COUNT, false)

        setPageTransformer { page, position ->
            page.pivotX = if (position < 0) page.width.toFloat() else 0f
            page.pivotY = page.height * 0.5f
            page.rotationY = 35f * position
        }

        registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val newDate = convertWeeklyIndexToDate(position)
                if (!isExpanded && curDate != newDate) {
                    curDate = newDate
                }
            }
        })
    }

    private lateinit var initialJob: Job

    init {
        initContainer()
        addViews()
        configureExpandGestureHandling()
        updateUIWithCurDate()
        enableTouchWeeklyPagerOnly()
        adjustWeekTextColors()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        initialJob = GlobalScope.launch(Dispatchers.Main) {
            delay(700L)

            if (monthlyViewPager == null) {
                monthlyViewPager = monthlyViewPagerGenerator()
                addView(monthlyViewPager!!, 0)
                updateUIWithCurDate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        initialJob.cancel()
    }

    private fun updateUIWithCurDate() {
        dateText.text = "${curDate.year} .${curDate.monthValue.toString().padStart(2, '0')}"

        val nextMonthlyIndex = convertDateToMonthlyIndex(curDate)
        val nextWeeklyIndex = convertDateToWeeklyIndex(curDate)

        if (monthlyViewPager?.currentItem != nextMonthlyIndex) {
            monthlyViewPager?.setCurrentItem(
                nextMonthlyIndex, false
            )
        }

        if (weeklyViewPager.currentItem != nextWeeklyIndex) {
            weeklyViewPager.setCurrentItem(
                nextWeeklyIndex, false
            )
        }

        adjustWeekTextColors()
    }

    private fun initContainer() {
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        background = MaterialShapeDrawable(
            ShapeAppearanceModel().toBuilder().setBottomLeftCorner(CornerFamily.ROUNDED, px(35).toFloat())
                .setBottomRightCorner(CornerFamily.ROUNDED, px(35).toFloat()).build()
        ).apply {
            fillColor = ColorStateList.valueOf(getColor(R.color.white))
        }
        elevation = px(4).toFloat()
    }

    private fun addViews() {
        addView(dateText)
        addView(todayButton)
        addView(topDivider)
        addWeekLayoutAndWeekTexts()
        addView(weeklyViewPager)
    }

    private fun addWeekLayoutAndWeekTexts() = weekTextLayout.also { layout ->
        addView(layout)
        weekTexts.forEach(layout::addView)
    }

    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
    }
    private val weekCapsulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.main_mint)
        setShadowLayer(12f, 0f, 0f, getColor(R.color.main_mint))
    }
    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            width / 2f - px(30),
            height - pxFloat(16),
            width / 2f + px(30),
            height - pxFloat(11),
            pxFloat(10),
            pxFloat(10),
            notchPaint,
        )

        if (isTodayInCurrentMonth) {
            val widthWithoutPadding = width - paddingHorizontal * 2f
            val rawWidth = widthWithoutPadding / 7f
            val maxWidth = pxFloat(42)
            val capsuleWidth = rawWidth.coerceAtMost(maxWidth)
            val capsuleLeftPadding = if (rawWidth >= maxWidth) (rawWidth - maxWidth) / 2f else 0f
            val capsuleHeight = pxFloat(64)
            val capsuleLeft = paddingHorizontal + capsuleLeftPadding + today.dayOfWeekIndex * rawWidth
            val capsuleWidthRadius = capsuleWidth / 2f

            canvas.drawRoundRect(
                capsuleLeft,
                pxFloat(72),
                capsuleLeft + capsuleWidth,
                pxFloat(72) + capsuleHeight,
                capsuleWidthRadius,
                capsuleWidthRadius,
                weekCapsulePaint,
            )
        }
    }


    private var expandVelocityTracker: VelocityTracker? = null
    private var offsetY = 0f

    @SuppressLint("Recycle")
    private fun configureExpandGestureHandling() {
        setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.y <= view.height - px(30)) {
                        return@setOnTouchListener false
                    }

                    expandVelocityTracker?.clear()
                    expandVelocityTracker = expandVelocityTracker ?: VelocityTracker.obtain()
                    expandVelocityTracker?.addMovement(event)

                    offsetY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    expandVelocityTracker?.apply {
                        addMovement(event)
                        computeCurrentVelocity(1000)
                    }

                    animValue = ((event.y - collapsedHeight) / (expandedHeight - collapsedHeight)).clamp(0f, 1.2f)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (expandVelocityTracker!!.yVelocity > 0) expand()
                    else collapse()
                }
            }
            true
        }
    }

    private fun adjustUIsWithAnimValue() {
        adjustHeight()
        adjustWeekTextColors()
        adjustWeekCapsulePaintOpacity()
        adjustViewPagersOpacity()
    }

    private fun adjustHeight() = updateLayoutParams<ViewGroup.LayoutParams> {
        height = MathUtils.lerp(collapsedHeight.toFloat(), expandedHeight.toFloat(), animValue).toInt()
    }

    private fun adjustWeekTextColors() {
        weekTexts.forEachIndexed { idx, textView ->
            textView.setTextColor(
                getWeekTextColor(
                    idx,
                    isTodayInCurrentWeek && today.dayOfWeekIndex == idx
                )
            )
        }
    }

    private fun getWeekTextColor(@IntRange(from = 0L, to = 6L) week: Int, isToday: Boolean = false): Int {
        val weekColor = getColor(CalendarUtil.getColorFromWeek(week))
        if (!isToday) return weekColor

        return ColorUtils.blendARGB(Color.WHITE, weekColor, animValue.clamp(0f, 1f))
    }

    private fun adjustWeekCapsulePaintOpacity() {
        weekCapsulePaint.alpha = (255 - animValue * 255).toInt().clamp(0, 255)
    }

    private fun adjustViewPagersOpacity() {
        weeklyViewPager.alpha = 1 - animValue
        monthlyViewPager?.alpha = animValue
    }

    private fun disableScroll() {
        scrollEnabled.value = false
    }

    private fun enableScroll() {
        scrollEnabled.value = true
    }

    private fun scrollToTop() {
        onScrollToTop.value = Once(Unit)
    }

    private fun enableTouchWeeklyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = true
        monthlyViewPager?.isUserInputEnabled = false
    }

    private fun enableTouchMonthlyPagerOnly() {
        weeklyViewPager.isUserInputEnabled = false
        monthlyViewPager?.isUserInputEnabled = true
    }

    fun interface OnDateChangeListener {
        fun onChange(date: LocalDate)
    }

    companion object {
        private const val parentId = ConstraintSet.PARENT_ID
        private const val MIN_HEIGHT_DP = 220
        private const val EXPAND_MARGIN_BOTTOM_DP = 120
    }
}
```

- 날짜 계산 유틸리티
***LocalDate.kt***
```kotlin
val LocalDate.weekOfMonth: Int
    get() {
        val gc = GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault()))
        gc.firstDayOfWeek = SUNDAY
        gc.minimalDaysInFirstWeek = 1
        return gc[WEEK_OF_MONTH]
    }

val LocalDate.dayOfWeekValue: Int
    get() = when (dayOfWeek.value + 1) {
        8 -> 1
        else -> dayOfWeek.value + 1
    }
val LocalDate.dayOfWeekIndex: Int
    get() = dayOfWeekValue - 1

fun convertMonthlyIndexToDate(index: Int): LocalDate {
    val cur = LocalDate.now()

    val diffMonth = MonthlyAdapter.MAX_ITEM_COUNT - index - 1
    return cur.minusMonths(diffMonth.toLong())
}

fun convertWeeklyIndexToDate(index: Int): LocalDate {
    val cur = LocalDate.now()

    val diffWeek = WeeklyAdapter.MAX_ITEM_COUNT - index - 1

    return cur.minusWeeks(diffWeek.toLong())
}

fun convertDateToMonthlyIndex(date: LocalDate): Int {
    val now = LocalDate.now()

    val yearDiff = now.year - date.year
    val diffIndex = now.monthValue - date.monthValue + yearDiff * 12

    return MonthlyAdapter.MAX_ITEM_COUNT - diffIndex - 1
}

fun convertDateToWeeklyIndex(date: LocalDate): Int {
    val now = LocalDate.now()

    val weekDiff = ChronoUnit.WEEKS.between(date, now).toInt()

    return WeeklyAdapter.MAX_ITEM_COUNT - weekDiff - 1
}


fun calculateRequiredRow(date: LocalDate): Int {
    return (date.lengthOfMonth() + date.withDayOfMonth(1).dayOfWeekIndex - 1) / 7 + 1
}

fun getMonthTexts(date: LocalDate): Triple<List<Int>, Int, Int> {
    val result = MutableList(42) { 0 }

    val previousMonthEndDay = date.minusMonths(1).lengthOfMonth()

    // 1 ~ 7 (MON ~ SUN)
    val startDayIndex = date.withDayOfMonth(1).dayOfWeekIndex
    val endDayIndex = startDayIndex + date.lengthOfMonth() - 1

    for (i in 0 until startDayIndex) {
        result[i] = previousMonthEndDay - (startDayIndex - i) + 1
    }
    for (i in startDayIndex..endDayIndex) {
        result[i] = i - startDayIndex + 1
    }
    for (i in (endDayIndex + 1)..41) {
        result[i] = i - endDayIndex
    }

    return Triple(result, startDayIndex, endDayIndex)
}

fun getWeekTexts(date: LocalDate): List<Int> {
    val (_result) = getMonthTexts(date)

    return _result.subList((date.weekOfMonth - 1) * 7, date.weekOfMonth * 7)
}
```

- 권한 허용 유틸리티
***PermissionUtil.kt***
```kotlin
/**
 * Dexter를 이용한 권한 요청 및 콜백에 관련된 유틸
 *
 * @author MJStudio
 * @see android.Manifest.permission
 */
object PermissionUtil {
    fun requestLocationPermissions(activity: Activity, listener: PermissionListener) {
        requestPermissions(
            activity, listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ), listener
        )
    }

    /**
     * 권한들이 허용되었는지 검사를 요청하는 함수
     *
     * @param activity Dexter 라이브러리가 사용할 Activity 객체
     * @param listener 권한들에 대한 검사가 완료되었을 때 콜백을 처리할 리스너
     * @param permissions 요청하는 권한 목록 [android.Manifest.permission]
     */
    private fun requestPermissions(activity: Activity, permissions: Collection<String>, listener: PermissionListener) {

        val callbackListener: MultiplePermissionsListener = object : BaseMultiplePermissionsListener() {

            override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                val deniedPermissions = report.deniedPermissionResponses.map { it.permissionName }
                val permanentlyDeniedPermissions =
                    report.deniedPermissionResponses.filter { it.isPermanentlyDenied }.map { it.permissionName }

                // 모든 권한이 허가되었다면,
                when {
                    report.areAllPermissionsGranted() -> {
                        listener.onPermissionGranted()
                    }
                    // 권한 중에 영구적으로 거부된 권한이 있다면
                    report.isAnyPermissionPermanentlyDenied -> {
                        listener.onAnyPermissionsPermanentlyDeined(deniedPermissions, permanentlyDeniedPermissions)
                    }
                    // 권한 중에 거부된 권한이 있다면
                    else -> {
                        listener.onPermissionShouldBeGranted(deniedPermissions)
                    }
                }
            }

        }

        /**
         * Dexter로 activity를 이용한 권한 요청
         */
        Dexter.withActivity(activity).withPermissions(permissions).withListener(callbackListener).check()
    }


    fun openPermissionSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        })
    }

    interface PermissionListener {
        /**
         * 모든 권한이 허용되었다.
         */
        fun onPermissionGranted() {}

        /**
         * 일부 권한이 거부되었다.
         *
         * @param deniedPermissions 거부된 권한 목록
         */
        fun onPermissionShouldBeGranted(deniedPermissions: List<String>) {}

        /**
         * 일부 권한이 영구적으로 거부되었다.
         *
         * @param deniedPermissions 거부된 권한 목록
         * @param permanentDeniedPermissions 영구적으로 거부된 권한 목록
         */
        fun onAnyPermissionsPermanentlyDeined(
            deniedPermissions: List<String>, permanentDeniedPermissions: List<String>
        ) {
        }
    }
}
```

- StatusBar 상태 조절 유틸리티
***StatusBarUtil.kt***
```kotlin
object StatusBarUtil {
    fun collapseStatusBar(activity: Activity) {
        activity.window?.run {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }


        fun expandStatusBar(activity: Activity) {
            activity.window?.run {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = Color.WHITE
            }
        }
    }
}
```

