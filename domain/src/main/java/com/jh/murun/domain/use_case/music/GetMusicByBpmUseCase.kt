package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicByBpmUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {
    operator fun invoke(bpm: Int) = getMusicRepository.fetchMusicByBpm(bpm)
}