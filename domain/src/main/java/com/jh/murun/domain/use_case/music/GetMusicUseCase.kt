package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {

    suspend operator fun invoke(bpm: Int) = getMusicRepository.getMusicList(bpm = bpm)
}