package com.andysapps.superdo.todo.events.firestore

import com.andysapps.superdo.todo.model.Bucket
import com.google.firebase.firestore.DocumentChange


/**
 * Created by Andrews on 14,November,2019
 */
class BucketUpdatedEvent (var documentChange: DocumentChange.Type?, var bucket : Bucket?)