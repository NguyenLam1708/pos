package com.example.pos.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomIdentityMapper implements SecurityIdentityAugmentor {

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity,
                                         AuthenticationRequestContext context) {

        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        if (!(identity.getPrincipal() instanceof JWTCallerPrincipal jwt)) {
            return Uni.createFrom().item(identity);
        }

        AuthenticatedUser user = new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaim("email"),
                jwt.getClaim("phoneNumber")
        );

        return Uni.createFrom().item(
                QuarkusSecurityIdentity.builder(identity)
                        .setPrincipal(user)
                        .build()
        );
    }
}


