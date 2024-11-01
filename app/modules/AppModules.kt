package com.example.arthub.modules

import com.example.arthub.ViewModel.PostViewModel
import com.example.arthub.api.RetrofitInstance
import com.example.arthub.api.repository.PostRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


val appModules = module {

    single{ RetrofitInstance.postApi }

    factory { PostRepository(get()) }
    viewModel { PostViewModel(get())}

}