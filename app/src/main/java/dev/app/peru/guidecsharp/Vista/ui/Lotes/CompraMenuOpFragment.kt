package dev.app.peru.guidecsharp.Vista.ui.Lotes

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.Globales.Datos
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.databinding.FragmentMenuOpCompraBinding
import kotlinx.coroutines.*
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CompraMenuOpFragment : Fragment() {
    private var _binding: FragmentMenuOpCompraBinding? = null
    private val binding get() = _binding!!

    var _CodigoGeneral : Int = -1
    var _TituloDocumento = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMenuOpCompraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = "Opciones"

        binding.txtTitulo.text = "..."
        binding.txtFila1.text = "..."
        binding.txtFila2.text = "..."
        binding.txtFila3.text = "..."

        _CodigoGeneral = Datos.idMovimiento
        _TituloDocumento = "Sin titulo"

        cargarDatos()

//        binding.btnModificar.setOnClickListener { btnModificar() }
//        binding.btnVerDocumento.setOnClickListener { btnVerDocumento() }
//        binding.btnAnularDocumento.setOnClickListener { btnAnularDocumento() }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarDatos(){
        val frm = FunGeneral.mostrarCargando(requireContext())
        frm.show()

        var rs: ResultSet? = null
        var _msj = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                rs = EmpleadoDALC.PA_ObtenerMovimientosJE_Android(_CodigoGeneral)
            } catch (e: Exception) {
                _msj = e.message.toString()
            }

            activity?.runOnUiThread {
                frm.dismiss()

                if (_msj != "") {
                    Globales.showErrorMessage(_msj, requireContext())
                    return@runOnUiThread
                }

                while (rs!!.next()) {
                    val MOV_OrdenCompra = rs!!.getString("MOV_OrdenCompra")
                    val MOV_FechaRegistro = rs!!.getString("MOV_FechaRegistro")

                    val CLI_Ruc = rs!!.getString("CLI_Ruc")
                    val CLI_RazonSocial = rs!!.getString("CLI_RazonSocial")
                    val CLI_CodigoInterno = rs!!.getString("CLI_CodigoInterno")

                    val LOT_Lote = rs!!.getString("LOT_Lote")
                    val LOT_FechaEmision = rs!!.getString("LOT_FechaEmision") + " 00:00:00"
                    val LOT_FechaProduccion = rs!!.getString("LOT_FechaProduccion") + " 00:00:00"
                    val LOT_FechaVencimiento = rs!!.getString("LOT_FechaVencimiento") + " 00:00:00"

                    val fIngreso = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss") //En este formato ingresa
                    val fSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy") //En este formato se muestra
                    val fecha = LocalDateTime.parse(MOV_FechaRegistro, fIngreso) //Variable para mostrar

                    val fecha1 = LocalDateTime.parse(LOT_FechaEmision, fIngreso) //Variable para mostrar
                    val fecha2 = LocalDateTime.parse(LOT_FechaProduccion, fIngreso) //Variable para mostrar
                    val fecha3 = LocalDateTime.parse(LOT_FechaVencimiento, fIngreso) //Variable para mostrar

                    val PROD_Nombre = rs!!.getString("PROD_Nombre")
                    val PROD_Unidad = rs!!.getString("PROD_Unidad")
                    val MOV_Cantidad = rs!!.getFloat("MOV_Cantidad")

                    binding.txtTitulo.text = "Nº ${MOV_OrdenCompra} - FECHA: ${fecha.format(fSalida)}"
                    binding.txtTitulo2.text = "LOTE Nº ${LOT_Lote}"
                    binding.txtFila1.text = "${CLI_Ruc}"
                    binding.txtFila11.text = "${CLI_CodigoInterno} : ${CLI_RazonSocial}"

                    binding.txtFila2.text = "F. EMISIÓN: ${fecha1.format(fSalida)}"
                    binding.txtFila22.text = "F. PRODUCCIÓN: ${fecha2.format(fSalida)}"
                    binding.txtFila23.text = "F. VENCIMIENTO: ${fecha3.format(fSalida)}"

                    binding.txtFila3.text = "${PROD_Nombre}"
                    binding.txtFila32.text = "CANT. DESPACHADA: ${MOV_Cantidad} ${PROD_Unidad}"

                    break
                }

                //Otras Variables
                _TituloDocumento = binding.txtTitulo.text.toString()
            }
        }
    }
}