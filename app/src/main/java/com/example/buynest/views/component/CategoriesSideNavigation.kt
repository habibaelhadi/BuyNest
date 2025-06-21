package com.example.buynest.views.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.ui.theme.LightGray
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.viewmodel.shared.SharedViewModel
import androidx.compose.runtime.getValue

@Composable
fun SideNavigation(
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    onSubcategorySelected: (String) -> Unit,
    selectedSubcategory: String?,
    sharedViewModel: SharedViewModel,

    ) {
    val categories by sharedViewModel.category.collectAsStateWithLifecycle()

    val subcategories = listOf("Accessories", "T-Shirts", "Shoes")

    LazyColumn(
        modifier = Modifier
            .width(120.dp)
            .fillMaxHeight()
            .background(
                color = LightGray,
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            )
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.title == selectedItem

            Column(
                modifier = Modifier
                    .background(
                        color = if (isSelected) white else LightGray,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(category.title) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(32.dp)
                            .background(
                                color = if (isSelected) MainColor else Color.Transparent,
                                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = category.title,
                        color = MainColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }

                // Subcategories
                if (isSelected) {
                    subcategories.forEach { sub ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSubcategorySelected(sub) }
                                .padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Canvas(modifier = Modifier.size(6.dp)) {
                                drawCircle(color = Color.DarkGray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sub,
                                color = if (selectedSubcategory == sub) MainColor else Color.DarkGray,
                                fontSize = 14.sp,
                                fontWeight = if (selectedSubcategory == sub) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
