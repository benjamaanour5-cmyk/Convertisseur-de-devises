package com.example.convertisseurdedevises

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var spDeviseSource: Spinner
    private lateinit var spDeviseCible: Spinner
    private lateinit var tvMontant: TextView
    private lateinit var tvResultat: TextView
    private lateinit var btnConvertir: Button
    private lateinit var btnInverser: ImageButton

    private val taux = mapOf(
        "EUR" to 1.0,
        "USD" to 1.17,
        "TND" to 3.40,
        "GBP" to 0.87,
        "JPY" to 172.50,
        "CAD" to 1.62
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupération des composants XML
        spDeviseSource = findViewById(R.id.spDeviseSource)
        spDeviseCible = findViewById(R.id.spDeviseCible)
        tvMontant = findViewById(R.id.tvMontant)
        tvResultat = findViewById(R.id.tvResultat)
        btnConvertir = findViewById(R.id.btnConvertir)
        btnInverser = findViewById(R.id.btnInverser)

        configurerDevises()
        configurerClavier()

        btnConvertir.setOnClickListener {
            effectuerConversion()
        }

        btnInverser.setOnClickListener {
            inverserDevises()
        }
    }

    private fun configurerDevises() {
        val adaptateur = ArrayAdapter.createFromResource(
            this,
            R.array.devises,
            android.R.layout.simple_spinner_item
        )

        adaptateur.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spDeviseSource.adapter = adaptateur
        spDeviseCible.adapter = adaptateur

        spDeviseSource.setSelection(0)
        spDeviseCible.setSelection(1)
    }

    private fun configurerClavier() {
        val ids = intArrayOf(
            R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn0, R.id.btnPoint
        )

        for (id in ids) {
            val bouton = findViewById<Button>(id)

            bouton.setOnClickListener {
                ajouterCaractere(bouton.text.toString())
            }
        }

        val btnC = findViewById<Button>(R.id.btnC)

        btnC.setOnClickListener {
            tvMontant.text = "0"
            tvResultat.text = ""
        }
    }

    private fun ajouterCaractere(c: String) {
        val montant = tvMontant.text.toString()

        if (montant.length >= 10) return
        if (c == "." && montant.contains(".")) return

        val nouveauMontant = when {
            montant == "0" && c != "." -> c
            montant == "0" && c == "." -> "0."
            else -> montant + c
        }

        tvMontant.text = nouveauMontant
    }

    private fun effectuerConversion() {
        val montant = tvMontant.text.toString().toDoubleOrNull() ?: 0.0

        if (montant == 0.0) {
            Toast.makeText(
                this,
                "Veuillez saisir un montant",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val source = spDeviseSource.selectedItem.toString()
        val cible = spDeviseCible.selectedItem.toString()

        val resultat = convertir(montant, source, cible)

        tvResultat.text = String.format(
            Locale.US,
            "%.2f",
            resultat
        )
    }

    private fun convertir(
        montant: Double,
        source: String,
        cible: String
    ): Double {
        val tauxSource = taux[source] ?: 1.0
        val tauxCible = taux[cible] ?: 1.0

        return (montant / tauxSource) * tauxCible
    }

    private fun inverserDevises() {
        val ancienneSource = spDeviseSource.selectedItemPosition
        val ancienneCible = spDeviseCible.selectedItemPosition

        spDeviseSource.setSelection(ancienneCible)
        spDeviseCible.setSelection(ancienneSource)

        if (tvResultat.text.isNotEmpty()) {
            effectuerConversion()
        }
    }
}
