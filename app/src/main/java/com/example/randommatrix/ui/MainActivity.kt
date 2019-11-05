package com.example.randommatrix.ui

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
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), MainContractor.IMainView {
    override fun updateListItems(
        matrixList: MutableList<MatrixModel>,
        mNumbers: MutableList<Int>
    ) {
        mList =  matrixList
        mUniqueNumbers = mNumbers
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun updateMatrix() {
        rvMatrix.adapter!!.notifyDataSetChanged()
    }

    private var isPersist: Boolean = false
    private var mSelectedPos = 3;
    private var mList = mutableListOf<MatrixModel>()
    private var mUniqueNumbers = mutableListOf<Int>()
    private lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val spinnerArray = arrayOf("2", "3", "4", "5", "6", "7", "8")
        val spinnerArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArray)
        //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vSpinner.adapter = spinnerArrayAdapter

        mPresenter = MainPresenter(this)


        val matrixAdapter = MatrixAdapter(mList);
        rvMatrix.adapter = matrixAdapter
        matrixAdapter.onItemClick = { mAdapterList ->
            mPresenter.persistData(mAdapterList)
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

                if (!isPersist) {
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
                isPersist = false
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                /*not required*/
            }
        }

        val dataList = mPresenter.fetchData()
        if (!dataList.isEmpty()) {
            mList.addAll(dataList)
            Log.d(LOGGER, "retrieved from database")
            mList[0].pos?.let { mSelectedPos = spinnerArrayAdapter.getPosition(it.toString()) }
            isPersist = true;
        }

        vSpinner.setSelection(mSelectedPos)

        btnGenerate.setOnClickListener {
            mPresenter.incrementAndRefresh(mList, mUniqueNumbers)
            Log.d(LOGGER, "list updated")
        }

    }

}
