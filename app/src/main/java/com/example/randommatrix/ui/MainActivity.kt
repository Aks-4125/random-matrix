package com.example.randommatrix.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.randommatrix.BaseActivity
import com.example.randommatrix.MatrixAdapter
import com.example.randommatrix.MatrixModel
import com.example.randommatrix.R
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.Predicate
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(), MainContractor.IMainView {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun updateMatrix(mutableList: MutableList<MatrixModel>): Any {


        return Unit // not required as of now
    }

    private var persist: Boolean = false
    private var SPINNER_POS = 3;
    private val mList = mutableListOf<MatrixModel>()
    private val mUniqueNumbers = mutableListOf<Int>()
    lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val spinnerArray = arrayOf("2", "3", "4", "5", "6", "7", "8")
        val spinnerArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArray)
        //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vSpinner.adapter = spinnerArrayAdapter

        mainPresenter = MainPresenter()


        val matrixAdapter = MatrixAdapter(mList);
        rvMatrix.adapter = matrixAdapter
        matrixAdapter.onItemClick = { mAdapterList ->
            mainPresenter.persistData(mAdapterList)
        }

        vSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val size = spinnerArray[position].toInt();
                val gridLayout =
                    GridLayoutManager(this@MainActivity, size)
                rvMatrix.layoutManager = gridLayout

                if (!persist) {
                    mList.clear()
                    mUniqueNumbers.clear()

                    for (i in 1..size * size) {
                        val mItem = MatrixModel();
                        val num = (1..99).random();
                        val mExistingItem = Observable.just(mUniqueNumbers)
                            .contains(num)
                            .blockingGet()
                        mExistingItem?.let {
                            val unqNum = (1..99).random()
                            mItem.number = unqNum
                            mUniqueNumbers.add(unqNum)
                        }
                        mUniqueNumbers.add(num)
                        mItem.pos = size
                        mList.add(mItem)
                    }
                }
                matrixAdapter.notifyDataSetChanged()
                persist = false
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                /*not required*/
            }
        }

        val dataList = mainPresenter.fetchData()
        if (!dataList.isEmpty()) {
            mList.addAll(dataList)
            Log.d(LOGGER, "retrieved from database")
            mList[0].pos?.let { SPINNER_POS = spinnerArrayAdapter.getPosition(it.toString()) }
            persist = true;
        }

        vSpinner.setSelection(SPINNER_POS)

        btnGenerate.setOnClickListener {
            Observable.fromIterable(mList)
                .filter(Predicate { t -> t.color == 0 })
                .toFlowable(BackpressureStrategy.BUFFER)
                .any { t ->
                    val rnd = Random()
                    val num = (1..99).random()
                    val mExistingItem = Observable.just(mUniqueNumbers)
                        .contains(num)
                        .blockingGet()
                    mExistingItem?.let {
                        val unqNum = (1..99).random()
                        t.number = unqNum
                        mUniqueNumbers.add(unqNum)
                    }
                    mUniqueNumbers.add(num)
                    t.number = num
                    t.color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                    return@any true
                }.blockingGet()
            mainPresenter.persistData(mList)
            rvMatrix.adapter!!.notifyDataSetChanged()
            Log.d(LOGGER, "list updated")
        }

    }

}
