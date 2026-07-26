package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.data.DefaultStreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.TagEntity
import javax.inject.Inject

class DefaultGetTags @Inject constructor (
    private val repository: DefaultStreamerRepository
){
    suspend operator fun invoke(): Result<List<TagEntity>>{
        return repository.getTags()
    }
}