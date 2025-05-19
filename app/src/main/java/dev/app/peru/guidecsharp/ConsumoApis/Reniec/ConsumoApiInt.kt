package dev.app.peru.guidecsharp.ConsumoApis.Reniec

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ConsumoApiInt {
    @GET("reniec/dni/{dni}")
    fun consultarDNI(@Path("dni") dni: String): Call<EmpleadoApiResponse>

    @GET("sunat/ruc/{ruc}")
    fun consultarRUC(@Path("ruc") ruc: String): Call<EmpleadoApiResponse>
}