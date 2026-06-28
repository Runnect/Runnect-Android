package com.runnect.runnect.presentation.countdown

import com.runnect.runnect.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CountDownStateMachineTest {

    @Test
    fun `카운트다운은 3에서 시작한다`() {
        assertEquals(3, CountDownStateMachine.INITIAL_COUNT)
    }

    @Test
    fun `카운트다운은 3_2_1 순서로 진행되고 이후 종료된다`() {
        assertEquals(2, CountDownStateMachine.nextCount(3))
        assertEquals(1, CountDownStateMachine.nextCount(2))
        assertNull(CountDownStateMachine.nextCount(1))
    }

    @Test
    fun `카운트다운 숫자에 맞는 drawable을 반환한다`() {
        assertEquals(R.drawable.anim_num3, CountDownStateMachine.numberDrawableRes(3))
        assertEquals(R.drawable.anim_num2, CountDownStateMachine.numberDrawableRes(2))
        assertEquals(R.drawable.anim_num1, CountDownStateMachine.numberDrawableRes(1))
    }
}
