package utn.methodology.infrastructure.persistence.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import utn.methodology.domain.entities.Usuario
import org.bson.Document


class RepositorioUsuario (private val database: MongoDatabase) {

     private var colección: MongoCollection<Document>

    init {
        colección = database.getCollection("usuarios") as MongoCollection<Document>
    }

    fun GuardaroActualizar (Usuario: Usuario) {
        try {
            val opcion = UpdateOptions().upsert(true)
            val filtrar = Document("id", Usuario.getId())
            val actualizar = Document("\$set", Usuario.toPrimitives())
            colección.updateOne(filtrar, actualizar, opcion)
        }catch(e: Exception) { println ("ocurrió un error: ${e.message}")}
    }


    fun RecuperarPorId (uuid: String): Usuario? {
        var usuario: Usuario? = null
        try {
            val filtrar = Document("uuid", uuid)

            val primitives = this.colección.find(filtrar).firstOrNull()

            usuario = Usuario.fromPrimitives(primitives as Map<String, String>)
        } catch (e: Exception){ println ("no se pudo encontrar el usuario con ese id: ${e.message} ")}
        return usuario
    }

    fun recuperarTodos(): List<Usuario> {
        val primitives = colección.find().map { it as Document }.toList()
        return primitives.map { Usuario.fromPrimitives(it.toMap() as Map<String, String>) }
    }

    fun existenciaporUsername(UserName: String): Boolean {
        val filtrar = Document("UserName", UserName)
        return colección.find(filtrar).firstOrNull() != null
    }

    fun existenciaporEmail(email: String): Boolean {
        val filtrar = Document("email", email)
        return colección.find(filtrar).firstOrNull() != null
    }
}