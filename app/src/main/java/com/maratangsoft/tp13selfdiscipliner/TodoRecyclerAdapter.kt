package com.maratangsoft.tp13selfdiscipliner

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maratangsoft.tp13selfdiscipliner.databinding.ItemRecyclerBinding

class TodoRecyclerAdapter(val context:Context, var todoItems:MutableList<TodoItem>): Adapter<TodoRecyclerAdapter.VH>() {

    val categoryIcons: Array<Int> = arrayOf(
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_work,
        R.drawable.ic_study,
        R.drawable.ic_health,
        R.drawable.ic_hobby,
        R.drawable.ic_meeting,
        R.drawable.ic_etc
    )

    inner class VH(val binding:ItemRecyclerBinding): ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                //TodoActivity의 BottomSheet를 보여주는 기능메소드 호출
                (context as TodoActivity).showBottomSheet(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return VH(ItemRecyclerBinding.bind(itemView))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.todoItemTvTitle.text = todoItems[position].title
        holder.binding.todoItemTvDate.text = todoItems[position].date
        holder.binding.todoItemIvCategory.setImageResource(categoryIcons[todoItems[position].category])
    }
    //코틀린의 함수 표기문법 중 return 값을 간략히 쓰기 위한 함수 단순화 문법
    override fun getItemCount() = todoItems.size
}