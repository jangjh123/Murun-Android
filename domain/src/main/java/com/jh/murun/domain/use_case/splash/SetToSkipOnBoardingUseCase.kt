package com.jh.murun.domain.use_case.splash

import com.jh.murun.domain.repository.SplashRepository
import javax.inject.Inject

class SetToSkipOnBoardingUseCase @Inject constructor(
    private val splashRepository: SplashRepository
){
    suspend operator fun invoke() = splashRepository.setToSkipOnBoarding()
}