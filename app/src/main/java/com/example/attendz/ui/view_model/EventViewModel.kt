package com.example.attendz.ui.view_model
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.Result


class EventViewModel : ViewModel() {

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _joinedEventList = MutableLiveData<List<Event>>()
    val joinedEventList: LiveData<List<Event>> get() = _joinedEventList

    private val _participantList = MutableLiveData<List<Participant>>()

    val participantList: LiveData<List<Participant>> get() = _participantList

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val firestore: FirebaseFirestore = Firebase.firestore

    fun loadEventList(hostEmail: String?): LiveData<List<Pair<Event, DocumentReference>>>  {
        _loadingState.value = LoadingState.LOADING
        val result = MutableLiveData<List<Pair<Event, DocumentReference>>>()
        firestore.collection("events")
            .whereEqualTo("host_email", hostEmail)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                _loadingState.value = LoadingState.SUCCESS
                val eventsWithRef = mutableListOf<Pair<Event, DocumentReference>>()

                for (document in querySnapshot.documents) {
                    val event = document.toObject(Event::class.java)
                    val documentReference = document.reference

                    if (event != null) {
                        eventsWithRef.add(Pair(event, documentReference))
                    }
                }
                result.value = eventsWithRef

                val events = querySnapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                _eventList.value = events
            }
            .addOnFailureListener { exception ->
                _loadingState.value = LoadingState.FAILURE
            }
        return result
    }
    fun createEvent(event: Event): MutableLiveData<DocumentReference?> {
        val result = MutableLiveData<DocumentReference?>()
        _loadingState.value = LoadingState.LOADING
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener {
                _loadingState.value = LoadingState.SUCCESS
                result.value = it
                // Event uploaded successfully
            }
            .addOnFailureListener { exception ->
                _loadingState.value = LoadingState.FAILURE
                // Handle failure
            }
        return result
    }
    fun getEventById(eventId: String) {
        firestore.collection("events").document(eventId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val event = documentSnapshot.toObject(Event::class.java)
                // Do something with the event
            }
            .addOnFailureListener { exception ->

            }
    }
    fun updateEventField(eventId: String, fieldName: String, updatedValue: Any): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        _loadingState.value = LoadingState.LOADING
        firestore.collection("events").document(eventId)
            .update(fieldName, updatedValue)
            .addOnSuccessListener {
                _loadingState.value = LoadingState.SUCCESS
                result.value = true // Success
            }
            .addOnFailureListener { exception ->
                _loadingState.value = LoadingState.FAILURE
                result.value = false // Failure
            }

        return result
    }
    fun getActiveEvent(hostEmail: String, ended: Boolean): LiveData<List<Pair<Event, DocumentReference>>> {
        val result = MutableLiveData<List<Pair<Event, DocumentReference>>>()

        firestore.collection("events")
            .whereEqualTo("host_email", hostEmail)
            .whereEqualTo("ended", ended)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val events = mutableListOf<Pair<Event, DocumentReference>>()

                for (document in querySnapshot.documents) {
                    val event = document.toObject(Event::class.java)
                    val documentReference = document.reference

                    if (event != null) {
                        events.add(Pair(event, documentReference))
                    }
                }

                result.value = events
            }
            .addOnFailureListener {
                // Handle failure
            }

        return result
    }

    fun joinEvent(data: Participant): MutableLiveData<String?> {
        val result = MutableLiveData<String?>()
        _loadingState.value = LoadingState.LOADING

        data.event_id?.let {
            firestore.collection("events").document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val event = documentSnapshot.toObject(Event::class.java)
                    if (event != null) {
                        if (event.ended == true) {
                            result.value = "The event is already closed."
                        } else {
                            firestore.collection("participants")
                                .whereEqualTo("email", data.email)
                                .whereEqualTo("event_id", data.event_id)
                                .get()
                                .addOnSuccessListener {
                                    if (it.isEmpty) {
                                        firestore.collection("participants")
                                            .add(data)
                                            .addOnSuccessListener {
                                                _loadingState.value = LoadingState.SUCCESS
                                                result.value = "Join Event Successfully"
                                            }
                                            .addOnFailureListener {
                                                _loadingState.value = LoadingState.FAILURE
                                                result.value = "Failed to Join event"
                                            }
                                    } else {
                                        result.value = "You already join the event."
                                    }

                                }
                                .addOnFailureListener {
                                }
                        }
                    }
                }
                .addOnFailureListener {

                }
        }

        return result
    }

    fun getJoinedEventList(email: String?) {
        _loadingState.value = LoadingState.LOADING
        firestore.collection("participants")
            .whereEqualTo("email", email)
            .orderBy("joined_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val participant = document.toObject(Participant::class.java)
                    if (participant != null) {
                        participant.event_id?.let {
                            firestore.collection("events").document(it)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val event = documentSnapshot.toObject(Event::class.java)
                                    if (event != null) {
                                        val currentList = _joinedEventList.value ?: emptyList()
                                        val updatedList = currentList + event
                                        _joinedEventList.value = updatedList
                                    }
                                }
                                .addOnFailureListener {

                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                _loadingState.value = LoadingState.FAILURE
            }
    }
    fun getParticipantsList(eventId: String){
        firestore.collection("participants")
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val participant = querySnapshot.documents.mapNotNull { it.toObject(Participant::class.java) }
                _participantList.value = participant
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}

data class Participant(
    val display_name: String? = null,
    val email: String? = null,
    val event_id: String? = null,
    val joined_at: Timestamp? = null,
)
data class Event(
    val created_at: Timestamp? = null,
    val host_email: String? = null,
    val host_name: String? = null,
    val ended: Boolean? = null,
    val name: String? = null,
)

data class EventWithRef(
    val event: Event,
    val ref: String,
)
enum class LoadingState {
    LOADING,
    SUCCESS,
    FAILURE,
    // Add more states like ERROR if needed
}
