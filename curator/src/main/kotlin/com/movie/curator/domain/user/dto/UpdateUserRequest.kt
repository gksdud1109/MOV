package com.movie.curator.domain.user.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Pattern(regexp = "(?s).*\\S.*", message = "nickname must not be blank")
    @field:Size(max = 100)
    val nickname: String? = null,
)
