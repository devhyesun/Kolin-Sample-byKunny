package com.devhyesun.kolinsample.extensions

import com.devhyesun.kolinsample.AutoClearedDisposable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) = this.add(disposable)