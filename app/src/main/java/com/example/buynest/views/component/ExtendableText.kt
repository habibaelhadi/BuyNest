package com.example.buynest.views.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int = 3
) {
    var isExpanded by remember { mutableStateOf(false) }

        val annotatedText = buildAnnotatedString {
            append(text)
            if (!isExpanded && text.length > 100) {
                append("... ")
                withStyle(SpanStyle(color = Color.Blue)) {
                    append("Read More")
                }
            }
        }


    ClickableText(
        text = annotatedText,
        maxLines = if (isExpanded) Int.MAX_VALUE else minimizedMaxLines,
        overflow = TextOverflow.Ellipsis,
        onClick = {
            if (!isExpanded) isExpanded = true
        },
        style = TextStyle(
            fontSize = 16.sp,
            color = Color.Black
        )
    )
}
