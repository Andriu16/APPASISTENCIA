package dev.app.peru.guidecsharp.ConsumoApis.Reniec

import android.util.Log
import dev.app.peru.guidecsharp.Globales.FunGeneral
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsumoApi(var con : Conector) {
    private var api = FunGeneral.retrofitDoc.create(ConsumoApiInt::class.java)

    fun consultarDni(dni: String) {
        val call = api.consultarDNI(dni)

        call.enqueue(object : Callback<EmpleadoApiResponse> {
            override fun onResponse(call: Call<EmpleadoApiResponse>, response: Response<EmpleadoApiResponse>) {
                if (!response.isSuccessful) {
                    try {
                        con.fallaConsultaDni("error code: " + response.code())
                    } catch (ex: Exception) {
                        Log.i("ERROR", ex.toString())
                    }
                    return
                }

                val empleado = response.body() as EmpleadoApiResponse

                try {
                    con.exitoConsultaDni(empleado)
                } catch (ex: Exception) {
                    Log.i("ERROR", ex.toString())
                }
            }

            override fun onFailure(call: Call<EmpleadoApiResponse>, t: Throwable) {
                try {
                    con.fallaConsultaDni(t.message.toString())
                } catch (ex: Exception) {
                    Log.i("ERROR", ex.toString())
                }
            }
        })
    }
}