package com.example.buynest.views.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.R
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.repository.categoryrepo.CategoryRepoImpl
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.categoryViewModel.CategoryFactory
import com.example.buynest.viewmodel.categoryViewModel.CategoryViewModel
import com.example.buynest.views.component.CategoryItem
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.SideNavigation

@Composable
fun CategoriesScreen(onCartClicked:()->Unit,onProductClicked:()->Unit) {
    var selectedCategory by remember { mutableStateOf<String?>("Kid") }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }
    val phenomenaBold = FontFamily(
        Font(R.font.phenomena_bold)
    )

    val categoryViewModel : CategoryViewModel = viewModel (
        factory = CategoryFactory(CategoryRepoImpl())
    )
    val categoryProduct by categoryViewModel.categoryProducts.collectAsStateWithLifecycle()
    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            categoryViewModel.getCategoryProducts(selectedCategory!!)
        }
    }

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
                    selectedSubcategory = null
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
                when (val result = categoryProduct) {
                    is ResponseState.Error ->{
                        Text(text = result.message)
                    }
                    ResponseState.Loading -> {
                        Indicator()
                    }
                    is ResponseState.Success<*> -> {
                        val data = result.data as ProductsByHandleQuery.Data
                        val edges = data.collectionByHandle?.products?.edges
                        val filteredEdges = if (selectedSubcategory != null) {
                            edges?.filter { it.node.productType.contains(selectedSubcategory!!, ignoreCase = true) }
                        } else {
                            edges
                        }
                        CategoryProducts(onProductClicked, filteredEdges)
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryProducts(
    onProductClicked: () -> Unit,
    categoryProductList: List<ProductsByHandleQuery.Edge>?
) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        items(categoryProductList?.size?:0){item ->
            CategoryItem(
                product = categoryProductList!![item].node
            )
        }
    }
}



