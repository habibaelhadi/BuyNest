package com.example.buynest.di

import android.content.Context
import com.example.buynest.repository.address.AddressRepository
import com.example.buynest.repository.authentication.AuthenticationRepo
import com.example.buynest.repository.authentication.firebase.FirebaseRepository
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepository
import com.example.buynest.repository.cart.CartRepository
import com.example.buynest.repository.category.ICategoryRepo
import com.example.buynest.repository.currency.ICurrencyRepository
import com.example.buynest.repository.discount.DiscountRepository
import com.example.buynest.repository.favorite.FavoriteRepo
import com.example.buynest.repository.home.IHomeRepository
import com.example.buynest.repository.order.IOrderRepo
import com.example.buynest.repository.payment.IPaymentRepository
import com.example.buynest.repository.productDetails.ProductDetailsRepository
import com.example.buynest.viewmodel.address.AddressViewModel
import com.example.buynest.viewmodel.authentication.AuthenticationViewModel
import com.example.buynest.viewmodel.brandproducts.BrandDetailsViewModel
import com.example.buynest.viewmodel.cart.CartUseCase
import com.example.buynest.viewmodel.cart.CartViewModel
import com.example.buynest.viewmodel.categoryViewModel.CategoryViewModel
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.discount.DiscountViewModel
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.viewmodel.home.HomeViewModel
import com.example.buynest.viewmodel.orders.OrdersViewModel
import com.example.buynest.viewmodel.payment.PaymentViewModel
import com.example.buynest.viewmodel.productInfo.ProductDetailsViewModel
import com.example.buynest.viewmodel.shared.SharedViewModel
import com.example.buynest.viewmodel.sreachMap.SearchViewModel
import io.mockk.mockk
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val TestModule = module {

    // Mocked repositories
    single<AddressRepository> { mockk(relaxed = true) }
    single<FirebaseRepository> { mockk(relaxed = true) }
    single<ShopifyAuthRepository> { mockk(relaxed = true) }
    single<AuthenticationRepo> { mockk(relaxed = true) }
    single<CartRepository> { mockk(relaxed = true) }
    single<ICategoryRepo> { mockk(relaxed = true) }
    single<ICurrencyRepository> { mockk(relaxed = true) }
    single<FavoriteRepo> { mockk(relaxed = true) }
    single<IHomeRepository> { mockk(relaxed = true) }
    single<IOrderRepo> { mockk(relaxed = true) }
    single<IPaymentRepository> { mockk(relaxed = true) }
    single<ProductDetailsRepository> { mockk(relaxed = true) }
    single<DiscountRepository> { mockk(relaxed = true) }

    // UseCase
    factory { CartUseCase(get()) }

    // Mock context if needed in CurrencyViewModel or SearchViewModel
    single<Context> { mockk(relaxed = true) }

    // ViewModels (use real constructors but injected mocks)
    viewModel { AddressViewModel(get()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { BrandDetailsViewModel(get()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { CurrencyViewModel(get(), get()) }
    viewModel { FavouritesViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { ProductDetailsViewModel(get(), get()) }
    viewModel { SharedViewModel() }
    viewModel { SearchViewModel(get()) }
    viewModel { DiscountViewModel(get()) }
}
