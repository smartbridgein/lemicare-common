package com.cosmicdoc.common.config;

import com.cosmicdoc.common.repository.BranchRepository;
import com.cosmicdoc.common.repository.OrganizationRepository;
import com.cosmicdoc.common.repository.UsersRepository;
import com.cosmicdoc.common.repository.impl.*;
import com.cosmicdoc.common.util.JsonDataLoader;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Repository configuration class that provides repository beans.
 * Moved to common module for reuse across services.
 */
@Configuration
public class RepositoryConfig {
    @Bean
    public JsonDataLoader jsonDataLoader() {
        return new JsonDataLoader();
    }

    @Bean
    public UsersRepository usersRepository(Firestore firestore) {
        return new UsersRepositoryImpl(firestore);
    }
    
    @Bean
    public OrganizationRepository organizationRepository(Firestore firestore) {
        return new OrganizationRepositoryImpl(firestore);
    }
    
    @Bean
    public BranchRepository branchRepository(Firestore firestore) {
        return new BranchRepositoryImpl(firestore);
    }

    @Bean
    public OrganizationMemberRepositoryImpl organizationMemberRepository(Firestore firestore) {
        return new OrganizationMemberRepositoryImpl(firestore);
    }
    
    @Bean
    public PasswordResetTokenRepositoryImpl passwordResetTokenRepository(Firestore firestore) {
        return new PasswordResetTokenRepositoryImpl(firestore);
    }
    
    @Bean
    public VerificationTokenRepositoryImpl verificationTokenRepository(Firestore firestore) {
        return new VerificationTokenRepositoryImpl(firestore);
    }
}
