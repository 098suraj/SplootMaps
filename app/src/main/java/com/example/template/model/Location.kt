package com.example.template.model

data class LocationModel(val latitude: Double, val longitude: Double){
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}
