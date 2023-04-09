package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicInfoByIdUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
){

    suspend operator fun invoke(id: String) = getMusicRepository.getMusicInfoById(id)
}