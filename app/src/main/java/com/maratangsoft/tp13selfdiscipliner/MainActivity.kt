package com.maratangsoft.tp13selfdiscipliner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import com.bumptech.glide.Glide
import com.maratangsoft.tp13selfdiscipliner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val CATEGORY_ALL = 0
        const val CATEGORY_WORK = 1
        const val CATEGORY_STUDY = 2
        const val CATEGORY_HEALTH = 3
        const val CATEGORY_HOBBY = 4
        const val CATEGORY_MEETING = 5
        const val CATEGORY_ETC = 6
        const val CATEGORY_DONE = 7
    }

    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.includeCategoryAll.root.setOnClickListener { clickedCategory(CATEGORY_ALL) }
        binding.includeCategoryWork.root.setOnClickListener { clickedCategory(CATEGORY_WORK) }
        binding.includeCategoryStudy.root.setOnClickListener { clickedCategory(CATEGORY_STUDY) }
        binding.includeCategoryHealth.root.setOnClickListener { clickedCategory(CATEGORY_HEALTH) }
        binding.includeCategoryHobby.root.setOnClickListener { clickedCategory(CATEGORY_HOBBY) }
        binding.includeCategoryMeeting.root.setOnClickListener { clickedCategory(CATEGORY_MEETING) }
        binding.includeCategoryEtc.root.setOnClickListener { clickedCategory(CATEGORY_ETC) }
        binding.includeCategoryDone.root.setOnClickListener { clickedCategory(CATEGORY_DONE) }

        loadData() //SharedPreferences에 저장된 프로필이미지, 이름을 가져와서 뷰에 보여주는 기능 호출
    }

    fun clickedCategory(category: Int) {
        val intent = Intent(this, TodoActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    fun loadData() {
        val pref = getSharedPreferences("account", MODE_PRIVATE)
        val profileImage = pref.getString("profile_image", "")
        val name = pref.getString("name", "")
        binding.tvName.text = "안녕하세요 " + name + "님"
        Glide.with(this).load(profileImage).error(R.mipmap.profile_default)
            .into(binding.civProfile)
    }

    override fun onResume() {
        super.onResume()

        loadDatabaseAndUiUpdate()
    }
    //Database "TodoDB.db" 파일 안에 있는 todo 테이블의 카테고리별 개수 가져오기
    private fun loadDatabaseAndUiUpdate(){
        val db: SQLiteDatabase = openOrCreateDatabase("TodoDB", MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS todo(num INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, date TEXT, category INTEGER, note TEXT, isDone INTEGER)")

        var countAll: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0", null)
        var countWork: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("1"))
        var countStudy: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("2"))
        var countHealth: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("3"))
        var countHobby: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("4"))
        var countMeeting: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("5"))
        var countEtc: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=0 AND category=?", arrayOf("6"))
        var countDone: Long = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM todo WHERE isDone=1", null)

        binding.includeCategoryAll.tvNum.text = countAll.toString()
        binding.includeCategoryWork.tvNum.text = countWork.toString()
        binding.includeCategoryStudy.tvNum.text = countStudy.toString()
        binding.includeCategoryHealth.tvNum.text = countHealth.toString()
        binding.includeCategoryHobby.tvNum.text = countHobby.toString()
        binding.includeCategoryMeeting.tvNum.text = countMeeting.toString()
        binding.includeCategoryEtc.tvNum.text = countEtc.toString()
        binding.includeCategoryDone.tvNum.text = countDone.toString()
    }
}