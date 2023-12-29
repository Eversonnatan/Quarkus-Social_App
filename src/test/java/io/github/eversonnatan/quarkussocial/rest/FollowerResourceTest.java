package io.github.eversonnatan.quarkussocial.rest;

import io.github.eversonnatan.quarkussocial.domain.model.Follower;
import io.github.eversonnatan.quarkussocial.domain.model.User;
import io.github.eversonnatan.quarkussocial.domain.repository.FollowerRepository;
import io.github.eversonnatan.quarkussocial.domain.repository.UserRepository;
import io.github.eversonnatan.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {


    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
    var user = new User();
    user.setAge(30);
    user.setName("Fulano");
    userRepository.persist(user);
    userId = user.getId();
    // seguidor
    var follower = new User();
        follower.setAge(76);
        follower.setName("Kratos");
    userRepository.persist(follower);
    followerId = follower.getId();

    //criar um follower
     var followerEntity = new Follower();
     followerEntity.setFollower(follower);
     followerEntity.setUser(user);
     followerRepository.persist(followerEntity);



    }
    @Test
    @DisplayName("should return 409 shen follower Id is equal to User id")
    public void sameUserAsfollowertest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId)
         .when()
                .put()
         .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));

    }
    @Test
    @DisplayName("should return 404 on follow a user shen User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId =999;
         given()
                 .contentType(ContentType.JSON)
                 .body(body)
                 .pathParam("userId",inexistentUserId)
        .when()
                 .put()
         .then()
                 .statusCode(Response.Status.NOT_FOUND.getStatusCode());


    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);


        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId )
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }
    @Test
    @DisplayName("should return 404 an list user followers and User id doen't exist")
    public void userNotFoundWhenListingFollowersTet(){

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
       .when()
                .get()
       .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }
@Test
@DisplayName("should list a user's followers")
public void listFollowersTest(){
        var response =
        given()
                .pathParam("userId",userId)
        .when()
                .get()
        .then()
                .extract().response();

       var followersCount = response.jsonPath().get("followersCount");
       var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals( 1, followersCount);
        assertEquals(1,followersContent.size());

        }
        @Test
        @DisplayName("should return  404on unfollow user and User id doen't exist")
        public void  userNotFoundWhenUnfollowingAuserTest(){
            var inexistentUserId = 999;

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId",inexistentUserId)
                    .queryParam("followerId",followerId)
            .when()
                    .delete()
            .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());

        }
        @Test
        @DisplayName("should unfollow an user")
        public  void  unfollowUserTest(){
        given()
                .pathParam("userId",userId)
                .queryParam("followerId",followerId)
         .when()
                .delete()
          .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        }


   }
















