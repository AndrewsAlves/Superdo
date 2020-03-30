package com.andysapps.superdo.todo.events

import com.andysapps.superdo.todo.model.Bucket

/**
 * Created by Admin on 13,November,2019
 */

class DeleteBucketEvent (var bucket : Bucket, var isPositive : Boolean)