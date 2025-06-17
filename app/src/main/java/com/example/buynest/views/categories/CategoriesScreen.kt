package com.example.buynest.views.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.*
import com.example.buynest.views.brandProducts.ProductGrid
import com.example.buynest.views.home.SearchBar

@Composable
fun CategoriesScreen(onCardClicked:()->Unit) {
    var selectedCategory by remember { mutableStateOf<String?>("Kids") }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }
    val phenomenaBold = FontFamily(
        Font(R.font.phenomena_bold)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text("BuyNest", fontSize = 22.sp,
            fontFamily = phenomenaBold,
            color = MainColor)
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar(
            onCardClicked = onCardClicked
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            SideNavigation(
                selectedItem = selectedCategory,
                onItemSelected = { clicked ->
                    selectedCategory = if (selectedCategory == clicked) null else clicked
                    selectedSubcategory = null // reset subcategory on main category change
                },
                onSubcategorySelected = { sub ->
                    selectedSubcategory = sub
                },
                selectedSubcategory = selectedSubcategory
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "   ${selectedCategory ?: ""}${selectedSubcategory?.let { " : $it" } ?: ""}",
                    fontSize = 20.sp,
                    fontFamily = phenomenaBold,
                    color = MainColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProductGrid("Categories")
            }
        }
    }
}




@Composable
fun SideNavigation(
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    onSubcategorySelected: (String) -> Unit,
    selectedSubcategory: String?
) {
    val categories = listOf("Kids", "Women", "Men", "Home")
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
            val isSelected = category == selectedItem

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
                        .clickable { onItemSelected(category) }
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
                        text = category,
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
                                color = if (selectedSubcategory == sub) Color(0xFF002366) else Color.DarkGray,
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



@Composable
fun  CategoryItem(){
    Card (
        modifier = Modifier
            .width(200.dp)
            .padding(4.dp),
        border = BorderStroke(1.dp, MainColor.copy(0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(
            modifier = Modifier.background(white)
                .height(175.dp)
        ){
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()

            ){
                Image(
                    painter = painterResource(id = R.drawable.product),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    contentScale = ContentScale.FillHeight
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp)
                        .align(Alignment.TopEnd)
                ){
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(white, shape = CircleShape)
                            .size(32.dp)
                            .padding(8.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nike Air Jordon",
                style = MaterialTheme.typography.titleMedium,
                color = MainColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


