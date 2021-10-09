package cz.encircled.fswing.observable

import cz.encircled.fswing.observable.collection.DelegatingObservableList
import cz.encircled.fswing.observable.collection.DelegatingObservableSet

fun <T> observableSet() = DelegatingObservableSet<T>()

fun <T> observableSet(vararg items: T) = DelegatingObservableSet<T>(items.toSet())

fun <T> observableList() = DelegatingObservableList<T>()

fun <T> observableList(vararg items: T) = DelegatingObservableList<T>(items.toSet())
