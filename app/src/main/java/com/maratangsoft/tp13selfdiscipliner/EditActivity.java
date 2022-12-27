package com.maratangsoft.tp13selfdiscipliner;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CalendarView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.maratangsoft.tp13selfdiscipliner.databinding.ActivityEditBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    int category;
    String date;
    String[] categoryTitles = {"전 체", "업 무", "공 부", "운 동", "취 미", "약 속", "기 타", "끝난 것"};

    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("할 일 추가");

        category = getIntent().getIntExtra("category", 0);
        binding.tvCategory.setText(categoryTitles[category]);
        date = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());
        binding.tvCalendar.setText(date);

        binding.tvCalendar.setOnClickListener(view -> showBottomSheetDialogCalendar());
        binding.tvCategory.setOnClickListener(view -> showBottomSheetDialogCategory());
        binding.btnSubmit.setOnClickListener(view -> clickSubmit());
    }

    void showBottomSheetDialogCalendar(){
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bsd_calendar);
        bottomSheetDialog.show();

        CalendarView calendarView = bottomSheetDialog.findViewById(R.id.bsd_calendar);
        calendarView.setOnDateChangeListener((view, year, month, day) -> {

            //달력에서 선택한 날짜로 태양력 Calendar 객체 생성
            GregorianCalendar calendar = new GregorianCalendar(year, month, day);
            date = new SimpleDateFormat("yyyy년 MM월 dd일").format(calendar.getTime());
            binding.tvCalendar.setText(date);
            bottomSheetDialog.dismiss();
        });
    }

    void showBottomSheetDialogCategory(){
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bsd_category);
        bottomSheetDialog.show();

        bottomSheetDialog.findViewById(R.id.bsd_category_work).setOnClickListener(view -> clickCategory(1));
        bottomSheetDialog.findViewById(R.id.bsd_category_study).setOnClickListener(view -> clickCategory(2));
        bottomSheetDialog.findViewById(R.id.bsd_category_health).setOnClickListener(view -> clickCategory(3));
        bottomSheetDialog.findViewById(R.id.bsd_category_hobby).setOnClickListener(view -> clickCategory(4));
        bottomSheetDialog.findViewById(R.id.bsd_category_meeting).setOnClickListener(view -> clickCategory(5));
        bottomSheetDialog.findViewById(R.id.bsd_category_etc).setOnClickListener(view -> clickCategory(6));
    }

    void clickCategory(int category){
        this.category = category;
        binding.tvCategory.setText(categoryTitles[category]);
        bottomSheetDialog.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    void clickSubmit(){
        SQLiteDatabase db = openOrCreateDatabase("TodoDB", MODE_PRIVATE, null);

        //저장할 데이터들 (title, date(멤버변수), category(멤버변수), note)
        String title = binding.etTitle.getText().toString();
        String note = binding.etNote.getText().toString();
        //테이블에 데이터를 삽입하는 SQL쿼리문 작성
        db.execSQL("INSERT INTO todo(title,date,category,note,isDone) VALUES(?,?,?,?,?)", new Object[]{title, date, category, note, 0});

        //저장완료 후 액티비티 종료
        onBackPressed();
    }
}