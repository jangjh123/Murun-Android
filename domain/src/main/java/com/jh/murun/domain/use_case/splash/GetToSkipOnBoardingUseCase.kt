package com.jh.murun.domain.use_case.splash

import com.jh.murun.domain.repository.SplashRepository
import javax.inject.Inject

class GetToSkipOnBoardingUseCase @Inject constructor(
    private val splashRepository: SplashRepository
){
    operator fun invoke() = splashRepository.getToSkipOnBoarding()
}