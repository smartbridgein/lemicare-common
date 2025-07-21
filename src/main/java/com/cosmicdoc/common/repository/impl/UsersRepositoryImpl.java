package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Users;
import com.cosmicdoc.common.model.UserStatus;
import com.cosmicdoc.common.repository.UsersRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UsersRepositoryImpl extends BaseRepositoryImpl<Users,String> implements UsersRepository {

   public UsersRepositoryImpl (Firestore firestore) {
        super();
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("users");
    }


    @Override
    public Optional<Users> findByEmail(String email) {
        try {
            System.out.println("DEBUG: Starting findByEmail with email: " + email);
            
            // First check if the email is in the database
            var query = getCollection().whereEqualTo("email", email);
            System.out.println("DEBUG: Created Firestore query");
            
            var future = query.get();
            System.out.println("DEBUG: Got future from query");
            
            var querySnapshot = future.get();
            System.out.println("DEBUG: Query executed, results: " + querySnapshot.size());
            
            var documents = querySnapshot.getDocuments();
            System.out.println("DEBUG: Got document list with size: " + documents.size());
            
            if (!documents.isEmpty()) {
                System.out.println("DEBUG: Found user document by email");
                
                // Manual conversion to avoid type mismatch errors
                var doc = documents.get(0);
                System.out.println("DEBUG: Document ID: " + doc.getId());
                
                var userData = doc.getData();
                System.out.println("DEBUG: Document data keys: " + String.join(", ", userData.keySet()));
                
                Users user = new Users();
                user.setUserId(doc.getId());
                System.out.println("DEBUG: Set userId: " + doc.getId());
                
                // Email
                if (userData.get("email") != null) {
                    user.setEmail((String) userData.get("email"));
                    System.out.println("DEBUG: Set email: " + userData.get("email"));
                } else {
                    System.out.println("DEBUG: No email field in document");
                }
                
                // Hashed Password - try multiple possible field names
                if (userData.get("hashedPassword") != null) {
                    user.setHashedPassword((String) userData.get("hashedPassword"));
                    System.out.println("DEBUG: Password hash present as 'hashedPassword'");
                } else if (userData.get("password_hash") != null) {
                    user.setHashedPassword((String) userData.get("password_hash"));
                    System.out.println("DEBUG: Password hash present as 'password_hash'");
                } else if (userData.get("passwordHash") != null) {
                    user.setHashedPassword((String) userData.get("passwordHash"));
                    System.out.println("DEBUG: Password hash present as 'passwordHash'");
                } else {
                    System.out.println("DEBUG: No password hash field found in document under any known name");
                }
                
                // Display Name
                if (userData.get("displayName") != null) {
                    user.setDisplayName((String) userData.get("displayName"));
                    System.out.println("DEBUG: Set displayName: " + userData.get("displayName"));
                }
                
                // Mobile
                if (userData.get("mobileNumber") != null) {
                    user.setMobileNumber((String) userData.get("mobileNumber"));
                    System.out.println("DEBUG: Set mobileNumber: " + userData.get("mobileNumber"));
                }
                
                // Handle status enum
                if (userData.get("status") != null) {
                    String statusStr = userData.get("status").toString();
                    System.out.println("DEBUG: Raw status value: " + statusStr);
                    
                    try {
                        user.setStatus(UserStatus.valueOf(statusStr));
                        System.out.println("DEBUG: Set status to: " + statusStr);
                    } catch (IllegalArgumentException ex) {
                        System.out.println("DEBUG: Invalid status value, defaulting to PENDING_VERIFICATION");
                        user.setStatus(UserStatus.PENDING_VERIFICATION); 
                    }
                } else {
                    System.out.println("DEBUG: No status field in document, defaulting to PENDING_VERIFICATION");
                    user.setStatus(UserStatus.PENDING_VERIFICATION);
                }
                
                // Handle organizations list
                if (userData.get("organizations") != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        List<String> orgs = (List<String>) userData.get("organizations");
                        user.setOrganizations(orgs);
                        System.out.println("DEBUG: Set organizations: " + orgs.size() + " orgs");
                    } catch (ClassCastException e) {
                        System.out.println("DEBUG: Error casting organizations field: " + e.getMessage());
                        user.setOrganizations(List.of());
                    }
                } else {
                    System.out.println("DEBUG: No organizations field in document");
                    user.setOrganizations(List.of()); // Empty list
                }
                
                System.out.println("DEBUG: Successfully mapped user data");
                return Optional.of(user);
            }
            
            System.out.println("DEBUG: No user found with email: " + email);
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in findByEmail: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    @Override
    public Optional<Users> findByPhone(String mobileNumber) {
        try {
            System.out.println("DEBUG: Starting findByPhone with mobile: " + mobileNumber);
            
            // First check if the mobile number is in the database
            var query = getCollection().whereEqualTo("mobileNumber", mobileNumber);
            System.out.println("DEBUG: Created Firestore query for mobile number");
            
            try {
                var future = query.get();
                System.out.println("DEBUG: Got future from query");
                
                // Add explicit timeout of 15 seconds to avoid blocking indefinitely
                var querySnapshot = future.get(15, java.util.concurrent.TimeUnit.SECONDS);
                System.out.println("DEBUG: Query executed, results: " + querySnapshot.size());
                
                var documents = querySnapshot.getDocuments();
                System.out.println("DEBUG: Got document list with size: " + documents.size());
                
                if (!documents.isEmpty()) {
                    System.out.println("DEBUG: Found user document by mobile");
                    return Optional.ofNullable(documents.get(0).toObject(Users.class));
                }
            } catch (java.util.concurrent.TimeoutException e) {
                System.err.println("DEBUG: Timeout when querying Firestore: " + e.getMessage());
                throw new RuntimeException("Timeout when finding user by mobile number", e);
            } catch (com.google.api.gax.rpc.DeadlineExceededException e) {
                System.err.println("DEBUG: Deadline exceeded when querying Firestore: " + e.getMessage());
                throw new RuntimeException("Connection deadline exceeded when finding user by mobile number", e);
            }
            
            System.out.println("DEBUG: No user found with mobile number: " + mobileNumber);
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in findByPhone: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error finding user by mobile number", e);
        }
    }

    @Override
    public void saveInTransaction(WriteBatch batch, Users user) {
        var docRef = getCollection().document(user.getUserId());
        batch.set(docRef, user);
    }

    @Override
    public void updateLastLogin(String userId) {
        try {
            // Get a reference to the specific user document.
            var docRef = getCollection().document(userId);

            // Create a map which we don't
            // need to wait for, making the sign-in process faster.
            docRef.update("lastLoginAt", new Timestamp(System.currentTimeMillis()));

        } catch (Exception e) {
            throw new RuntimeException("Error while update the timestamp");
        }
    }
  }

