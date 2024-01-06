package com.jh.murun

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PresentationTest : BehaviorSpec({
    Given("리스트의 순서를 변경한다") {
        val list = listOf(0, 1, 2, 3)

        When("1번과 3번을 변경한다") {
            val first = list[1]
            val third = list[3]

            val reorderedList = list.toIntArray().also {
                it[1] = third
                it[3] = first
            }.toList()

            Then("결과") {
                reorderedList shouldBe listOf(0, 3, 2, 1)
            }

            Then("값 기반 소팅") {
                reorderedList.sorted() shouldBe listOf(0, 1, 2, 3)
            }
        }
    }
})