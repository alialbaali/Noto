package com.noto.data.repository.util

import java.net.ConnectException
import java.net.UnknownHostException

internal const val NETWORK_ERROR = "Please connect to the internet!"
internal const val UNKNOWN_ERROR = "Something went wrong! please try again later"

internal inline fun <R> tryCatching(block: () -> (R)): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        println(e.printStackTrace())
        when (e) {
            is UnknownHostException -> Result.failure(Throwable(NETWORK_ERROR))
            is ConnectException -> Result.failure(Throwable(NETWORK_ERROR))
//            is HttpException -> {
//                val errorBody = e.response()?.errorBody()?.string() ?: return Result.failure(Throwable(UNKNOWN_ERROR))
//                val error = JSONObject(errorBody).get("error") as String?
//                Result.failure(Throwable(error))
//            }
            else -> Result.failure(Throwable(e.message))
        }
    }
}


//object ErrorConverter {
//
//    private lateinit var converter: Converter<ResponseBody, ResponseSchema<Any>>
//
//    operator fun invoke(converter: Converter<ResponseBody, ResponseSchema<Any>>) {
//        println("INVOKED")
//        this.converter = converter
//    }
//
//    fun parse(responseBody: ResponseBody): String? {
//        return runCatching {
//            println(responseBody.string())
//            converter.convert(responseBody)
//        }.mapCatching { response ->
//            println("ERROR ${response?.error}")
//            response?.error
//        }.getOrNull()
//    }
//
////    fun convert(responseBody: ResponseBody?): Result<ResponseSchema<Any>> {
////        return if (responseBody != null) {
////            runCatching {
////                converter.convert(responseBody)
////            }.mapCatching { response ->
////                if (response != null) {
////                    Result.success(response)
////                } else {
////                    Result.failure(Throwable("Something Went Wrong"))
////                }
////            }.getOrElse {
////                Result.failure(it)
////            }
////        } else {
////            Result.failure(Throwable("Something Went Wrong"))
////        }
//    }
//}