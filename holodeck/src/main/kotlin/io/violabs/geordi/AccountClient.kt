package io.violabs.geordi

interface AccountClient {

    fun getAccountByClientId(id: String? = null): Account?
}