package com.example.buynest.views.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.R
import com.example.buynest.repository.favoriteRepo.FavoriteRepoImpl
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.views.brandProducts.ProductGrid
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.SideNavigation

@Composable
fun CategoriesScreen(onCartClicked:()->Unit,onProductClicked:()->Unit) {
    var selectedCategory by remember { mutableStateOf<String?>("Kids") }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }
    val phenomenaBold = FontFamily(
        Font(R.font.phenomena_bold)
    )
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModel.FavouritesFactory(FavoriteRepoImpl())
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            onCartClicked = onCartClicked
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
                ProductGrid("Categories", onProductClicked,favViewModel= favViewModel)
            }
        }
    }
}






