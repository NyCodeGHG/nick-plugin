package de.nycode.nickplugin.database

/**
 * Abstraction of a database source for usage of different databases
 */
interface DatabaseProvider {

    /**
     * Connect to the specific database
     */
    fun connect()

    /**
     * Close the connection to the database
     */
    fun close()

    /**
     * The name of the provider used in the config.yml configuration file
     */
    val providerName: String

}