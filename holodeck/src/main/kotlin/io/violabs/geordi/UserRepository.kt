package io.violabs.geordi

interface UserRepository {
    fun findUserByName(name: String): User?
    fun saveUser(user: User): User
}