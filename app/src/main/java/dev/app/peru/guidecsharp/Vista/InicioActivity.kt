package dev.app.peru.guidecsharp.Vista

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.PermisosDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.RecordarDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.DatosIniciales
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.Globales.Globales.navView
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.ActivityInicioBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date


class InicioActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInicioBinding

    lateinit var recordarDAO: RecordarDAO
    lateinit var permisoDAO: PermisosDAO
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Globales.inic = this
        //binding.navView.itemIconTintList = null
        recordarDAO = ConexionSqlite.getInstancia(this).recordarDAO()
        permisoDAO = ConexionSqlite.getInstancia(this).permisosDAO()

// Iniciar descarga automática al crear la actividad

        cargarInicio(R.id.nav_gallery)

        setSupportActionBar(binding.appBarHome.toolbar)




        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        val iconNuevaVersion = navView.getHeaderView(0).findViewById<ImageView>(R.id.iconNuevaVersion)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_ver_asistencia,R.id.nav_control_puerta,R.id.nav_ver_control_puerta,R.id.nav_gallery, R.id.nav_consulta_lote,  R.id.nav_configuration, R.id.nav_cerrar_sesion), drawerLayout        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val usu = navView.getHeaderView(0).findViewById<TextView>(R.id.txtTitulo)
        //val ver = navView.getHeaderView(0).findViewById<TextView>(R.id.txtSubTitulo)
        //usu.text = "User: "+Globales.Usuario
        //ver.text = "Versión: v4.6"

        //cambiar tambien en activity_login.xml de forma manual: v4.3

        Globales.cargarView = "SI"
        Globales.navView = navView
        FunGeneral.visibleItemsMenu(this)

        val navMenu = navView.menu
        val cerrarSesionItem = navMenu.findItem(R.id.nav_cerrar_sesion)
        val spanString = SpannableString(cerrarSesionItem.title)
        spanString.setSpan(ForegroundColorSpan(Color.RED), 0, spanString.length, 0)
        cerrarSesionItem.title = spanString


        iconNuevaVersion.setOnClickListener {
            // Replace "https://example.com" with your actual URL
            val url = "https://drive.google.com/drive/u/1/folders/1at4J63jui6RFLqwGmYnHalXaumikEi4T"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

    }
    @SuppressLint("RestrictedApi")
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (menu is MenuBuilder) {
            try {
                val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
                field.isAccessible = true
                field.setBoolean(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }


    fun cargarInicio(id: Int){
        val graph = findNavController(R.id.nav_host_fragment_content_home).graph
        graph.startDestination = id
        findNavController(R.id.nav_host_fragment_content_home).setGraph(graph)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (Globales.origenQR == "ASISTENCIA" && Globales.asist != null && Globales.origenQR2 == "PUERTA"){
            Globales.origenQR2 = ""
            Globales.asist.onActivityResult2(requestCode, resultCode, intent)
        }
        else if (Globales.origenQR == "ASISTENCIA" && Globales.asist != null){
            Globales.asist.onActivityResult(requestCode, resultCode, intent)
        }

        /**if (Globales.origenQR == "DETALLETAREO" && Globales.detalletareo != null){
            Globales.detalletareo.onActivityResult(requestCode, resultCode, intent)
        }**/

        if (Globales.origenQR == "VERASISTENCIA" && Globales.verasis != null){
            Globales.verasis.onActivityResult(requestCode, resultCode, intent)
        }

        if (Globales.origenQR == "VERASISTENCIA2" && Globales.verasis2 != null){
            Globales.verasis2.onActivityResult(requestCode, resultCode, intent)
        }

        if (Globales.origenQR == "CONSULTA LOTE" && Globales.frmConsultaLote != null){
            Globales.frmConsultaLote.onActivityResult(requestCode, resultCode, intent)
        }
    }

    fun cerrarSesion(){

        val msj = "La sesión actual se cerrará.\n ¿Desea continuar?"
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("¡CONFIRME!")
        dialog.setMessage(msj)
        dialog.setPositiveButton("SI") { dialoginterface, i ->
            Globales.reiniciar()
            recordarDAO.PA_EliminarRecordado()

            var PI_Codigo : Int = -1
            var PI_Descripcion: String = "TODO"

            //Guardar ultimo PI_Codigo leido...
            var datosInicialesDAO = ConexionSqlite.getInstancia(this).datosInicialesDAO()
            datosInicialesDAO.PA_EliminarDatosIniciales()
            datosInicialesDAO.crear(DatosIniciales(0, PI_Codigo, PI_Descripcion))

            val asistenciaPuertaDAO = ConexionSqlite.getInstancia(this).asistenciaPuertaDAO()
            asistenciaPuertaDAO.eliminarTodos()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        dialog.setNegativeButton("NO") { dialoginterface, i ->
            dialoginterface.dismiss()
        }
        dialog.show()
    }



}