package com.example.randommatrix

import io.realm.RealmObject

open class MatrixModel(var number: Int? = 0, var color: Int? = 0) : RealmObject()