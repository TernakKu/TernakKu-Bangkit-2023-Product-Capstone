package com.dicoding.ternakku.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LoginResult(

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("Name")
	val name: String,

	@field:SerializedName("token")
	val token: String
)
