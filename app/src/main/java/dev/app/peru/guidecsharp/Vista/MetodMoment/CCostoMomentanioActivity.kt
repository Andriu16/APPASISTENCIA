package dev.app.peru.guidecsharp.Vista.MetodMoment

import android.os.Bundle
import android.os.StrictMode
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.CentroCostoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.MetodoDAO.Tareo_CCostoDAO
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.CentroCosto
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo_CCosto
import dev.app.peru.guidecsharp.databinding.ActivityDialogBuscarcBinding
import java.util.ArrayList

class CCostoMomentanioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDialogBuscarcBinding

    private lateinit var ccostoDAO: CentroCostoDAO
    private lateinit var tareo_CCostoDAO: Tareo_CCostoDAO
    private lateinit var CCostoAdapter: ArrayAdapter<CentroCosto>
    private lateinit var tareo_CCostoAdapter: ArrayAdapter<Tareo_CCosto>
    private var ccostoArray: ArrayList<CentroCosto> = ArrayList()
    private var tareo_CCostoArray: ArrayList<Tareo_CCosto> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDialogBuscarcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ccostoDAO = ConexionSqlite.getInstancia(this).centroCostoDAO()
        tareo_CCostoDAO = ConexionSqlite.getInstancia(this).tareo_CCostoDAO()
        insertCCosto()

        CCostoAdapter =
            ArrayAdapter<CentroCosto>(this, android.R.layout.simple_spinner_item, ccostoArray)
        binding.spnCCostos.adapter = CCostoAdapter
        listarCCosto()
        binding.btnAgregarCCosto.setOnClickListener {
            recivirdatosActivity()
            obtenerDato()
        }


    }

    private fun listarCCosto() {
        CCostoAdapter.clear()
        val lista = ccostoDAO.PA_ListaCentroCosto() as ArrayList<CentroCosto>

        if (lista.isNotEmpty()) {
            lista.iterator().forEach {
                CCostoAdapter.add(it)
            }
        } else {
            insertCCosto()
        }
        CCostoAdapter.notifyDataSetChanged()

    }

    private fun insertCCosto() {
        val obj = CentroCosto(0, "Costo 1 ", "", "", 0)
        ccostoDAO.guardarCentroCosto(obj)
    }

    private fun obtenerDato() {
        val ccosto = binding.spnCCostos.selectedItem as CentroCosto
        val codigo = ccosto.CC_Codigo
        val idTareo = recivirdatosActivity()
        insertTareoCCosto(idTareo!!, codigo.toInt())
    }

    private fun recivirdatosActivity(): Int? {
        val bundle = intent.extras
        val idTareo = bundle?.getInt("idTareo")
        return idTareo
    }

    private fun insertTareoCCosto(tareo: Int, ccosto: Int) {
        val obj = Tareo_CCosto(0, tareo, ccosto)
        val tareo = tareo_CCostoDAO.guardarTareo_CCosto(obj)
        Toast.makeText(this, "Se guardo correctamente con el id $tareo", Toast.LENGTH_SHORT).show()

    }


}
