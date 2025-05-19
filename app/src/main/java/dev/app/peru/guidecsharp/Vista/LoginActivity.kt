package dev.app.peru.guidecsharp.Vista

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.AccesoSql.Modelo.PuertaIngreso_sql
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.RecordarDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Recordar
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.ResultSet

class LoginActivity : AppCompatActivity(), Validator.ValidationListener {

    private lateinit var binding: ActivityLoginBinding

    @NotEmpty(message = "Ingrese usuario, porfavor")
    var usuario: EditText? = null

    @NotEmpty(message = "Ingrese contraseña, porfavor")
    var clave: EditText? = null

    //Variables
    lateinit var recordarDAO: RecordarDAO
    private var validator = Validator(this)
    private var actualizarApp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializando variables de SQLite
        recordarDAO = ConexionSqlite.getInstancia(this).recordarDAO()
        usuario = binding.etUsuario.editText
        clave = binding.etClave.editText
        validator.setValidationListener(this)

//        binding.txtPuerta.setEndIconOnClickListener { btnPuerta() }
//        binding.etpuerta.setOnClickListener { btnPuerta() }

        datosIniciales()
        conexionNube()

        binding.btnLogin.setOnClickListener { ingresar() }
        usuario?.requestFocus()
    }

    var _CodigoPuerta : Int = -1;
    private fun btnPuerta(){
        var _listaPuertas = ArrayList<PuertaIngreso_sql>()

        var rs: ResultSet? = null

        var frm = FunGeneral.mostrarCargando(this)
        frm.show()

        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {

            try {
                rs = EmpleadoDALC.PA_ListarPuertaIngreso_Android()
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    var obj = PuertaIngreso_sql()
                    obj.PI_Codigo = rs!!.getInt("PI_Codigo")
                    obj.PI_Descripcion = rs!!.getString("PI_Descripcion")
                    _listaPuertas.add(obj)
                }

                val items = arrayOfNulls<String>(_listaPuertas.size)
                for(i in _listaPuertas.indices){
                    items[i] = _listaPuertas[i].PI_Descripcion
                }

                MaterialAlertDialogBuilder(this@LoginActivity)
                    .setTitle("Elija una Puerta")
                    .setIcon(R.drawable.ic_store_black)
                    .setItems(items) { dialog, which ->
//                        binding.txtPuerta.editText!!.setText(_listaPuertas[which].PI_Descripcion)
                        _CodigoPuerta = _listaPuertas[which].PI_Codigo
                    }
                    .show()
            }
        }
    }

    private fun datosIniciales() {
        title = Globales.nombreApp
        //Codigo para permitir conexión no segura al servidor
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun conexionNube() {
        val obj = recordarDAO.PA_UltimoRecordado()
        if (obj != null){
            usuario?.setText(obj.REC_Usuario)
            clave?.setText(obj.REC_Contraseña)
            binding.chkRecordar.isChecked = true

            Globales.CodigoUsuario = obj.CodUsuario
            Globales.Usuario = obj.REC_Usuario
            Globales.Contraseña = obj.REC_Contraseña
            Globales.CodRol = obj.CodRol
            Globales.Empleado = obj.Empleado
            Globales.cod = obj.CodEmpleado
            Globales.rol = obj.Rol

            startActivity(Intent(this, InicioActivity::class.java))
            finish()
        }
    }

    private fun ingresar() {
        binding.btnLogin.isEnabled = false
        validator.validate()
    }

    private fun estadoControles(estado: Boolean) {
        binding.etUsuario.editText?.isEnabled = estado
        binding.etClave.editText?.isEnabled = estado
        binding.chkRecordar.isEnabled = estado
        binding.btnLogin.isEnabled = estado
    }

    override fun onValidationSucceeded() {
        estadoControles(false)

        /*if (actualizarApp) {
            Globales.showWarningMessage("La versión de la App ha cambiado, es necesario actualizar a la última versión.", this)
            estadoControles(true)
            return
        }*/

        val _Usuario = usuario?.text.toString()
        val _Clave = clave?.text.toString()

        try {
            val rs = EmpleadoDALC.PA_ObtenerUsuario_PorNick(_Usuario)

            var msj = ""
            var i = 0
            while (rs.next()) {
                i++
                val _ClaveBD = rs.getString("usu_password")
                if (_Clave != _ClaveBD) {
                    msj = "Verifique su contraseña"
                    break
                }

                Globales.CodigoUsuario = rs.getInt("usu_id")
                Globales.Usuario = rs.getString("usu_login")
                Globales.Contraseña = rs.getString("usu_password")
                Globales.cod = rs.getInt("emp_id")
                Globales.Empleado = rs.getString("Empleado")
                Globales.CodRol = rs.getInt("rol_id")
                Globales.rol = rs.getString("rol_nombre")
                break
            }

            if (i == 0) {
                msj = "Usuario no encontrado"
            }

            //VALIDANDO DATOS
            if (msj != "") {
                Toast.makeText(this, msj, Toast.LENGTH_LONG).show()
                estadoControles(true)
            } else {
                //Si está marcado chkRecordar
                recordarDAO.PA_EliminarRecordado()
                if (binding.chkRecordar.isChecked) {
                    recordarDAO.crear(construirRecordar())
                }

                Globales.cargarView = "NO"
                FunGeneral.obtenerPemisos(this)

                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al conectar al servidor.\n${e.message}", Toast.LENGTH_LONG).show()
            Log.d("Error: ", e.message.toString())
            estadoControles(true)
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            val message = error.getCollatedErrorMessage(this)
            if (view is EditText) {
                (view as EditText).error = message
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            estadoControles(true)
        }
    }

    fun construirRecordar(): Recordar {
        val obj = Recordar(
            -1,
            Globales.CodigoUsuario,
            Globales.Usuario,
            Globales.Contraseña,
            Globales.CodigoUsuario,
            Globales.cod,
            Globales.Empleado,
            Globales.CodRol,
            Globales.rol
        )
        return obj
    }
}