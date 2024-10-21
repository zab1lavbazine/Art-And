package art.example.modules


import art.example.api.repository.impl.UserRepository
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.repository.impl.PostRepository
import art.example.database.AppDatabase
import com.example.arthub.api.RetrofitInstance
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModules = module {

    single { RetrofitInstance.getUserApiService() } // Ensure this returns the correct instance
    single { RetrofitInstance.getPostApiService() }
    single { RetrofitInstance.getTagApiService() }

    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().postDao() }
    single { get<AppDatabase>().tagDao() }

    factory { UserRepository(get(), get(), androidContext()) }
    factory { PostRepository(get(), get(), get(), get()) }

    viewModel { UserViewModel(get(), androidContext()) }
    viewModel { PostViewModel(get(), androidContext()) }



}