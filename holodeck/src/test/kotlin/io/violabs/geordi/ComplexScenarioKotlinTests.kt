package io.violabs.geordi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestTemplate
import io.violabs.geordi.common.with

private val SHARED_MAP_SIMULATION = SimulationGroup
    .vars("scenario",    "username",     "userId", "accountId")
    .with("Deanna Troi", "imzadi2336",   "1",      123)
    .with("Data",        "sp0t",         "2",      456)
    .with("Wesley",      "shutupWesley", "3",      789)

class ComplexScenarioKotlinTests : UnitSim() {
    private val userRepository = mock<UserRepository>()
    private val accountClient = mock<AccountClient>()

    private val userService = UserService(userRepository, accountClient)

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() = setup<ComplexScenarioKotlinTests>(
            SHARED_MAP_SIMULATION with { ::`show a mockable service with shared map - #scenario` },
            SHARED_MAP_SIMULATION with { ::`show a mockable service shared scope - #scenario` },
        )
    }

    @TestTemplate
    fun `show a mockable service with shared map - #scenario`(username: String, userId: String, accountId: Int) = test {
        setup {
            this["user"] = User(username)
            this["resultUser"] = User(username, userId)
            this["account"] = Account(accountId)
        }

        expect {
            it["resultUser"].coax<User>()?.copy(account = it["account"].force<Account>())
        }

        setupMocks {
            every { userRepository.findUserByName(username) } returns it["resultUser"].force()
            every { accountClient.getAccountByClientId(userId) } returns it["account"].force()
            every { userRepository.saveUser(it.expected!!) } returns it.expected!!
        }

        whenever { userService.updateUser(it["user"].force<User>()) }
    }

    @TestTemplate
    fun `show a mockable service shared scope - #scenario`(username: String, userId: String, accountId: Int) = test {
        // setup
        val user = User(username)
        val resultUser = user.copy(id = userId)
        val account = Account(accountId)

        expect {
            resultUser.copy(account = account)
        }

        setupMocks {
            every { userRepository.findUserByName(username) } returns resultUser
            every { accountClient.getAccountByClientId(userId) } returns account
            every { userRepository.saveUser(it.expected!!) } returns it.expected!!
        }

        whenever { userService.updateUser(user) }
    }
}