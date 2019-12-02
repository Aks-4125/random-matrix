package com.example.randommatrix.ui

import android.graphics.Color
import android.util.Log
import com.example.randommatrix.base.BaseActivity.Companion.LOGGER
import com.example.randommatrix.data.MatrixModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.*

@Suppress("RedundantSamConstructor")
class MainPresenter(mView: MainContractor.IMainView) : MainContractor.IMainPresenter {

    private var view: MainContractor.IMainView = mView

    init {
        Log.d(LOGGER, "Presenter initialized")
    }

    /**
     * increment in background thread and update ui on main thread
     */
    override fun incrementAndRefresh(mList: MutableList<MatrixModel>, mNumbers: MutableList<Int>) {
        val rnd = Random()
        Observable.fromIterable(mList)
            .subscribeOn(Schedulers.io())
            .filter(Predicate { t -> t.color == 0 })
            .toFlowable(BackpressureStrategy.LATEST)
            .any { matrixBlock ->
                var num = (1..499).random()
                val mExistingItem = Observable.fromIterable(mList)
                    .filter(Predicate { t -> t.number == num })
                    .firstElement()
                    .blockingGet()
                mExistingItem?.let {
                    val unqNum = (500..999).random()
                    num = unqNum
                }
                mNumbers.add(num)
                matrixBlock.number = num
                matrixBlock.color =
                    Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                return@any true
            }.subscribe()


        view.updateListItems(mList, mNumbers)
        persistData(mList)
    }


    /**
     * fetch items from database
     */
    override fun fetchData(): MutableList<MatrixModel> {
        Realm.getDefaultInstance().let { realm ->

            val isEmpty = realm.where(MatrixModel::class.java).findAll().isEmpty()
            return if (isEmpty) mutableListOf()
            else
                realm.copyFromRealm(
                    realm.where(MatrixModel::class.java)
                        .findAll()
                )
        }

    }


    /**
     * insert or update existing record to persist data
     */
    override fun persistData(matrixList: MutableList<MatrixModel>) {

        Realm.getDefaultInstance().let { realm ->
            realm.executeTransaction { transaction ->
                transaction.deleteAll()
                transaction.insertOrUpdate(matrixList)
            }
        }
        view.updateMatrix()
    }

    /**
     * populate default matrix with white background
     */
    override fun generateDefaultMatrix(
        mList: MutableList<MatrixModel>,
        mUniqueNumbers: MutableList<Int>,
        size: Int
    ) {
        Observable.just(mList)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                for (i in 1..size * size) {
                    val mItem = MatrixModel()
                    var num = (1..499).random()
                    val mExistingItem = Observable.fromIterable(mList)
                        .filter(Predicate { t -> t.number == num })
                        .firstElement()
                        .blockingGet()
                    mExistingItem?.let {
                        val unqNum = (500..999).random()
                        num = unqNum
                    }
                    mUniqueNumbers.add(num)
                    mItem.number = num
                    mItem.pos = size
                    mList.add(mItem)
                }
            }.blockingSubscribe()
        view.updateMatrix()
    }
}