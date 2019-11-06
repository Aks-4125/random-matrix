package com.example.randommatrix.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.randommatrix.R
import com.example.randommatrix.adapter.MatrixAdapter
import com.example.randommatrix.base.BaseActivity
import com.example.randommatrix.data.MatrixModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * View interface (no business logic) only user interaction is handled
 */
class MainActivity : BaseActivity(), MainContractor.IMainView {
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>
    private lateinit var spinnerArray: Array<String>
    private lateinit var matrixAdapter: MatrixAdapter
    private var isPersist: Boolean = false
    private var mSelectedPos = 3
    private var mList = mutableListOf<MatrixModel>()
    private var mUniqueNumbers = mutableListOf<Int>()
    private lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
        loadSpinner()
        val dataList = mPresenter.fetchData()
        if (!dataList.isEmpty()) {
            mList.addAll(dataList)
            Log.d(LOGGER, "retrieved from database")
            mList[0].pos?.let { mSelectedPos = spinnerArrayAdapter.getPosition(it.toString()) }
            isPersist = true
        }
        vSpinner.setSelection(mSelectedPos)
    }

    /**
     * populate spinner ui item
     */
    private fun loadSpinner() {
        vSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val size = spinnerArray[position].toInt()
                val gridLayout =
                    GridLayoutManager(this@MainActivity, size)
                rvMatrix.layoutManager = gridLayout
                mUniqueNumbers.clear()

                if (!isPersist) {
                    mList.clear()
                    mPresenter.generateDefaultMatrix(mList, mUniqueNumbers, size)
                }
                isPersist = false
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                /*not required*/
            }
        }
    }

    /**
     * Initialize ui
     */
    private fun initializeUI() {
        spinnerArray = arrayOf("2", "3", "4", "5", "6", "7", "8")
        spinnerArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
        //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vSpinner.adapter = spinnerArrayAdapter
        mPresenter = MainPresenter(this)
        matrixAdapter = MatrixAdapter(mList)
        rvMatrix.adapter = matrixAdapter
        matrixAdapter.onItemClick = { mAdapterList ->
            mPresenter.persistData(mAdapterList)
        }
        btnGenerate.setOnClickListener {
            mPresenter.incrementAndRefresh(mList, mUniqueNumbers)
            Log.d(LOGGER, "list updated")
        }
    }

    override fun updateListItems(
        matrixList: MutableList<MatrixModel>,
        mNumbers: MutableList<Int>
    ) {
        mList = matrixList
        mUniqueNumbers = mNumbers
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun updateMatrix() {
        rvMatrix.adapter!!.notifyDataSetChanged()
    }

}
