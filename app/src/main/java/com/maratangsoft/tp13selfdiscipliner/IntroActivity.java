package com.maratangsoft.tp13selfdiscipliner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maratangsoft.tp13selfdiscipliner.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;
    
    //프로필 이미지 경로와 이름 데이터 변수, NPE 막기 위해 빈 문자열 삽입
    String profileImage = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadData();

        binding.civProfile.setOnClickListener(view -> clickCivProfile());
        binding.btnStart.setOnClickListener(view -> clickBtnStart());
    }

    //프로필사진 클릭시 갤러리앱 or 사진앱을 실행
    void clickCivProfile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }
    
    //시작하기 버튼을 누르면 EditText에 적은 이름 저장하고 메인액티비티로
    void clickBtnStart(){
        saveData();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_CANCELED){
            Intent intent = result.getData();
            Uri uri = intent.getData();
            Glide.with(IntroActivity.this).load(uri).error(R.mipmap.profile_default).into(binding.civProfile);
            profileImage = uri.toString();
        }
    });
    
    //SharedPreference에 프로필이미지와 이름 저장
    void saveData(){
        name = binding.etName.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();

        editor.putString("profile_image", profileImage);
        editor.putString("name", name);
        editor.commit();
        Toast.makeText(this, "프로필 저장 완료", Toast.LENGTH_SHORT).show();
    }

    void loadData(){
        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
        profileImage = pref.getString("profile_image", "");
        name = pref.getString("name", "익명");

        Glide.with(this).load(profileImage).error(R.mipmap.profile_default).into(binding.civProfile);
        binding.etName.setText(name);
    }
}