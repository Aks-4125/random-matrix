package com.example.randommatrix.ui

import com.example.randommatrix.MatrixModel

interface MainContractor {

    interface IMainView {
        fun updateMatrix(mutableList: MutableList<MatrixModel>): Any
    }

    interface IMainPresenter {
        fun persistData(matrixList: MutableList<MatrixModel>): Any
        fun fetchData(): MutableList<MatrixModel>
    }

}