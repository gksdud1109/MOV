package com.movie.curator.global.response

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


object DelegateUtil {
    /**
     * 값을 참조할 때마다 값을 새로 평가합니다.
     */
    fun <T> reference(valueProvider: () -> T): ReadOnlyProperty<Any?, T> =
        ReadOnlyProperty<Any?, T> { _, _ -> valueProvider() }

    /**
     * null을 허용하지 않는 변수의 기본값을 지정합니다.
     *
     * type-safe builder 등에서 사용자가 null을 입력하게 하고 싶지 않지만 입력하지 않으면 기본값을 지정하고 싶을 때 사용합니다.
     */
    fun <T : Any> notNullWithDefault(defaultValueLazyProvider: () -> T): ReadWriteProperty<Any?, T> =
        NotNullWithDefaultVar(defaultValueLazyProvider)
}

private class NotNullWithDefaultVar<T : Any>(val defaultValueLazyProvider: () -> T) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: defaultValueLazyProvider()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun toString(): String =
        "NotNullWithDefaultProperty(value=${value ?: defaultValueLazyProvider()})"
}