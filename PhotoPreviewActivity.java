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
