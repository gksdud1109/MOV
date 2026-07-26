package com.movie.curator.common.error

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
