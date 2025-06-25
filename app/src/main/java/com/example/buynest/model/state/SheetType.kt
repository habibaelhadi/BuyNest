package com.example.buynest.model.state

sealed class SheetType {
    object None : SheetType()
    object Coupon : SheetType()
    object Address : SheetType()
}
