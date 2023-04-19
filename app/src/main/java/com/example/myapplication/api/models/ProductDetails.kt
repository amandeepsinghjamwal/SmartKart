package com.example.myapplication.api.models

data class ProductDetails(
    val _id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageUrl: String,
    val __v: Int?,
    val createdAt: String?,
    val updatedAt:String?,
    val cartItemId:String?,
    val quantity:Int?,
    val watchListItemId:String?
)
{
    var watchListId:String?=null
    var isWishlisted:Boolean?=false

    var cartID:String?=null
    var isInCart:Boolean?=false
    init {
        isWishlisted = watchListItemId != null

        if(watchListItemId!=null){
            watchListId=watchListItemId
        }

        isInCart=cartItemId!=null
        if(cartItemId!=null){
            cartID=cartItemId
        }
    }

}