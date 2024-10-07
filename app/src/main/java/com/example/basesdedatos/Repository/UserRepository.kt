package com.example.basesdedatos.Repository

import com.example.basesdedatos.DAO.UserDao
import com.example.basesdedatos.Model.User

class UserRepository(private val userDao: UserDao){

    suspend fun insert(user: User){
        userDao.insert(user)
    }

    suspend fun getAllUsers():List<User>{
        return userDao.getAllUsers()
    }

    suspend fun deleteById(userId: Int): Int{
        return userDao.deleteById(userId)
    }

    suspend fun updateById(userId: Int, nombre: String, apellido: String, edad: Int){
        userDao.updateById(userId, nombre, apellido, edad)
    }


}