package io.violabs.geordi

import org.junit.jupiter.api.Test

class ComplexKotlinTests : UnitSim() {
    private val userRepository = mock<UserRepository>()
    private val accountClient = mock<AccountClient>()

    private val userService = UserService(userRepository, accountClient)

    @Test
    fun `show a mockable service with shared map`() = test {
        setup {
            this["user"] = User("test")
            this["resultUser"] = User("test", "1")
            this["account"] = Account(1)
        }

        expect {
            it["resultUser"].coax<User>()?.copy(account = Account(1))
        }

        setupMocks {
            every { userRepository.findUserByName("test") } returns it["resultUser"].force()
            every { accountClient.getAccountByClientId("1") } returns it["account"].force()
            every { userRepository.saveUser(it.expected!!) } returns it.expected!!
        }

        whenever { userService.updateUser(it["user"].force<User>()) }
    }

    @Test
    fun `show a mockable service shared scope`() = test {
        // setup
        val user = User("test")
        val resultUser = user.copy(id = "1")
        val account = Account(1)

        expect {
            resultUser.copy(id = "1", account = Account(1))
        }

        setupMocks {
            every { userRepository.findUserByName("test") } returns resultUser
            every { accountClient.getAccountByClientId("1") } returns account
            every { userRepository.saveUser(it.expected!!) } returns it.expected!!
        }

        whenever { userService.updateUser(user) }
    }
}