package br.com.nglauber.tdcapp.presentation

import androidx.lifecycle.*
import br.com.nglauber.tdcapp.domain.interactor.session.GetSessionsByModality
import br.com.nglauber.tdcapp.presentation.mapper.SessionMapper
import br.com.nglauber.tdcapp.presentation.model.SessionBinding

class SessionListViewModel (
        private val getSessions: GetSessionsByModality,
        private val mapper: SessionMapper
) : ViewModel(), LifecycleObserver {
    var eventId: Long = 0
    var modalityId: Long = 0

    private val state: MutableLiveData<ViewState<List<SessionBinding>>> = MutableLiveData()

    fun getState(): LiveData<ViewState<List<SessionBinding>>> {
        return state
    }

    fun fetchSessionsByModality(eventId: Long, modalityId: Long) {
        this.eventId = eventId
        this.modalityId = modalityId

        state.postValue(ViewState(ViewState.Status.LOADING))

        getSessions.execute(GetSessionsByModality.Params(eventId, modalityId)) {
            onNext { sessionList ->
                val list = sessionList.map { mapper.fromDomain(it) }
                state.postValue(ViewState(ViewState.Status.SUCCESS, list))
            }
            onError { state.postValue(ViewState(ViewState.Status.ERROR, error = it)) }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchIfNeeded() {
        if (state.value == null) {
            fetchSessionsByModality(eventId, modalityId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getSessions.dispose()
    }
}