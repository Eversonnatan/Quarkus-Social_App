package io.github.eversonnatan.quarkussocial.rest;
import io.github.eversonnatan.quarkussocial.domain.model.Follower;
import io.github.eversonnatan.quarkussocial.domain.model.Post;
import io.github.eversonnatan.quarkussocial.domain.model.User;
import io.github.eversonnatan.quarkussocial.domain.repository.FollowerRepository;
import io.github.eversonnatan.quarkussocial.domain.repository.PostRepository;
import io.github.eversonnatan.quarkussocial.domain.repository.UserRepository;
import io.github.eversonnatan.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public  void setUP(){
      //usuario padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Astrogildo");
        userRepository.persist(user);
        userId = user.getId();

        //criada a postagem para o usuario
        Post post =new Post();
        post.setText("Hello World");
        post.setUser(user);
        postRepository.persist(post);

      //usuario que não segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Tiburcio");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuario seguidor
        var userFollower = new User();
        userFollower.setAge(45);
        userFollower.setName("Cicrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);


    }

    @Test
    @DisplayName("should create a post for a user")
    public void creatPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId",userId)
        .when()
                .post()
        .then()
                .statusCode(201);

    }

    @Test
    @DisplayName("should return 404 shen trying to make a post an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();

        postRequest.setText("some text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);

    }
        @Test
        @DisplayName("should return 404 user doesn't exist")
        public void listPostUserNotFoundTest(){
            var inexistentUserId = 999;

            given()
                    .pathParam("userId", inexistentUserId)
            .when()
                    .get()
            .then()
                    .statusCode(404);


        }
        @Test
        @DisplayName("should return 400 when followerId header is not present")
        public void listPostFollowerHeaderNotSendTest(){

            given()
                    .pathParam("userId" , userId)
            .when()
                    .get()
            .then()
                    .statusCode(400)
                    .body(Matchers.is("You forgot the header followerId"));

        }

        @Test
        @DisplayName("should return 404 when follower doesn't exist")
        public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 999;

        given()
                .pathParam("userId",userId)
                .header("followerId",inexistentFollowerId)
       .when()
                .get()
       .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));


        }

        @Test
        @DisplayName("should return 403 whe follower isn't a follower")
        public void listPostNotAFollower(){
            given()
                    .pathParam("userId", userId)
                    .header("followerId", userNotFollowerId)
            .when()
                    .get()
            .then()
                    .statusCode(403)
                    .body(Matchers.is("You can't see these posts"));


        }

        @Test
        @DisplayName("should return possts")
        public void listPostsTest(){
            given()
                    .pathParam("userId",userId)
                    .header("followerId",userFollowerId)
            .when()
                    .get()
            .then()
                    .statusCode(200)
                    .body("size()",Matchers.is(1));

        }
}

















