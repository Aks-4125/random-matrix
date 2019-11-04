package com.example.randommatrix.ui

import android.util.Log
import com.example.randommatrix.BaseActivity.Companion.LOGGER
import com.example.randommatrix.MatrixModel
import io.realm.Realm

class MainPresenter : MainContractor.IMainPresenter {
    override fun fetchData(): MutableList<MatrixModel> {
        Realm.getDefaultInstance().let { realm ->

            val isEmpty = realm.where(MatrixModel::class.java).findAll().isEmpty()
            if (isEmpty) return mutableListOf()
            else
                return realm.copyFromRealm(
                    realm.where(MatrixModel::class.java)
                        .findAll()
                )
        }

    }

    init {
        Log.d(LOGGER, "Presenter initialized")
    }

    override fun persistData(matrixList: MutableList<MatrixModel>): Any {

        Realm.getDefaultInstance().let { realm ->
            realm.executeTransaction { transaction ->
                transaction.deleteAll()
                transaction.insertOrUpdate(matrixList)
            }
        }

        return matrixList
    }
}