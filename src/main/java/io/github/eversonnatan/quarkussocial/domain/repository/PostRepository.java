package io.github.eversonnatan.quarkussocial.domain.repository;

import io.github.eversonnatan.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository  implements PanacheRepository<Post> {
}
