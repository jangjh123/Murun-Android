package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicFileUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {
    suspend operator fun invoke(url: String) = getMusicRepository.fetchMusicFile(url)
}