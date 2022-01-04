package food.map.utils

import android.app.Activity
import food.map.R

fun Activity.slideLeft() {
    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
}

fun Activity.slideRight() {
    overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
}