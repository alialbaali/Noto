package com.noto.todo.di

import com.noto.database.NotoDatabase
import com.noto.todo.repository.SubTodoRepository
import com.noto.todo.repository.TodoRepository
import com.noto.todo.repository.TodolistRepository
import com.noto.todo.viewModel.TodoViewModel
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.todo.viewModel.TodolistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val todoModule = module {

    viewModel { TodolistListViewModel(get()) }

    viewModel { TodolistViewModel(get(), get()) }

    viewModel { TodoViewModel(get(), get()) }

    single { TodolistRepository(get(), get()) }

    single { TodoRepository(get()) }

    single { SubTodoRepository(get()) }

    single { NotoDatabase.getInstance(androidContext()).todolistDao }

    single { NotoDatabase.getInstance(androidContext()).todoDao }

    single { NotoDatabase.getInstance(androidContext()).subTodoDao }

}
