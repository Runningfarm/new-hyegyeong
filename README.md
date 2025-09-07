# 9/7 수정사항
### 1km 러닝 인증 사진 퀘스트 활성화 수정   

- 오류 수정 코드는 09.04에 주원님이 톡방에 올려주신 전체 프로젝트 파일 기반으로 수정한 점 참고 부탁드립니다.  
추가적으로 필요한 수정 사항이 있다면 연락주세요!

- 수정 코드를 제외한 이전 수정 사항은 hyegyeong 레포에 업로드 되어 있습니다.   

<details>
  <summary>AndroidManifest.xml</summary>
  
  ##
- <activity android:name=".EditProfileActivity" android:exported="true" 아래에
```
<activity android:name=".PhotoPreviewActivity" android:exported="true" />
```
액티비티 추가
</details>
<details>
  <summary>build.gradle.kts(:app)</summary>

  ##
  - dependencies {} 안에
  ```
// ML Kit 이미지 라벨링
    implementation("com.google.mlkit:image-labeling:17.0.9")
```
추가
  
</details>
<details>
  <summary>Tab3Activity.java</summary>

  ##
  - public class Tab3Activity extends AppCompatActivity {}안에서
```
private Button btnClaimP1, btnClaimP2;
```
를
```
private Button btnQuestP1, btnQuestP2;
```
로 변경
##
- protected void onCreate()내부   
boxRewardP2 = findViewById(R.id.boxRewardP2); 아래에
```
btnClaimP1 = findViewById(R.id.btnClaimP1);
        btnClaimP2 = findViewById(R.id.btnClaimP2);
```
를
```
btnQuestP1 = findViewById(R.id.btnQuestP1);
        btnQuestP2 = findViewById(R.id.btnQuestP2);
```
로 변경   

- double lastRunDistance = getIntent().getDoubleExtra("lastRunDistance", 0.0);
setupActivityResultLaunchers 아래 부분

```
// 전달받은 러닝 거리를 1km 조건 체크에 활용
        btnClaimP1.setEnabled(lastRunDistance >= 1.0);
        btnClaimP2.setEnabled(true); // 항상 가능

        // p1번 버튼 클릭 → 권한 → 촬영 → 미리보기
        btnClaimP1.setOnClickListener(v -> {
            currentPhotoQuestNumber = 101;
            if (lastRunDistance < 1.0) {
                Toast.makeText(this, "1km 이상 러닝 시 활성화됩니다.", Toast.LENGTH_LONG).show();
                return;
            }
            ensureCameraPermissionThenCapture();
        });

        //p2번 버튼 클릭 → 권한 → 촬영 → 미리보기
        btnClaimP2.setOnClickListener(v -> {
            currentPhotoQuestNumber = 102;
            ensureCameraPermissionThenCapture();
        });
```
를
```
btnQuestP1.setEnabled(true);
        btnQuestP2.setEnabled(true);

        btnQuestP1.setOnClickListener(v -> {
            if (lastRunDistance >= 1.0) {
                currentPhotoQuestNumber = 101;
                ensureCameraPermissionThenCapture();
            } else {
                Toast.makeText(this, "1km 이상 러닝 시 촬영 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnQuestP2.setOnClickListener(v -> {
            currentPhotoQuestNumber = 102;
            ensureCameraPermissionThenCapture();
        });
```
로 변경
##
```
    //보상 성공 시 UI 업데이트 (신규)
    private void handleCameraQuestRewardUI(int questNumber) {
        try {
            int boxId;
            int progressId;
            int btnId;

            // P1(24), P2(25) 매핑
            if (questNumber == 101) {
                boxId = R.id.boxRewardP1;
                progressId = R.id.progressQuestP1;
                btnId = R.id.btnClaimP1;
            } else {
                boxId = R.id.boxRewardP2;
                progressId = R.id.progressQuestP2;
                btnId = R.id.btnClaimP2;
            }

            ImageView box = findViewById(boxId);
            if (box != null) {
                box.setImageResource(R.drawable.box_opened);
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_open);
                box.startAnimation(fadeIn);
            }

            ProgressBar pb = findViewById(progressId);
            if (pb != null) pb.setProgress(100);

            Button btn = findViewById(btnId);
            if (btn != null) {
                btn.setEnabled(false);
                btn.setText("완료");
            }

            Toast.makeText(this, "퀘스트 " + questNumber + " 보상을 받았습니다!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("CameraQuestUI", "UI update failed: " + e.getMessage());
        }
    }
```
를
```
private void handleCameraQuestRewardUI(int questNumber) {
        try {
            int boxId,progressId,btnId;

            // P1(24), P2(25) 매핑
            if (questNumber == 101) {
                boxId = R.id.boxRewardP1;
                progressId = R.id.progressQuestP1;
                btnId = R.id.btnQuestP1;
            } else {
                boxId = R.id.boxRewardP2;
                progressId = R.id.progressQuestP2;
                btnId = R.id.btnQuestP2;
            }

            ImageView box = findViewById(boxId);
            if (box != null) {
                box.setImageResource(R.drawable.box_opened);
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_open);
                box.startAnimation(fadeIn);
            }

            ProgressBar pb = findViewById(progressId);
            if (pb != null) pb.setProgress(100);

            Button btn = findViewById(btnId);
            if (btn != n가
```
<!-- 하단 탭바 -->
    <LinearLayout
        android:id="@+id/bottomBar"
        android:layout_width="match_parent"
        android:layout_height="70dp"
        android:layout_alignParentBottom="true"
        android:orientation="horizontal"
        android:background="#FFFFFF"
        android:layout_margin="8dp"
        android:padding="6dp"
        android:elevation="10dp"
        android:clipToPadding="false"
        android:backgroundTint="#FFFFFF"
        android:backgroundTintMode="src_in"
        android:weightSum="5"
        android:gravity="center">

        <ImageButton
            android:id="@+id/tab1Button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:background="@null"
            android:contentDescription="탭1"
            android:src="@drawable/ic_home"/>

        <Space
            android:layout_width="16dp"
            android:layout_height="wrap_content" />

        <ImageButton
            android:id="@+id/tab2Button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:src="@drawable/ic_running"
            android:background="@drawable/bottom_tab_selector"
            android:contentDescription="탭 2" />

        <Space
            android:layout_width="16dp"
            android:layout_height="wrap_content" />

        <ImageButton
            android:id="@+id/tab3Button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:background="@null"
            android:contentDescription="탭3"
            android:src="@drawable/ic_quest" />

        <Space
            android:layout_width="16dp"
            android:layout_height="wrap_content" />

        <ImageButton
            android:id="@+id/tab4Button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:background="@null"
            android:contentDescription="탭4"
            android:src="@drawable/ic_inventory" />

        <Space
            android:layout_width="16dp"
            android:layout_height="wrap_content" />

        <ImageButton
            android:id="@+id/tab6Button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:background="@null"
            android:contentDescription="탭6"
            android:src="@drawable/ic_mypage" />
    </LinearLayout>
```
</details>
