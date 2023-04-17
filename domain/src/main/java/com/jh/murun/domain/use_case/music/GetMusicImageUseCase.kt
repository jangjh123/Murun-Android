package com.jh.murun.domain.use_case.music

import com.jh.murun.domain.repository.GetMusicRepository
import javax.inject.Inject

class GetMusicImageUseCase @Inject constructor(
    private val getMusicRepository: GetMusicRepository
) {
    suspend operator fun invoke(url: String) = getMusicRepository.fetchMusicImage(url)
}