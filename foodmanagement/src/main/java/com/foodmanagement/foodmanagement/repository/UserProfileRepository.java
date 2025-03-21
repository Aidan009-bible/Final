package com.foodmanagement.foodmanagement.repository;

import com.foodmanagement.foodmanagement.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<Users, Integer> {
    
    // Find user by email
    Optional<Users> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find active users
    List<Users> findByIsEnabled(boolean isEnabled);
    
    // Find by email and enabled status
    @Query("SELECT u FROM Users u WHERE u.email = :email AND u.isEnabled = true")
    Optional<Users> findActiveUserByEmail(@Param("email") String email);
    
    // Update failed login attempts
    @Modifying
    @Query("UPDATE Users u SET u.failedAttempt = :attempt WHERE u.email = :email")
    void updateFailedAttempts(@Param("attempt") int attempt, @Param("email") String email);
    
    // Find users by name containing (for search functionality)
    List<Users> findByNameContainingIgnoreCase(String name);
    
    // Find users by phone number
    Optional<Users> findByPhoneNumber(String phoneNumber);
    
    // Find users by address containing
    List<Users> findByAddressContainingIgnoreCase(String address);
    
    // Custom query to find users with profile image
    @Query("SELECT u FROM Users u WHERE u.profileImage IS NOT NULL")
    List<Users> findUsersWithProfileImage();
    
    // Update user's enabled status
    @Modifying
    @Query("UPDATE Users u SET u.isEnabled = :status WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") Integer userId, @Param("status") boolean status);
    
    // Update user's lock status
    @Modifying
    @Query("UPDATE Users u SET u.accountNonLocked = :status WHERE u.id = :userId")
    void updateAccountLockStatus(@Param("userId") Integer userId, @Param("status") boolean status);
    
    // Delete user by email
    void deleteByEmail(String email);
    
    // Find users created between dates
    @Query("SELECT u FROM Users u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<Users> findUsersCreatedBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
}