# 0907
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
            if (btn != null) {
                btn.setEnabled(false);
                btn.setText("완료");
            }

            Toast.makeText(this, "카메라 퀘스트 보상을 받았습니다!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("CameraQuestUI", "UI update failed: " + e.getMessage());
        }
    }
```
로 변경
</details>

<details>
  <summary>PhotoPreviewActivity.java</summary>

  ##
  - 전체 수정
  
  ```
  package kr.ac.hs.farm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

public class PhotoPreviewActivity extends AppCompatActivity {

    private ImageView previewImageView;
    private Button btnClaimReward;
    private Uri photoUri;
    private int questNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);

        // XML 뷰 초기화
        previewImageView = findViewById(R.id.previewImageView);
        btnClaimReward = findViewById(R.id.btnClaimReward);

        // Intent에서 전달된 사진과 퀘스트 번호 받기
        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("photoUri");
        questNumber = intent.getIntExtra("questNumber", -1);

        // 사진 미리보기 표시
        if (photoUri != null) {
            previewImageView.setImageURI(photoUri);

            if (questNumber == 101) {
                // ★ CHANGED: p1번은 "촬영만 하면" 보상 가능
                btnClaimReward.setEnabled(true);

            } else if (questNumber == 102) {
                // ★ CHANGED: p2번은 ML Kit 라벨링 후 식물/나무일 때 활성화
                btnClaimReward.setEnabled(false);
                analyzePhotoForQuest102(photoUri);
            }
        } else {
            Toast.makeText(this, "사진 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 보상 버튼 클릭 이벤트
        btnClaimReward.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("questNumber", questNumber);
            resultIntent.putExtra("rewardResult", "success");
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        findViewById(R.id.tab1Button).setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.tab2Button).setOnClickListener(view -> startActivity(new Intent(this, Tab2Activity.class)));
        findViewById(R.id.tab3Button).setOnClickListener(view -> startActivity(new Intent(this, Tab3Activity.class)));
        findViewById(R.id.tab4Button).setOnClickListener(view -> startActivity(new Intent(this, Tab4Activity.class)));
        findViewById(R.id.tab6Button).setOnClickListener(view -> startActivity(new Intent(this, Tab6Activity.class)));
    }

    // P2 (102번) 퀘스트: ML Kit으로 식물 판별
    private void analyzePhotoForQuest102(Uri uri) {
        try {
            InputImage image = InputImage.fromFilePath(this, uri);

            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

            labeler.process(image)
                    .addOnSuccessListener(labels -> {
                        boolean foundPlant = false;
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            if (text.equalsIgnoreCase("plant") || text.equalsIgnoreCase("flower") || text.equalsIgnoreCase("tree")
                                    || text.equalsIgnoreCase("leaf")) {
                                foundPlant = true;
                                break;
                            }
                        }

                        if (foundPlant) {
                            btnClaimReward.setEnabled(true);
                            Toast.makeText(this, "식물이 감지되었습니다! 보상을 받아주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "식물이 감지되지 않았습니다. 다시 촬영해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "이미지 분석 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "분석 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
```
</details>

<details>
<summary> activity_tab3.xml </summary>
 
## 카메라 퀘스트 영역

  ```
<Button
                            android:id="@+id/btnClaimP1"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginStart="12dp"
                            android:backgroundTint="#FFF7D1"
                            android:elevation="2dp"
                            android:enabled="false"
                            android:text="사진찍기"
                            android:textColor="#5D7755"
                            android:textStyle="bold" />
```
에서
```
 android:id="@+id/btnQuestP1"
```
로 android:id 변경
```
android:enabled="false"
```
삭제

```
<Button
                            android:id="@+id/btnClaimP2"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginStart="12dp"
                            android:backgroundTint="#FFF7D1"
                            android:elevation="2dp"
                            android:enabled="false"
                            android:text="사진찍기"
                            android:textColor="#5D7755"
                            android:textStyle="bold" />
```
에서
```
android:id="@+id/btnQuestP2"
```
로 android:id 변경
```
android:enabled="false"
```
삭제
</details>

<details>
  <summary>activity_photopreview.xml</summary>

##
  ```
android:padding="16dp"
```
삭제
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
<Button/> 아래에 하단 탭바 추가
</details>
