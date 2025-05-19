package dev.app.peru.guidecsharp.Vista.ui.cerrarsesion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import dev.app.peru.guidecsharp.Globales.Globales
import dev.app.peru.guidecsharp.R
import dev.app.peru.guidecsharp.databinding.FragmentCerrarSesionBinding
import dev.app.peru.guidecsharp.databinding.FragmentHomeBinding

class CerrarSesionFragment : Fragment() {

    private var _binding: FragmentCerrarSesionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentCerrarSesionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Globales.inic.cerrarSesion()

        setHasOptionsMenu(true)
        return root
    }

}