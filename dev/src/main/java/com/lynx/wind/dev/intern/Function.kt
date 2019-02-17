package com.lynx.wind.dev.intern

import android.content.Context
import android.util.TypedValue


internal fun Float.toDp(context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this,
    context.resources.displayMetrics)