package com.example.buynest.views.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor

@Composable
fun MapSearchBar(
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
) {
    val backgroundColor = MainColor
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color.White,
                MaterialTheme.shapes.medium
            )
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 12.dp)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(4.dp))

            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = searchQuery,
                    textStyle = TextStyle(color = textColor),
                    onValueChange = onQueryChange,
                    singleLine = false,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchQuery.text.isEmpty()) {
                                Text(
                                    text = "Search location",
                                    color = Color.White
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            if (searchQuery.text.isNotEmpty()) {
                IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}
