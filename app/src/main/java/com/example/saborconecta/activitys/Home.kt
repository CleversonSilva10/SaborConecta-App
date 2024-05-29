package com.example.saborconecta.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saborconecta.MainActivity
import com.example.saborconecta.R
import com.example.saborconecta.activitys_menu.AdicionarProduto
import com.example.saborconecta.activitys_menu.config_perfil
import com.example.saborconecta.adapter.MenuAdapter
import com.example.saborconecta.databinding.ActivityHomeBinding
import com.example.saborconecta.model.menu_home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val BD = FirebaseFirestore.getInstance()
    private val menuList = ArrayList<menu_home>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LeituraDados(binding)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Troca_de_Tela(Home::class.java)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.adicionarPedido -> {
                    Troca_de_Tela(AdicionarProduto::class.java)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.menu_profile -> {
                    Troca_de_Tela(config_perfil::class.java)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false // Caso nenhum dos itens corresponda
        }

        menuList.add(menu_home("Verduras e Vegetais", "01", R.drawable.icon_base))
        menuList.add(menu_home("Frutas Frescas", "02", R.drawable.icon_base))
        menuList.add(menu_home("Legumes e Grãos", "03", R.drawable.icon_base))
        menuList.add(menu_home("Ervas e Temperos", "04", R.drawable.icon_base))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = MenuAdapter(this, menuList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
    }

    private fun LeituraDados(binding: ActivityHomeBinding) {
        val UsuarioAtual = auth.currentUser?.uid.toString()
        fun descriptografar(dadoCriptografado: String): String {
            val chave = 3
            return dadoCriptografado.map { if (it.isLetter()) (it.toInt() - chave).toChar() else it }.joinToString("")
        }
        BD.collection("InfoUsuarios").document(UsuarioAtual)
            .addSnapshotListener { documento, error ->
                if (documento != null) {
                    val nome = descriptografar(documento.getString("Nome") ?: "")
                    binding.textViewUserName.text = "Bem vindo(a), $nome"
                }
            }
    }


    private fun Troca_de_Tela(next_tela: Class<*>) {
        val intent = Intent(this, next_tela)
        startActivity(intent)
        finish()
    }
}