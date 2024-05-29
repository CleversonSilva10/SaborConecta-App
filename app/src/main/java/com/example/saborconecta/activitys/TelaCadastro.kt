package com.example.saborconecta.activitys

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.saborconecta.MainActivity
import com.example.saborconecta.databinding.ActivityTelaCadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class TelaCadastro : AppCompatActivity() {
    private lateinit var binding: ActivityTelaCadastroBinding
    private val auth = FirebaseAuth.getInstance()
    private val BD = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var Consumidor_ou_AgroFamiliar: String = ""
        var valor = "0"

        binding.radioButtonConsumer.setOnClickListener {
            binding.radioButtonConsumer.isChecked = true
            binding.radioButtonProducer.isChecked = false
            Consumidor_ou_AgroFamiliar = "Consumidor"
        }

        binding.radioButtonProducer.setOnClickListener {
            binding.radioButtonProducer.isChecked = true
            binding.radioButtonConsumer.isChecked = false
            Consumidor_ou_AgroFamiliar = "Agrofamiliar"
        }

        binding.imageViewBack.setOnClickListener {
            Troca_de_Tela(MainActivity::class.java)
        }

        binding.ButtonLinkTermoUso.setOnClickListener {
            val url = "https://saborconecta.blogspot.com/2024/05/politica-de-privacidade-e-termos-de-uso.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.radioTermoUso.setOnCheckedChangeListener { buttonView, isChecked ->
            valor = if (isChecked) {
                Mensagens("Você aceitou os termos de uso", buttonView, Color.parseColor("#118DF0"))
                "1"
            } else {
                Mensagens("Reeleia novamente os termos de uso", buttonView, Color.parseColor("#118DF0"))
                "0"
            }
            if (valor == "0") {
                binding.radioTermoUso.isChecked = false
            }
        }

        binding.buttonSignUp.setOnClickListener {
            if (valor == "1") {
                val nome = binding.editTextName.text.toString()
                val email = binding.editTextEmail.text.toString()
                val senha = binding.editTextPassword.text.toString()
                val confirme_senha = binding.editTextConfirmPassword.text.toString()
                val telefone = binding.editTextPhone.text.toString()
                val dataNas = binding.editTextDOB.text.toString()
                novo_cadastro(
                    it,
                    nome,
                    email,
                    senha,
                    confirme_senha,
                    telefone,
                    dataNas,
                    Consumidor_ou_AgroFamiliar, valor
                )
            } else {
                Mensagens(
                    "Para efetuar cadastro, precisa aceitar os termos de uso", it, Color.parseColor("#118DF0")
                )
            }
        }
    }

    private fun novo_cadastro(
        view: View,
        nome: String,
        email: String,
        senha: String,
        confirme_senha: String,
        telefone: String,
        dataNas: String,
        Consumidor_ou_AgroFamiliar: String,
        TermoAceite: String
    ) {
        when {
            nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirme_senha.isEmpty() || telefone.isEmpty()
            -> {
                Mensagens("Atenção: Preencha todos os campos", view, Color.parseColor("#118DF0"))
            }

            senha.length < 6 -> {
                Mensagens("Senha: Mínimo de 6 caracters", view, Color.parseColor("#118DF0"))
            }

            confirme_senha.length < 6 -> {
                Mensagens(
                    "Confirme sua senha: Mínimo de 6 caracters",
                    view,
                    Color.parseColor("#118DF0")
                )
            }
            senha != confirme_senha -> {
                Mensagens(
                    "Campos de senha não estão iguais, digite novamente",
                    view,
                    Color.parseColor("#118DF0")
                )
            }
            senha == confirme_senha -> {
                authEmail(nome, email, senha, telefone, dataNas, Consumidor_ou_AgroFamiliar, view, TermoAceite)
            }
        }
    }
    private fun authEmail(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        dataNas: String,
        Consumidor_ou_AgroFamiliar: String,
        view: View,
        TermoAceite: String
    ) {

        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful) {
                Mensagens("Cadastro Realizado!", view, Color.parseColor("#FF4868"))
                SalvarInfoUsuarios(nome, email, dataNas, telefone, Consumidor_ou_AgroFamiliar, TermoAceite)
                deslogar()
                Troca_de_Tela(MainActivity::class.java)
            }
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthWeakPasswordException -> Mensagens(
                    "Senha inválida: Mínimo de 6 caracters",
                    view,
                    Color.parseColor("#118DF0")
                )

                is FirebaseAuthInvalidCredentialsException -> Mensagens(
                    "E-mail Inválido: Digite novamente!",
                    view,
                    Color.parseColor("#118DF0")
                )

                is FirebaseAuthUserCollisionException -> Mensagens(
                    "Conta já existente!",
                    view,
                    Color.parseColor("#118DF0")
                )

                is FirebaseNetworkException -> Mensagens(
                    "Sem conexão com a internet!",
                    view,
                    Color.parseColor("#118DF0")
                )

                else -> "Não foi possivel o cadastro"
            }
        }
    }

    private fun SalvarInfoUsuarios(nome: String, email: String, dataNas: String, telefone: String, Consumidor_ou_AgroFamiliar: String, TermoAceite: String) {
        val usuarioatual = auth.currentUser?.uid.toString()

        fun criptografar(dado: String): String {
            val chave = 3
            return dado.map { if (it.isLetter()) (it.toInt() + chave).toChar() else it }.joinToString("")
        }

        val dadousuario = hashMapOf(
            "Nome" to criptografar(nome),
            "email" to criptografar(email),
            "telefone" to criptografar(telefone),
            "data de Nascimento" to criptografar(dataNas),
            "Classificação Usuário" to criptografar(Consumidor_ou_AgroFamiliar),
            "TermoAceite" to criptografar(TermoAceite)
        )
        BD.collection("InfoUsuarios").document(usuarioatual).set(dadousuario)
    }


    private fun Mensagens(mensagem: String, view: View, cor: Int) {
        val snack = Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG)
        snack.setBackgroundTint(cor)
        snack.show()
    }

    private fun deslogar() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun Troca_de_Tela(next_tela: Class<*>) {
        val intent = Intent(this, next_tela)
        startActivity(intent)
        finish()
    }
}