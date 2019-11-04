package com.example.randommatrix.ui

import com.example.randommatrix.MatrixModel

interface MainContractor {

    public interface IMainView {
       public abstract fun updateMatrix(mutableList: MutableList<MatrixModel>) : Any
    }

    interface IMainPresenter {
        public abstract  fun persistData(mutableList: MutableList<MatrixModel>) : Any
    }

}