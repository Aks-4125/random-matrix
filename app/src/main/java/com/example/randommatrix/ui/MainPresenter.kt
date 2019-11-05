package com.example.randommatrix.ui

import android.graphics.Color
import android.util.Log
import com.example.randommatrix.BaseActivity.Companion.LOGGER
import com.example.randommatrix.MatrixModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.Predicate
import io.realm.Realm
import java.util.*

class MainPresenter(mView: MainContractor.IMainView) : MainContractor.IMainPresenter {
    override fun incrementAndRefresh(
        mList: MutableList<MatrixModel>,
        mNumbers: MutableList<Int>
    ) {
        Observable.fromIterable(mList)
            .filter(Predicate { t -> t.color == 0 })
            .toFlowable(BackpressureStrategy.BUFFER)
            .any { t ->
                val rnd = Random()
                val num = (1..99).random()
                val mExistingItem = Observable.just(mNumbers)
                    .contains(num)
                    .blockingGet()
                mExistingItem?.let {
                    val unqNum = (1..99).random()
                    t.number = unqNum
                    mNumbers.add(unqNum)
                }
                mNumbers.add(num)
                t.number = num
                t.color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                return@any true
            }.blockingGet()

        view.updateListItems(mList,mNumbers)
        persistData(mList)
    }

    private var view: MainContractor.IMainView = mView

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

    override fun persistData(matrixList: MutableList<MatrixModel>) {

        Realm.getDefaultInstance().let { realm ->
            realm.executeTransaction { transaction ->
                transaction.deleteAll()
                transaction.insertOrUpdate(matrixList)
            }
        }
        view.updateMatrix()
    }
}