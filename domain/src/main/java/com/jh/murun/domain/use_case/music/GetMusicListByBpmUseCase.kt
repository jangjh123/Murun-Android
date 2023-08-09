package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicListByBpmUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {
    suspend operator fun invoke(bpm: Int) = getMusicRepository.fetchMusicListByBpm(bpm = bpm)
}