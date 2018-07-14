package br.com.nglauber.tdcapp.domain.interactor

abstract class UseCase<T> {

    protected var onNext = { _: T -> }
    protected var onError = { _: Throwable -> }
    protected var onComplete = {}

    fun onNext(onNext: (T) -> Unit) : UseCase<T> {
        this.onNext = onNext
        return this
    }

    fun onError(onError: (Throwable) -> Unit) : UseCase<T> {
        this.onError = onError
        return this
    }

    fun onComplete(onComplete: () -> Unit) : UseCase<T> {
        this.onComplete = onComplete
        return this
    }
}