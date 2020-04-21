package com.noto.domain

import com.noto.R

enum class NotoIcon(val resId: Int) {
    NOTEBOOK(R.drawable.ic_notebook_24dp),
    LIST(R.drawable.ic_list_24dp),
    FITNESS(R.drawable.ic_fitness_24dp),
    HOME(R.drawable.ic_home_24dp),
    BOOK(R.drawable.ic_book_24dp),
    SCHOOL(R.drawable.ic_school_24dp),
    WORK(R.drawable.ic_work_24dp),
    LAPTOP(R.drawable.ic_laptop_24dp),
    GROCERY(R.drawable.ic_grocery_24dp),
    SHOP(R.drawable.ic_shop_24dp),
    GAME(R.drawable.ic_game_24dp),
    TRAVEL(R.drawable.ic_travel_24dp),
    MUSIC(R.drawable.ic_music_24dp),
    IDEA(R.drawable.ic_idea_24dp),
    WRENCH(R.drawable.ic_wrench_24dp),
    CHART(R.drawable.ic_chart_24dp),
    CALENDAR(R.drawable.ic_calendar_24dp),
    CODE(R.drawable.ic_code_24dp)
}

enum class NotoColor(val resId: Int) {
    GRAY(R.color.colorPrimaryGray),
    BLUE(R.color.colorPrimaryBlue),
    PINK(R.color.colorPrimaryPink),
    CYAN(R.color.colorPrimaryCyan)
}

enum class SortMethod {
    Alphabetically,
    CreationDate,
    Checked,
    Starred,
    Custom
}

enum class BlockType {
    NOTE,
    BULLET,
    NUMBER
}

enum class SortType {
    ASC,
    DESC
}