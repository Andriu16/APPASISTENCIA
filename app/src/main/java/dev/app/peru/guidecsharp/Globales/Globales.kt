package dev.app.peru.guidecsharp.Globales

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.awesomedialog.*
import com.google.android.material.navigation.NavigationView
import dev.app.peru.guidecsharp.AccesoSql.Modelo.DetalleTareo_sql
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.Vista.InicioActivity
import dev.app.peru.guidecsharp.Vista.ui.Lotes.ConsultarLoteFragment
import dev.app.peru.guidecsharp.Vista.ui.gallery.DetalleTareoFragment
import dev.app.peru.guidecsharp.Vista.ui.gallery.TareoNuevoFragment
import dev.app.peru.guidecsharp.Vista.ui.home.AsistenciaDiariaFragment
import dev.app.peru.guidecsharp.Vista.ui.home.AsistenciaDiariaOnlineFragment
import dev.app.peru.guidecsharp.Vista.ui.home.TomarAsistenciaFragment

object Globales {
    var _IDGRUPOTRABAJO = ""
    var _DESCRIPCIONGRUPOTRABAJO = ""
    var _IDTURNO = ""
    var _DESCRIPCIONTURNO = ""
    var _ArrayDetalleTareo = ArrayList<DetalleTareo_sql>()

    var _fragmentTareoNuevo = TareoNuevoFragment()



    var loadingDialog: AwesomeDialog? = null
    private var progressDialog: ProgressDialog? = null

    var Tienda = "PRINCIPAL"
    val _Version = "1.0.0000"
    var CodEmpresa = 1
    var nombreApp = "Fundo App"

    var CodigoUsuario = -1
    var Usuario = ""
    var Contraseña = ""
    var CodRol = -1
    var Empleado = ""
    var cod = -1
    var rol = ""

    fun reiniciar(){
        CodigoUsuario = -1
        Usuario = ""
        Contraseña = ""
        CodRol = -1
        Empleado = ""
        cod = -1
        rol = ""
    }

    var origenQR : String = ""
    var origenQR2 : String = ""
    lateinit var asist : TomarAsistenciaFragment
    lateinit var verasis : AsistenciaDiariaFragment
    lateinit var verasis2 : AsistenciaDiariaOnlineFragment
    lateinit var frmConsultaLote : ConsultarLoteFragment
    lateinit var detalletareo: DetalleTareoFragment
    lateinit var inic: InicioActivity

    lateinit var navView : NavigationView
    var cargarView = "NO"

    fun SinPermiso(context: Context){
        showWarningMessage("No tiene permiso para este proceso", context)
        //Toast.makeText(context, "No tiene permiso para este proceso", Toast.LENGTH_SHORT).show()
    }

    fun showWarningMessage(msj: String, context: Context) {
        AwesomeDialog.build((context) as Activity)
            .title("Advertencia")
            .body(msj)
            .icon(R.drawable.ic_warning)
            .onPositive("OK") {
            }
    }
    fun showSuccessMessage(msj: String, context: Context) {
        AwesomeDialog.build((context) as Activity)
            .title("¡Excelente!")
            .body(msj)
            .icon(R.drawable.success)
            .onPositive("OK") {
            }
    }
    fun showErrorMessage(msj: String, context: Context) {
        AwesomeDialog.build((context) as Activity)
            .title("ERROR")
            .body(msj)
            .icon(R.drawable.error)
            .onPositive("OK") {
            }
    }

    fun showWarningMessageAndCuestions(msj: String, context: Context,onPositiveClick:(()->Unit)?=null,onNegativeClick:(()->Unit)?=null) {
        AwesomeDialog.build((context) as Activity)
            .body(msj)
            .icon(R.drawable.question2)
            .onPositive("Si") {onPositiveClick?.invoke()}
            .onNegative("No") { onNegativeClick?.invoke()
            }
    }
    fun showSuccessesMessageAndCuestions(msj: String, context: Context,onPositiveClick:(()->Unit)?=null,onNegativeClick:(()->Unit)?=null) {
        AwesomeDialog.build((context) as Activity)
            .title("Succuses")
            .body(msj)
            .icon(R.drawable.success)
            .onPositive("OK") { onPositiveClick?.invoke() }
            .onNegative("Cancel") { onNegativeClick?.invoke() }
    }
    fun showErrorMessageAndCuestions(msj: String, context: Context,onPositiveClick:(()->Unit)?=null,onNegativeClick:(()->Unit)?=null) {
        AwesomeDialog.build((context) as Activity)
            .title("Error")
            .body(msj)
            .icon(R.drawable.error)
            .onPositive("OK") { onPositiveClick?.invoke()}
            .onNegative("Cancel") { onNegativeClick?.invoke()}
    }

    fun showLoadingMessage(message: String, context: Context) {
        hideLoadingMessage()
        progressDialog = ProgressDialog(context).apply {
            setTitle("Cargando...")
            setMessage(message)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hideLoadingMessage() {
        progressDialog?.dismiss()
        progressDialog = null
    }
    //






}