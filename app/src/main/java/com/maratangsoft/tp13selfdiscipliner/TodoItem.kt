package com.maratangsoft.tp13selfdiscipliner

data class TodoItem(
    val num:Int,
    var title:String,
    var date:String,
    var category:Int,
    var note:String,
    var isDone:Int
    )