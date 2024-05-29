import junit.framework.TestCase.assertEquals
import org.junit.Test

class CriptografiaTest {

    companion object {
        fun criptografar(dado: String, chave: Int): String {
            return dado.map { if (it.isLowerCase()) {
                (((it.toInt() - 'a'.toInt() + chave) % 26) + 'a'.toInt()).toChar()
            } else it }.joinToString("")
        }

        fun descriptografar(dadoCriptografado: String, chave: Int): String {
            return dadoCriptografado.map { if (it.isLowerCase()) {
                (((it.toInt() - 'a'.toInt() - chave + 26) % 26) + 'a'.toInt()).toChar()
            } else it }.joinToString("")
        }
    }
    @Test
    fun testCriptografiaDescriptografia() {
        val dadoOriginal = "HelloWorld"
        val chave = 3

        val dadoCriptografado = criptografar(dadoOriginal, chave)
        val dadoDescriptografado = descriptografar(dadoCriptografado, chave)

        assertEquals(dadoOriginal, dadoDescriptografado)
    }
}
