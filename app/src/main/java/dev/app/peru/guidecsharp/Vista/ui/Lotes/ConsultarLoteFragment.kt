package dev.app.peru.guidecsharp.Vista.ui.Lotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import dev.app.peru.guidecsharp.AccesoSql.Controlador.EmpleadoDALC
import dev.app.peru.guidecsharp.Globales.Datos
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentConsultarLoteBinding
import dev.app.peru.guidecsharp.databinding.FragmentSlideshowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConsultarLoteFragment : Fragment() {

    private var _binding: FragmentConsultarLoteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConsultarLoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = "Consulta de Lote"

        Globales.origenQR = "CONSULTA LOTE"
        Globales.frmConsultaLote = this

        binding.btnAgregarPredios.setOnClickListener { btnLeerQR() }

        return root
    }

    fun btnLeerQR(){
        val integrador = IntentIntegrator(requireActivity())
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrador.setPrompt("")
        integrador.setBeepEnabled(true)
        integrador.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                //Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
            } else {

                var _CodigoMovimiento : Int = -1

                //Validar que codigo sea entero y que exista en la BD

                try{
                    _CodigoMovimiento = result.contents.toInt()
                }catch (ex: java.lang.Exception){
                    Globales.showWarningMessage("EL QR ESCANEADO NO ES VÁLIDO", requireContext())
                    return
                }

                val frm = FunGeneral.mostrarCargando(requireContext())
                frm.show()

                var rs: ResultSet? = null
                var _msj = ""

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        rs = EmpleadoDALC.PA_ObtenerMovimientoJE_AndroidValidar(_CodigoMovimiento)
                    } catch (e: Exception) {
                        _msj = e.message.toString()
                    }

                    activity?.runOnUiThread {
                        frm.dismiss()

                        if (_msj != "") {
                            Globales.showErrorMessage(_msj, requireContext())
                            return@runOnUiThread
                        }

                        var _cuenta : Int = 0
                        while (rs!!.next()) {
                            _cuenta++
                            break
                        }

                        //Otras Variables

                        if (_cuenta == 0){
                            Globales.showWarningMessage("EL REGISTRO BUSCADO YA NO ESTÁ DISPONIBLE", requireContext())
                            return@runOnUiThread
                        }
                        else{
                            Datos.idMovimiento = _CodigoMovimiento
                            findNavController().navigate(R.id.action_nav_consulta_lote_to_compraMenuOpFragment)
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}