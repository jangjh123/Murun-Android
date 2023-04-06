package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicListByCadence @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {

    suspend operator fun invoke(cadence: Int) = getMusicRepository.getMusicList(bpm = cadence)
}