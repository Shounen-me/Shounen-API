package src.main.kotlin.utils

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import src.main.kotlin.models.Token

interface LoginService {

    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("code") code: String,
                       @Field("code_verifier") codeVerifier: String,
                       @Field("grant_type") grantType: String) : Call<Token>

}