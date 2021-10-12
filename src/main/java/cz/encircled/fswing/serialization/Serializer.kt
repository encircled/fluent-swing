package cz.encircled.fswing.serialization

import com.google.gson.*
import cz.encircled.fswing.observable.collection.DelegatingObservableList
import cz.encircled.fswing.observable.collection.DelegatingObservableSet
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.observable.collection.ObservableSet
import javafx.beans.property.SimpleLongProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.lang.reflect.ParameterizedType


interface Serializer {

    fun <T> toObject(string: ByteArray, clazz: Class<T>): T = toObject(String(string), clazz)

    fun <T> toObject(string: String, clazz: Class<T>): T

    fun fromObject(obj: Any): String

}

class GsonSerializer : Serializer {

    private val gson: Gson

    init {
        val longSer: JsonSerializer<SimpleLongProperty> = JsonSerializer { t, _, ctx -> ctx.serialize(t.get()) }
        val longDes: JsonDeserializer<SimpleLongProperty> = JsonDeserializer { t, _, _ -> SimpleLongProperty(t.asLong) }
        val obsDes: JsonDeserializer<ObservableList<*>> = JsonDeserializer { t, type, ctx ->
            FXCollections.observableArrayList(
                (t as JsonArray).map { ctx.deserialize<Any>(it, (type as ParameterizedType).actualTypeArguments[0]) }
            )
        }
        val obsColDes: JsonDeserializer<ObservableCollection<*>> = JsonDeserializer { t, type, ctx ->
            DelegatingObservableList(
                (t as JsonArray).map { ctx.deserialize<Any>(it, (type as ParameterizedType).actualTypeArguments[0]) }
            )
        }
        val obsSetDes: JsonDeserializer<ObservableSet<*>> = JsonDeserializer { t, type, ctx ->
            DelegatingObservableSet(
                (t as JsonArray).map { ctx.deserialize<Any>(it, (type as ParameterizedType).actualTypeArguments[0]) }
                    .toSet()
            )
        }

        this.gson = GsonBuilder()
            .registerTypeAdapter(SimpleLongProperty::class.java, longSer)
            .registerTypeAdapter(SimpleLongProperty::class.java, longDes)
            .registerTypeAdapter(ObservableList::class.java, obsDes)
            .registerTypeAdapter(ObservableCollection::class.java, obsColDes)
            .registerTypeAdapter(ObservableSet::class.java, obsSetDes)
            .create()
    }

    override fun <T> toObject(string: String, clazz: Class<T>): T = gson.fromJson(string, clazz)

    override fun fromObject(obj: Any): String = gson.toJson(obj)

}
