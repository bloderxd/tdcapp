package br.com.nglauber.tdcapp.domain.interactor

import br.com.nglauber.tdcapp.domain.executor.PostExecutionThread
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

abstract class CompletableUseCase<in Params> constructor(
        private val postExecutionThread: PostExecutionThread
) : UseCase<Unit>() {

    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseCompletable(params: Params? = null): Completable

    open fun execute(params: Params? = null, handling: UseCase<Unit>.() -> Any? = {}) {
        val completable = this.buildUseCaseCompletable(params)
                .subscribeOn(Schedulers.io())
                .observeOn(postExecutionThread.scheduler)
        addDisposable(completable.subscribeWith(object: DisposableCompletableObserver() {
            override fun onComplete() {
                this@CompletableUseCase.onComplete()
            }

            override fun onError(e: Throwable) {
                this@CompletableUseCase.onError(e)
            }
        }))
    }

    fun dispose() {
        disposables.dispose()
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
}