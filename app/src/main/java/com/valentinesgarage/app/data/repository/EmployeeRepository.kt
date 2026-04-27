package com.valentinesgarage.app.data.repository

import com.valentinesgarage.app.data.local.dao.EmployeeDao
import com.valentinesgarage.app.data.local.entity.EmployeeEntity
import com.valentinesgarage.app.domain.model.Employee
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Bridges the Room employee table to the domain layer.
 */
class EmployeeRepository(private val dao: EmployeeDao) {

    fun observeAll(): Flow<List<Employee>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeById(id: Long): Flow<Employee?> =
        dao.observeById(id).map { it?.toDomain() }

    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun findById(id: Long): Employee? = dao.findById(id)?.toDomain()

    suspend fun findByUsername(username: String): Employee? =
        dao.findByUsername(username.trim())?.toDomain()

    suspend fun isUsernameTaken(username: String): Boolean =
        dao.findByUsername(username.trim()) != null

    suspend fun register(
        username: String,
        name: String,
        password: String,
        role: EmployeeRole,
        email: String,
        phone: String,
        nowMillis: Long,
    ): Employee {
        val salt = PasswordHasher.newSalt()
        val entity = EmployeeEntity(
            username = username.trim().lowercase(),
            name = name.trim(),
            role = role.name,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            email = email.trim(),
            phone = phone.trim(),
            createdAt = nowMillis,
            isActive = true,
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    suspend fun authenticate(username: String, password: String): Employee? {
        val row = dao.findByUsername(username.trim()) ?: return null
        if (!row.isActive) return null
        if (!PasswordHasher.verify(password, row.passwordSalt, row.passwordHash)) return null
        return row.toDomain()
    }

    suspend fun updatePassword(employeeId: Long, currentPassword: String, newPassword: String): Boolean {
        val row = dao.findById(employeeId) ?: return false
        if (!PasswordHasher.verify(currentPassword, row.passwordSalt, row.passwordHash)) return false
        val salt = PasswordHasher.newSalt()
        dao.update(
            row.copy(
                passwordHash = PasswordHasher.hash(newPassword, salt),
                passwordSalt = salt,
            )
        )
        return true
    }

    suspend fun updateProfile(
        employeeId: Long,
        name: String,
        email: String,
        phone: String,
    ): Employee? {
        val row = dao.findById(employeeId) ?: return null
        val updated = row.copy(name = name.trim(), email = email.trim(), phone = phone.trim())
        dao.update(updated)
        return updated.toDomain()
    }
}
