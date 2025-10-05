package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Users;
import com.cosmicdoc.common.model.UserStatus;
import com.cosmicdoc.common.repository.UsersRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class UsersRepositoryImpl extends AbstractTransactionalRepositoryImpl<Users,String> implements UsersRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
   public UsersRepositoryImpl (Firestore firestore) {
        super(firestore);
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

    /*@Override
    public Optional<Users> findByEmail(String email) {
        try {
            logger.debug("Attempting to find user by email: {}", email);

            // Construct the query to find documents where the 'email' field matches
            var query = getCollection().whereEqualTo("email", email);

            // Execute the query and wait for results
            var querySnapshot = query.get().get();

            // Check if any documents were returned
            if (querySnapshot.isEmpty()) {
                logger.debug("No user found with email: {}", email);
                return Optional.empty();
            }

            // In a well-designed system, email should be unique, so we expect at most one document.
            // We retrieve the first matching document.
            QueryDocumentSnapshot document = querySnapshot.getDocuments().get(0);
            logger.debug("Found user document with ID: {}", document.getId());

            // --- The core: Use toObject() for automatic mapping ---
            Users user = document.toObject(Users.class);

            // --- Post-processing (optional, for defensive programming against inconsistent data) ---
            // These checks ensure that if a field is somehow missing or null in Firestore
            // (despite your Java model expecting it), the Java object has sensible defaults.
            // Firestore's toObject() will set primitive fields (like boolean) to their default
            // values (false) and object fields (like String, List, Enum) to null if the field
            // is missing in the document.

            if (user.getStatus() == null) {
                logger.warn("User {} has no status set in Firestore, defaulting to PENDING_VERIFICATION.", user.getUserId());
                user.setStatus(UserStatus.PENDING_VERIFICATION);
            }
            if (user.getOrganizations() == null) {
                logger.warn("User {} has no organizations list in Firestore, defaulting to empty list.", user.getUserId());
                user.setOrganizations(Collections.emptyList()); // Always prefer emptyList() over null list
            }
            // Add similar defensive checks for other fields (e.g., displayName, mobileNumber)
            // if they are critical and might be missing in some Firestore documents.

            logger.info("Successfully retrieved and mapped user with ID: {}", user.getUserId());
            return Optional.of(user);

        } catch (InterruptedException | ExecutionException e) {
            // Handle Firestore specific exceptions
            logger.error("Firestore operation failed while finding user by email: {}", email, e);
            throw new RuntimeException("Failed to retrieve user by email from Firestore.", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions (e.g., during toObject() mapping if data is truly bad)
            logger.error("An unexpected error occurred while processing user data for email: {}", email, e);
            throw new RuntimeException("An error occurred during user data processing.", e);
        }
    }*/

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

