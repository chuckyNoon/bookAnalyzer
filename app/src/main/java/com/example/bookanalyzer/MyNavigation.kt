package com.example.bookanalyzer

import com.example.bookanalyzer.ui.fragments.ResultFragmentExtra

sealed class MyNavigation{
    class ToResultFragment(val extra: ResultFragmentExtra) : MyNavigation()
    class ToWordsFragment(val analysisId: Int) : MyNavigation()
    class ToProcessFragment(val bookPath:String) : MyNavigation()
    class Exit() : MyNavigation()
}