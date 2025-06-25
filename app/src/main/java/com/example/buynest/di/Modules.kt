package com.example.buynest.di

import androidx.room.Room
import com.example.buynest.BuildConfig
import com.example.buynest.model.data.local.AppDatabase
import com.example.buynest.model.data.local.CurrencyDao
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.repository.payment.datasource.IPaymentDataSource
import com.example.buynest.repository.payment.datasource.PaymentDataSourceImpl
import com.example.buynest.model.data.remote.rest.StripeAPI
import com.example.buynest.repository.address.AddressRepository
import com.example.buynest.repository.address.AddressRepositoryImpl
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSource
import com.example.buynest.repository.address.datasource.ShopifyAddressDataSourceImpl
import com.example.buynest.repository.authentication.AuthenticationRepo
import com.example.buynest.repository.authentication.AuthenticationRepoImpl
import com.example.buynest.repository.authentication.firebase.FirebaseRepository
import com.example.buynest.repository.authentication.firebase.FirebaseRepositoryImpl
import com.example.buynest.repository.authentication.firebase.datasource.FirebaseDataSourceImpl
import com.example.buynest.repository.authentication.firebase.datasource.IFirebaseDataSource
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepository
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepositoryImpl
import com.example.buynest.repository.authentication.shopify.datasource.ShopifyAuthRemoteDataSource
import com.example.buynest.repository.authentication.shopify.datasource.ShopifyAuthRemoteDataSourceImpl
import com.example.buynest.repository.cart.CartRepository
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSource
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
import com.example.buynest.repository.category.CategoryRepoImpl
import com.example.buynest.repository.category.ICategoryRepo
import com.example.buynest.repository.currency.CurrencyRepositoryImpl
import com.example.buynest.repository.currency.ICurrencyRepository
import com.example.buynest.repository.currency.datasource.CurrencyDataSourceImpl
import com.example.buynest.repository.currency.datasource.ICurrencyDataSource
import com.example.buynest.repository.discount.DiscountRepository
import com.example.buynest.repository.discount.DiscountRepositoryImpl
import com.example.buynest.repository.discount.datasource.ShopifyDiscountDataSource
import com.example.buynest.repository.discount.datasource.ShopifyDiscountDataSourceImpl
import com.example.buynest.repository.favorite.FavoriteRepo
import com.example.buynest.repository.favorite.FavoriteRepoImpl
import com.example.buynest.repository.home.HomeRepository
import com.example.buynest.repository.home.IHomeRepository
import com.example.buynest.repository.order.IOrderRepo
import com.example.buynest.repository.order.OrderRepo
import com.example.buynest.repository.payment.IPaymentRepository
import com.example.buynest.repository.payment.PaymentRepositoryImpl
import com.example.buynest.repository.productDetails.ProductDetailsRepository
import com.example.buynest.repository.productDetails.ProductDetailsRepositoryImpl
import com.example.buynest.utils.constant.*
import com.example.buynest.viewmodel.address.AddressViewModel
import com.example.buynest.viewmodel.authentication.AuthenticationViewModel
import com.example.buynest.viewmodel.brandproducts.BrandDetailsViewModel
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
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val diModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<CurrencyDao> { get<AppDatabase>().currencyDao() }

    single (qualifier = named("CLIENT_APOLLO")){
        ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )
    }

    single (qualifier = named("ADMIN_APOLLO")) {
        ApolloClient.createApollo(
            BASE_URL = ADMIN_BASE_URL,
            ACCESS_TOKEN = BuildConfig.Admin_ACCESS_TOKEN,
            Header = ADMIN_HEADER
        )
    }

    //stripe client
    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.STRIPE_SECRET_KEY}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.stripe.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single<StripeAPI> {
        get<Retrofit>().create(StripeAPI::class.java)
    }

    //data source
    factory<ShopifyAddressDataSource> {
        ShopifyAddressDataSourceImpl(get(named("CLIENT_APOLLO")))
    }
    factory<IFirebaseDataSource> { FirebaseDataSourceImpl() }
    factory<ShopifyAuthRemoteDataSource> { ShopifyAuthRemoteDataSourceImpl(get(named("CLIENT_APOLLO"))) }
    factory<CartDataSource> { CartDataSourceImpl(get(named("CLIENT_APOLLO"))) }
    factory<IPaymentDataSource> { PaymentDataSourceImpl(get()) }
    factory<ICurrencyDataSource> { CurrencyDataSourceImpl(get())}
    factory<ShopifyDiscountDataSource> { ShopifyDiscountDataSourceImpl(get(named("ADMIN_APOLLO"))) }


    //repositories
    single<AddressRepository> { AddressRepositoryImpl(get()) }
    single<FirebaseRepository> { FirebaseRepositoryImpl(get()) }
    single<ShopifyAuthRepository> { ShopifyAuthRepositoryImpl(get()) }
    single<AuthenticationRepo> { AuthenticationRepoImpl(
        firebaseRepository = get(),
        shopifyRepository = get()
    ) }
    single<CartRepository> { CartRepositoryImpl(get()) }
    single<ICategoryRepo> { CategoryRepoImpl() }
    single<ICurrencyRepository> { CurrencyRepositoryImpl(get()) }
    single<FavoriteRepo> { FavoriteRepoImpl() }
    single<IHomeRepository> { HomeRepository() }
    single<IOrderRepo> { OrderRepo(get(named("ADMIN_APOLLO"))) }
    single<IPaymentRepository> { PaymentRepositoryImpl(get()) }
    single<ProductDetailsRepository> { ProductDetailsRepositoryImpl() }
    single<DiscountRepository> { DiscountRepositoryImpl(get()) }

    //viewModels
    viewModel { AddressViewModel(get()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { BrandDetailsViewModel(get()) }
    viewModel { CartViewModel(
        repository = get(),
        orderRepository = get()
    )}
    viewModel { CategoryViewModel(get()) }
    viewModel { CurrencyViewModel(
        repository = get(),
        context = androidContext()
    )}
    viewModel { FavouritesViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { SharedViewModel() }
    viewModel { SearchViewModel(context = androidContext()) }
    viewModel { DiscountViewModel(get()) }
}