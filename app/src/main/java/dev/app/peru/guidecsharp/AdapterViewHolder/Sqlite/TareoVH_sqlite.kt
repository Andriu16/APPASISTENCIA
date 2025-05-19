package dev.app.peru.guidecsharp.AdapterViewHolder.Sqlite

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.app.peru.guidecsharp.AccesoSqlite.Controlador.ConexionSqlite
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Otros.*
import dev.app.peru.guidecsharp.AccesoSqlite.Modelo.Tareo
import dev.app.peru.guidecsharp.Globales.DatosTareo
import dev.app.peru.guidecsharp.Globales.FunGeneral
import dev.app.peru.guidecsharp.R

class TareoVH_sqlite(inflater: LayoutInflater, parent: ViewGroup, listener: TareoAD_sqlite.MyClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fila_tareo_sqlite, parent, false)),
    View.OnCreateContextMenuListener {

    var txtTituloTareo: TextView = itemView.findViewById(R.id.txtTituloTareo)
    var txtCultivoFila: TextView = itemView.findViewById(R.id.txtCultivoFila)
    var txtIncidenciaFila: TextView = itemView.findViewById(R.id.txtIncidenciaFila)
    var txtParronLoteCC: TextView = itemView.findViewById(R.id.txtParronLoteCC)
    var txtLabor: TextView = itemView.findViewById(R.id.txtLabor)
    var txtTipoTareo: TextView = itemView.findViewById(R.id.txtTipoTareo)
    var txtTipoTrab2: TextView = itemView.findViewById(R.id.txtTipoTrab2)
    var txtEstadoTareo: TextView = itemView.findViewById(R.id.txtEstadoTareo)
    var fabSincronizarTareo: FloatingActionButton = itemView.findViewById(R.id.fabSincronizarTareo)

    var _CodigoGeneral : Long = -1

    init {
        fabSincronizarTareo.setOnClickListener {
            listener.onClick(adapterPosition, "Sincronizar")
        }

        itemView.setOnClickListener {
            listener.onClick(adapterPosition, "Item_click")
        }

        itemView.setOnCreateContextMenuListener(this)
    }

    fun bind(obj: Tareo) {
        _CodigoGeneral = obj.idTareo
        var packingDAO = ConexionSqlite.getInstancia(itemView.context).packingDAO()
        var cultivoDao = ConexionSqlite.getInstancia(itemView.context).cultivoDAO()
        var centroCostoDAO = ConexionSqlite.getInstancia(itemView.context).centroCostoDAO()
        var laboresDAO = ConexionSqlite.getInstancia(itemView.context).laboresDAO()
        var actividadDAO = ConexionSqlite.getInstancia(itemView.context).actividadDAO()

        var objPacking : Packing = packingDAO.PA_ObtenerPacking_PorCodigo(obj.PAC_Codigo)
        var objCultivo : Cultivo = cultivoDao.PA_ObtenerCultivo_PorCodigo(obj.CU_Codigo)
        var objCentroCosto : CentroCosto = centroCostoDAO.PA_ObtenerCentroCosto_PorCodigo(obj.CC_Codigo)
        var objLabores : Labores = laboresDAO.PA_ObtenerLabores_PorCodigo(obj.Lab_Codigo)
        var objActividad : Actividad = actividadDAO.PA_ObtenerActividad_PorCodigo(obj.AC_Codigo)

        txtTituloTareo.text = ".:: ${objPacking.PAC_Descripcion}"
        txtCultivoFila.text = "CULTIVO: ${objCultivo.CU_Descripcion}"
        txtIncidenciaFila.text = "INCIDENCIA: ${obj.TA_Incidencia}"

        if (obj.TA_Incidencia == "DIRECTA"){
            txtParronLoteCC.text = "PARRÓN/LOTE: ${objCentroCosto.CC_Descripcion}"
        }
        else{
            txtParronLoteCC.text = "CENTRO COSTO: ${objCentroCosto.CC_Descripcion}"
        }

        txtLabor.text = "LABOR: ${objLabores.Lab_Descripcion} - ${objActividad.AC_Descripcion}"
        txtTipoTareo.text = "${obj.TA_TipoTareo}"

        if (obj.TA_TipoTareo == "JORNAL"){
            txtTipoTareo.setTextColor(Color.BLACK)
        }
        else if (obj.TA_TipoTareo == "TAREA"){
            txtTipoTareo.setTextColor(Color.BLUE)
        }
        else {
            txtTipoTareo.setTextColor(Color.GREEN)
        }

        txtTipoTrab2.text = "${obj._Trabajadores}"

        if (obj.TA_Codigo == -2){
            txtEstadoTareo.text = "ERROR EN SINCRONIZACIÓN"
            txtEstadoTareo.setTextColor(Color.RED)
            fabSincronizarTareo.isVisible = true
        }
        else if (obj.TA_Codigo == -1){
            txtEstadoTareo.text = "NO SINCRONIZADO"
            txtEstadoTareo.setTextColor(Color.RED)
            fabSincronizarTareo.isVisible = true
        }else{
            txtEstadoTareo.text = "SINCRONIZADO"
            txtEstadoTareo.setTextColor(Color.GREEN)
            fabSincronizarTareo.isVisible = false
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        DatosTareo.TA_Codigo = _CodigoGeneral
        menu?.add(Menu.NONE, R.id.action_editar_datos, 0, "Editar Datos")
        menu?.add(Menu.NONE, R.id.action_eliminar_tareo, 0, "Eliminar Tareo")
    }
}