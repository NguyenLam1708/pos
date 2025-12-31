package com.example.pos.security;

import com.example.pos.reponsitory.UserRoleRepository;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomIdentityMapper implements SecurityIdentityAugmentor {

    @Inject
    UserRoleRepository userRoleRepository;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity,
                                         AuthenticationRequestContext context) {

        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        if (!(identity.getPrincipal() instanceof JWTCallerPrincipal jwt)) {
            return Uni.createFrom().item(identity);
        }

        String userId = jwt.getSubject();

        return userRoleRepository.findRoleCodesByUserId(userId)
                .map(roles -> {

                    AuthenticatedUser user = new AuthenticatedUser(
                            userId,
                            jwt.getClaim("email"),
                            jwt.getClaim("phoneNumber")
                    );

                    QuarkusSecurityIdentity.Builder builder =
                            QuarkusSecurityIdentity.builder(identity)
                                    .setPrincipal(user);

                    roles.forEach(builder::addRole);

                    return builder.build();
                });
    }
}
