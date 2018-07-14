package br.com.nglauber.tdcapp.presentation

import androidx.lifecycle.*
import br.com.nglauber.tdcapp.domain.interactor.modality.GetModalitiesByEvent
import br.com.nglauber.tdcapp.domain.model.Modality
import br.com.nglauber.tdcapp.presentation.mapper.ModalityMapper
import br.com.nglauber.tdcapp.presentation.model.ModalityBinding

class ModalityListViewModel(
        private val getModalitiesByEvent: GetModalitiesByEvent,
        private val mapper: ModalityMapper

) : ViewModel(), LifecycleObserver {

    var eventId: Long = 0
    private val state: MutableLiveData<ViewState<Map<String, List<ModalityBinding>>>> = MutableLiveData()

    fun getState(): LiveData<ViewState<Map<String, List<ModalityBinding>>>> {
        return state
    }

    fun fetchModalities(eventId: Long) {
        state.postValue(ViewState(ViewState.Status.LOADING))
        getModalitiesByEvent.execute(eventId) {
            onNext { modalityList ->
                val modalitiesGroupedByDate = modalityList
                        .map { mapper.fromDomain(it) }
                        .sortedWith(compareBy({ it.date }, { it.positionOnEvent }))
                        .groupBy { it.date }
                state.postValue(ViewState(ViewState.Status.SUCCESS, modalitiesGroupedByDate))
            }
            onError { e -> state.postValue(ViewState(ViewState.Status.ERROR, error = e)) }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchIfNeeded() {
        if (state.value == null) {
            fetchModalities(eventId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getModalitiesByEvent.dispose()
    }
}