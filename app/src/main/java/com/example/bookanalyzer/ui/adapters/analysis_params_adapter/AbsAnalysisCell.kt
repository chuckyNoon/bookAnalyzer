package com.example.bookanalyzer.ui.adapters.analysis_params_adapter

sealed class AbsAnalysisCell{
    data class Parameter(val name: String, val value: String) : AbsAnalysisCell()
    data class WordListButton(val buttonText:String) : AbsAnalysisCell()
}
