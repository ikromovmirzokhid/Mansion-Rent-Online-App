package com.imb.newtask.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imb.newtask.pojos.*
import com.imb.newtask.repository.UserRepository
import com.imb.newtask.room.Database
import com.imb.newtask.room.FavoriteMansion
import com.imb.newtask.room.RecentSearchedMansion
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserViewModel : ViewModel() {
    private var db = Database.getDatabase()

    fun login(username: String, password: String): LiveData<UserLogin> =
        UserRepository.login(username, password)


    fun register(user: UserRegister): LiveData<UserRegister> =
        UserRepository.register(user)


    fun getUserById(token: String, id: Int): LiveData<User> = UserRepository.getUserById(token, id)


    fun updateUserById(token: String, id: Int, user: User): LiveData<User> =
        UserRepository.updateUserById(token, id, user)


    fun getOwnerMansionsById(token: String, id: Int): LiveData<MansionsResult> =
        UserRepository.getOwnerMansionById(token, id)


    fun getRegions(token: String): LiveData<RegionsApiResult> = UserRepository.getRegions(token)


    fun createNewMansion(token: String, newMansion: NewMansion): LiveData<Mansion> =
        UserRepository.createNewMansion(token, newMansion)


    fun updateMansion(token: String, id: Int, newMansion: NewMansion): LiveData<Mansion> =
        UserRepository.updateMansion(token, id, newMansion)


    fun createFacilities(
        token: String,
        newFacilities: NewFacilities
    ): LiveData<NewFacilities> = UserRepository.createFacilities(token, newFacilities)


    fun deleteFacility(token: String, id: Int): LiveData<String> =
        UserRepository.deleteFacility(token, id)


    fun uploadMansionPicture(
        token: String,
        mansionId: RequestBody,
        picture: MultipartBody.Part
    ): LiveData<PictureResult> = UserRepository.uploadMansionPicture(token, mansionId, picture)


    fun filterMansionListByRegionAndRooms(
        token: String,
        regionNum: Int,
        roomsNum: Int
    ): LiveData<MansionsResult> =
        UserRepository.filterMansionsListByRegionAndRooms(token, regionNum, roomsNum)

    fun bookMansion(token: String, bookDetails: BookDetails): LiveData<BookDetailsResult> =
        UserRepository.bookMansion(token, bookDetails)


    fun rateMansionById(token: String, rating: Int, mansionId: Int): LiveData<RatingMansionResult> =
        UserRepository.rateMansionById(token, rating, mansionId)

    fun getAllFavoriteMansions(): LiveData<List<FavoriteMansion>> =
        db.favoriteMansionsDao().getAllFavoriteMansions()


    fun getFavoriteMansionById(id: Int): LiveData<FavoriteMansion> =
        db.favoriteMansionsDao().getMansionById(id)


    fun insertNewFavouriteMansion(mansion: FavoriteMansion) =
        db.favoriteMansionsDao().insertNewFavouriteMansion(mansion)


    fun deleteMansionFromFavoriteDB(mansion: FavoriteMansion): Int =
        db.favoriteMansionsDao().deleteMansionFromFavoriteDB(mansion)


    fun getAllRecentSearchedMansions(): LiveData<List<RecentSearchedMansion>> =
        db.recentSearchedMansionsDao().getAllRecentSearchedMansions()

    fun getRecentSearchedMansionById(id: Int): LiveData<RecentSearchedMansion> =
        db.recentSearchedMansionsDao().getRecentSearchedMansionById(id)

    fun insertRecentSearchedMansion(mansion: RecentSearchedMansion) =
        db.recentSearchedMansionsDao().insertRecentSearchedMansion(mansion)

    fun updateRecentSearchedMansion(mansion: RecentSearchedMansion): Int =
        db.recentSearchedMansionsDao().updateRecentSearchedMansion(mansion)
}