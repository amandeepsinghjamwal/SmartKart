package com.example.myapplication.api.models

data class ProductDetails(
    val _id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageUrl: String,
    val __v: Int? = null,
    val createdAt: String? = null,
    val updatedAt:String?=null,
    var cartItemId:String?=null,
    var quantity:Int?=null,
    val watchListItemId:String?=null,
    val isFirebaseProduct:Boolean?=null
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