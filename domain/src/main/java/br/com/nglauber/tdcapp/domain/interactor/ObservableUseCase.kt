package br.com.nglauber.tdcapp.domain.interactor

import br.com.nglauber.tdcapp.domain.executor.PostExecutionThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

abstract class ObservableUseCase<T, in Params> constructor(
        private val postExecutionThread: PostExecutionThread
) : UseCase<T>() {

    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseObservable(params: Params? = null): Observable<T>

    open fun execute(
            params: Params? = null,
            handling: UseCase<T>.() -> Any? = {}) {
        val observable = this.buildUseCaseObservable(params)
                .subscribeOn(Schedulers.io())
                .observeOn(postExecutionThread.scheduler)
        addDisposable(observable.subscribeWith(object : DisposableObserver<T>() {
            override fun onComplete() {
                this@ObservableUseCase.onComplete()
            }

            override fun onNext(t: T) {
                this@ObservableUseCase.onNext(t)
            }

            override fun onError(e: Throwable) {
                this@ObservableUseCase.onError(e)
            }
        }))
    }

    fun dispose() {
        disposables.clear()
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
}