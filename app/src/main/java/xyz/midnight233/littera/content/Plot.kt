package xyz.midnight233.littera.content

import xyz.midnight233.littera.persist.Profile
import kotlin.reflect.KProperty

class Plot(val segment: Segment) {
    private lateinit var propName: String

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Plot {
        propName = property.name
        return this
    }

    var plotFinished by Profile
        .scene(segment::class.qualifiedName!!)
        .markDeferred { "Plot(${propName}):Finished" }

    private var condition: PredicateScope.() -> Boolean = { true }

    val finished get() = plotFinished
    val active get() = !finished && condition(ContentScope)

    fun finish() {
        plotFinished = true
    }

    fun need(pred: PredicateScope.() -> Boolean) {
        condition = pred
    }

    infix fun after(other: Plot) = need { other.finished }.let { other }
    infix fun before(other: Plot) = other.need { finished }.let { other }
    fun requireAll(vararg others: Plot) = need { others.all { it.finished } }
    fun requireAny(vararg others: Plot) = need { others.any { it.finished } }

    val requireActive: PredicateScope.() -> Boolean = { this@Plot.active }
}