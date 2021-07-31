package com.datn.cookingguideapp.domain.model

/**
 * A class of types whose instances hold the value of an entity with stable identity.
 */
interface Identifiable<T> {
    /**
     * The stable identity of the entity associated with this instance.
     */
    val id: T

    fun isTheSameAs(other: Identifiable<T>) = id == other.id

    override operator fun equals(other: Any?): Boolean
}