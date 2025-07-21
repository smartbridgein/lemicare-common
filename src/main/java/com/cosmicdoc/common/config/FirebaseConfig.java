package com.cosmicdoc.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for initializing the Firebase Admin SDK.
 * This class ensures that the FirebaseApp is initialized only once and
 * provides the Firestore instance as a bean for dependency injection.
 * Moved to common module for reuse across services.
 */
@Configuration
@Profile("!local")
public class FirebaseConfig {

    // Injects the path to the service account key from application.yml
    @Value("${app.firebase.service-account-path}")
    private String serviceAccountPath;

    /**
     * Initializes the Firebase Admin SDK.
     * <p>
     * It checks if an app has already been initialized to prevent errors during
     * hot-reloads or in test environments. This is a critical check for robustness.
     *
     * @return The initialized FirebaseApp instance.
     * @throws IOException if the credentials file cannot be read.
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // Load the service account credentials from the classpath
            InputStream serviceAccount = new ClassPathResource(serviceAccountPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } else {
            // Return the already initialized app
            return FirebaseApp.getInstance();
        }
    }

    /**
     * Provides the Firestore instance as a Spring bean.
     * <p>
     * By defining this as a bean, Spring can automatically inject the Firestore
     * object into any other component (like a repository or service) that needs it.
     * Spring automatically passes the FirebaseApp bean created above as a parameter.
     *
     * @param firebaseApp The initialized FirebaseApp bean.
     * @return The Firestore database instance.
     */
    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
