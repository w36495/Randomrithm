package com.w36495.randomrithm.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.randomrithm.domain.entity.Problem
import com.w36495.randomrithm.domain.entity.Tag
import com.w36495.randomrithm.domain.usecase.GetProblemUseCase
import kotlinx.coroutines.launch

class ProblemViewModel(
    private val getProblemUseCase: GetProblemUseCase
) : ViewModel() {
    private var _problem = MutableLiveData<Problem>()
    val problem: LiveData<Problem>
        get() = _problem

    fun getProblem(problemId: Int) {
        viewModelScope.launch {
            try {
                val result = getProblemUseCase.fetchProblem(problemId)

                if (result.isSuccessful) {
                    result.body()?.let { it ->
                        val tags = mutableListOf<Tag>()
                        it.tags.forEach {
                            tags.add(Tag(it.bojTagId, it.key, it.displayNames[0].name, it.problemCount))
                        }

                        _problem.value = Problem(
                            id = it.problemId,
                            level = it.level.toString(),
                            title = it.titleKo,
                            tags = tags.sortedBy { it.name }
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.d("ProblemViewModel", exception.printStackTrace().toString())
            }
        }
    }
}