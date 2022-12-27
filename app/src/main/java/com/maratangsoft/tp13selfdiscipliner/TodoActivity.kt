package com.maratangsoft.tp13selfdiscipliner

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.maratangsoft.tp13selfdiscipliner.databinding.ActivityTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoActivity : AppCompatActivity() {
    val binding by lazy { ActivityTodoBinding.inflate(layoutInflater) }
    var position:Int = -1
    var category: Int = 0
    var categoryTitles: Array<String> = arrayOf("전체", "업무", "공부", "운동", "취미", "약속", "기타", "끝난 것")
    val slogans: Array<String> = arrayOf(
        "다람쥐 헌 쳇바퀴에 타고파.",
        "앗살람 알라이쿰.",
        "인간의 욕심은 끝이 없고 같은 실수를 반복한다.",
        "All work and no play makes Jack a dull boy.",
        "내가 조선의 국모다.",
        "여러분 이거 다 거짓말인 거 아시죠?",
        "비내리는 호남선 남행열차에",
        "a long time ago in a galaxy far, far away..."
    )
    //할일 데이터를 저장하는 리스트
    var todoItems: MutableList<TodoItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        category = intent.getIntExtra("category", 0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //카테고리 종류에 따라 제목글씨 표시
        supportActionBar?.title = categoryTitles[category]
        //카테고리 종류에 따라 슬로건 글씨 표시
        binding.tvSlogan.text = slogans[category]
        //오늘의 날짜 표시
        binding.tvToday.text = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())

        //새로운 할일 작성하는 액티비티로 전환
        binding.fabAddTodo.setOnClickListener { view: View? ->
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }
        //리사이클러뷰에 어댑터 생성하여 연결하기
        binding.recyclerview.adapter = TodoRecyclerAdapter(this@TodoActivity, todoItems)

        //아이템의 완료버튼 클릭 이벤트 처리
        binding.bsBtnDone.setOnClickListener {clickDone()}
    }

    //제목줄의 뒤로가기 버튼 (Up 버튼) 클릭시 자동 실행되는 콜백 메소드
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //액티비티가 화면에 보여질 때 자동으로 실행되는 라이프사이클 메소드
    override fun onResume() {
        super.onResume()
        //SQLite DB에 저장된 할일 데이터 읽어오는 기능메소드 호출
        loadDB()
    }

    private fun loadDB(){
        //값 중복 로드 방지를 위해 기존 데이터 지우기
        todoItems.clear()
        binding.recyclerview.adapter?.notifyDataSetChanged()

        //SQLite DB에서 할일 데이터 읽어오기
        val db:SQLiteDatabase = openOrCreateDatabase("TodoDB", MODE_PRIVATE, null)

        //category 선택에 따라 해당 목록만 가져오도록 쿼리문 사용
        val cursor = when (category) {
            0 -> db.rawQuery("SELECT * FROM todo WHERE isDone=0", null) //ALL 카테고리
            7 -> db.rawQuery("SELECT * FROM todo WHERE isDone=1", null) //DONE 카테고리
            else -> db.rawQuery("SELECT * FROM todo WHERE isDone=0 AND category=?", arrayOf(category.toString())) //기타 카테고리들
        }
        while (cursor.moveToNext()){
            val num:Int = cursor.getInt(0)
            val title:String = cursor.getString(1)
            val date:String = cursor.getString(2)
            val category:Int = cursor.getInt(3)
            val note:String = cursor.getString(4)
            val isDone:Int = cursor.getInt(5)

            todoItems.add(TodoItem(num, title, date, category, note, isDone))
        }
        cursor.close()
        binding.recyclerview.adapter?.notifyDataSetChanged()
    }

    //클릭된 아이템의 정보를 보여주는 BottomSheet를 보여주는 기능메소드 호출
    fun showBottomSheet(position:Int){
        //멤버변수 포지션에 전달받은 포지션 넣기
        this.position = position
        //BottomSheet의 상태(expanded/Collapsed)에 따라 내리고 올리는 동작 수행
        //올리기 직전에 값 업데이트 작업 수행
        if (BottomSheetBehavior.from(binding.bs).state == BottomSheetBehavior.STATE_COLLAPSED){
            updateBottomSheetData(position)
            BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_EXPANDED
            
        }else{
            BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.from(binding.bs).addBottomSheetCallback(object : BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    updateBottomSheetData(position)
                    BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.from(binding.bs).removeBottomSheetCallback(this)
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
    }
    
    fun updateBottomSheetData(position:Int){
        binding.tvBsTitle.text = todoItems[position].title
        binding.tvBsDate.text = todoItems[position].date
        binding.tvBsCategory.text = categoryTitles[todoItems[position].category]
        binding.etBsNote.setText(todoItems[position].note) //edittext의 text는 String이 아니고 Editable 타입이라 그냥 문자열 넣을 수 없음. 그때는 자바처럼 setText() 쓰면 됨

        //완료된 일정이라면 완료 버튼 보이지 않게 하기
        if(category == 7) binding.bsBtnDone.visibility = View.INVISIBLE
    }

    private fun clickDone(){
        val num:Int = todoItems[position].num
        val db:SQLiteDatabase = openOrCreateDatabase("TodoDB", MODE_PRIVATE, null)
        db.execSQL("UPDATE todo SET isDone=1 WHERE num=?", arrayOf(num.toString()))

        todoItems.removeAt(position)
        binding.recyclerview.adapter?.notifyItemRemoved(position)
        BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_COLLAPSED
    }
}