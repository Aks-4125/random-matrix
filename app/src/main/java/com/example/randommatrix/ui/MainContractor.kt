package com.example.randommatrix.ui

import com.example.randommatrix.data.MatrixModel

interface MainContractor {

    interface IMainView {
        fun updateMatrix()

        fun updateListItems(
            matrixList: MutableList<MatrixModel>,
            mNumbers: MutableList<Int>
        )
    }

    interface IMainPresenter {
        fun persistData(matrixList: MutableList<MatrixModel>): Any

        fun fetchData(): MutableList<MatrixModel>

        fun incrementAndRefresh(
            mList: MutableList<MatrixModel>,
            mNumbers: MutableList<Int>
        )

        fun generateDefaultMatrix(
            mList: MutableList<MatrixModel>,
            mUniqueNumbers: MutableList<Int>,
            size: Int
        )

    }

}