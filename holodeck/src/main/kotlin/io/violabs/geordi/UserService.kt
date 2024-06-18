package io.violabs.geordi

class UserService(
    private val userRepository: UserRepository,
    private val accountClient: AccountClient
) {

    fun updateUser(user: User): User {
        val existingUser = userRepository.findUserByName(user.name) ?: throw Exception("User does not exist")

        existingUser.account = accountClient.getAccountByClientId(existingUser.id)

        takeItIn(anotherMethod())

        return userRepository.saveUser(existingUser)
    }

    fun anotherMethod(): String {
        return "THIS SHOULD NOT BE"
    }

    fun takeItIn(input: String) {
        println("I took in $input")
    }
}