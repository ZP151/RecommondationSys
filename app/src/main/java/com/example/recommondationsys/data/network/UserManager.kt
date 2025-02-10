package com.example.recommendationsys.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.recommondationsys.data.network.UserDTO
import com.example.recommondationsys.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PATCH
import java.io.EOFException

// 定义 API 接口
interface UserApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterApiResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginApiResponse?>

    /*@GET("/api/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): User*/

    @PATCH("/api/users/update-isnew")
    suspend fun updateUserIsNew(
        @Header("Authorization") token: String
    ): Response<Unit>
}

// 注册请求数据
data class RegisterRequest(val username: String, val password: String,val confirmPassword:String)
data class LoginRequest(val username: String, val password: String)

// API 响应
data class RegisterApiResponse(val token: String, val user:UserDTO)
data class LoginApiResponse(val token: String, val user:UserDTO)


// UserManager，负责与后端交互
object UserManager {
    private lateinit var sharedPreferences: SharedPreferences
    private val apiService: UserApiService

    private var loginJob: Job? = null  // ❶ 记录登录 API 请求

    private const val USER_KEY = "user_data"
    private const val TOKEN_KEY = "auth_token"
    private val gson = Gson()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UserApiService::class.java)
    }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    suspend fun registerUser(username: String, password: String,confirmPassword: String): Boolean {
        try {
            val response = apiService.register(RegisterRequest(username, password,confirmPassword)) // 调用注册 API

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (!responseBody?.token.isNullOrEmpty()) {
                    saveToken(responseBody!!.token)
                    return true
                } else {
                    Log.e("UserManager", "注册失败：返回的 Token 为空")
                    return false
                }
            } else {
                Log.e("UserManager", "注册失败: HTTP ${response.code()} ${response.message()}")
                return false
            }
        } catch (e: Exception) {
            Log.e("UserManager", "注册请求失败: ${e.message}")
            return false
        }
    }


    //现在loginuser调用后，将token和user对象都保存到sharedpref里了，方便后期调用来判断newuser
    suspend fun loginUser(username: String, password: String): Boolean {
        loginJob?.cancel()  // 取消之前的登录请求
        return withContext(Dispatchers.IO) {
            try {
                Log.d("UserManager", "正在登录: $username")
                val response = apiService.login(LoginRequest(username, password))

                Log.d("UserManager", "服务器返回: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()  // 获取 JSON 解析后的对象
                    Log.d("UserManager", "服务器返回 Body: $responseBody")

                    if (responseBody == null || responseBody.token.isNullOrEmpty()) {
                        Log.e("UserManager", "登录失败: 服务器返回空 Token 或者空 Body")
                        return@withContext false
                    }

                    saveToken(responseBody.token)

                    if (responseBody.user == null) {
                        Log.e("UserManager", "登录失败: 服务器返回的 User 为空")
                        return@withContext false
                    }

                    saveUser(responseBody.user)

                    // 确保 SharedPreferences 存储完成后再返回 true
                    Log.d("UserManager", "登录成功: ${responseBody.user}")
                    return@withContext true
                } else {
                    Log.e("UserManager", "登录失败: HTTP ${response.code()} ${response.message()}")
                    return@withContext false
                }
            } catch (e: EOFException) {
                Log.e("UserManager", "服务器返回了空数据，登录失败")
                return@withContext false
            } catch (e: Exception) {
                Log.e("UserManager", "登录请求异常: ${e.message}")
                return@withContext false
            }
        }
        /*val response = apiService.login(LoginRequest(username, password))
        return if (response.token.isNotEmpty()) {
            saveToken(response.token)
            saveUser(response.user)
            true
        } else {
            false
        }*/
        /*return try {
            Log.d("UserManager", "正在登录: $username")
            val response = apiService.login(LoginRequest(username, password))

            Log.d("UserManager", "服务器返回: ${response}")

            if (response.token.isNullOrEmpty()) {
                Log.e("UserManager", "登录失败: 服务器返回空 Token")
                return false
            }

            saveToken(response.token)

            if (response.user == null) {
                Log.e("UserManager", "登录失败: 服务器返回的 User 为空")
                return false
            }

            saveUser(response.user)
            Log.d("UserManager", "登录成功: ${response.user}")
            true
        } catch (e: Exception) {
            Log.e("UserManager", "登录请求异常: ${e.message}")
            false
        }*/
    }

    /*suspend fun getCurrentUserFromApiWithToken(): User? {
        val token = getToken() ?: return null
        return try {
            val response = apiService.getCurrentUser("Bearer $token")
            response
        } catch (e: Exception) {
            Log.e("UserManager", "获取用户信息失败: ${e.message}")
            null
        }
    }*/

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    // 🔥 存储完整 User 对象
    private fun saveUser(userDTO: UserDTO) {
        val userJson = gson.toJson(userDTO)
        sharedPreferences.edit().putString(USER_KEY, userJson).apply()
    }


    // 🔥 从 SharedPreferences 获取完整 UserDto 对象
    fun getUser(): UserDTO? {
       /* val userJson = sharedPreferences.getString(USER_KEY, null) ?: return null
        return gson.fromJson(userJson, User::class.java)*/

        val userJson = sharedPreferences.getString(USER_KEY, null)
        Log.d("UserManager", "从 SharedPreferences 获取的 JSON: $userJson")

        if (userJson == null) {
            Log.e("UserManager", "SharedPreferences 里没有用户数据！")
            return null
        }

        return try {
            gson.fromJson(userJson, UserDTO::class.java)
        } catch (e: Exception) {
            Log.e("UserManager", "JSON 解析失败", e)
            null
        }
    }


    // 🔥 添加 userId 的存取方法
    private fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return getUser()?.id
    }

    suspend fun logout() {
        loginJob?.cancel()  // ❹ 退出登录时取消 API 请求
        sharedPreferences.edit().clear().apply()
        Log.d("UserManager", "用户已退出，取消所有请求")
    }

    // 更新 SharedPreferences 和数据库
    suspend fun updateUserIsNew(isNew: Boolean) {
        val currentUser = getUser() // 从 SharedPreferences 获取当前用户对象

        if (currentUser != null) {
            val updatedUser = UserDTO( // 转换 User -> UserDTO
                id = currentUser.id,
                username = currentUser.username,
                isNewUser = isNew
            )
            saveUser(updatedUser) // 存入 SharedPreferences
        }
        //这里更新user为not new还要把user传回去
        //暂未实现
        val token = getToken() ?: return
        try {
            val response = apiService.updateUserIsNew("Bearer $token")
            if (response.isSuccessful) {
                Log.d("UserManager", "isNewUser 更新成功")
            } else {
                Log.e("UserManager", "更新 isNewUser 失败: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("UserManager", "更新 isNewUser 发生异常: ${e.message}")
        }
    }

}
