package cz.encircled.fswing.observable

import cz.encircled.fswing.observable.collection.DelegatingObservableList
import cz.encircled.fswing.observable.collection.DelegatingObservableSet
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.observable.collection.ObservableSet

fun <T> observableSet(): ObservableSet<T> = DelegatingObservableSet()

fun <T> observableSet(vararg items: T): ObservableSet<T> = DelegatingObservableSet(items.toSet())

fun <T> observableList(): ObservableCollection<T> = DelegatingObservableList()

fun <T> observableList(vararg items: T): ObservableCollection<T> = DelegatingObservableList(items.toList())

fun <T> observableList(items: List<T>): ObservableCollection<T> = DelegatingObservableList(items)
