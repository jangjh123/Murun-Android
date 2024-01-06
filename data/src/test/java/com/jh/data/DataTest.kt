package com.jh.data

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class DataTest : BehaviorSpec({
    Given("저장된 순서와 값(실제 인덱스)이 다른 리스트가 주어진다") {
        val list = listOf(0, 3, 2, 1)

        When("이를 획득할 때에는 값(실제 인덱스)를 기준으로 정렬한다") {
            val result = list.sorted()

            Then("반환 결과") {
                result shouldBe listOf(0, 1, 2, 3)
            }
        }
    }
})