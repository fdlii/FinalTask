package advertisement.services;

import advertisement.daos.interfaces.IRatingDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.RatingEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.invalid.RatingInvalidException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.IRatingModelToEntityMapper;
import advertisement.models.Rating;
import advertisement.models.User;
import advertisement.services.implementations.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTestClass {
    @Mock
    private IRatingDAO ratingDAO;
    @Mock
    private IUserDAO userDAO;
    @Mock
    private IRatingModelToEntityMapper ratingModelToEntityMapper;

    @InjectMocks
    private RatingService ratingService;

    private User seller;
    private User reviewer;
    private UserEntity sellerEntity;
    private UserEntity reviewerEntity;
    private Rating rating;
    private RatingEntity ratingEntity;

    @BeforeEach
    void setUp() {
        seller = User.builder().login("ngusev341@mail.ru").build();
        reviewer = User.builder().login("ngusev342@mail.ru").build();

        sellerEntity = UserEntity.builder().id(1L).login("ngusev341@mail.ru").sellerRating(0.0).build();
        reviewerEntity = UserEntity.builder().id(2L).login("ngusev342@mail.ru").build();

        rating = Rating.builder()
                .seller(seller)
                .reviewer(reviewer)
                .score(5)
                .comment("Great seller!")
                .build();

        ratingEntity = RatingEntity.builder()
                .id(10L)
                .score(5)
                .comment("Great seller!")
                .build();
    }

    // ---------- getSellerRatings ----------

    @Test
    void getSellerRatingsSuccessfully() {
        //Given
        List<RatingEntity> entities = List.of(ratingEntity);
        List<Rating> expected = List.of(rating);

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(sellerEntity));
        when(ratingDAO.getSellerRatings(1L)).thenReturn(entities);
        when(ratingModelToEntityMapper.toModelList(entities)).thenReturn(expected);

        //Then
        List<Rating> result = ratingService.getSellerRatings("ngusev341@mail.ru");

        assertEquals(expected, result);
    }

    @Test
    void getSellerRatingsUserNotFoundException() {
        //Given

        //When
        when(userDAO.findByLogin("ghuewfhcuweoi@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> ratingService.getSellerRatings("ghuewfhcuweoi@mail.ru"));

        verify(ratingDAO, never()).getSellerRatings(anyLong());
    }

    // ---------- addRating ----------

    @Test
    void addRatingSuccessfully() {
        //Given

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(sellerEntity));
        when(userDAO.findByLogin("ngusev342@mail.ru")).thenReturn(Optional.of(reviewerEntity));
        when(ratingModelToEntityMapper.toEntity(rating)).thenReturn(ratingEntity);
        when(ratingDAO.getSellerRating(1L)).thenReturn(5.0);
        when(ratingModelToEntityMapper.toModel(ratingEntity)).thenReturn(rating);

        //Then
        Rating result = ratingService.addRating(rating);

        assertEquals(rating, result);
        assertEquals(sellerEntity, ratingEntity.getSeller());
        assertEquals(reviewerEntity, ratingEntity.getReviewer());
        assertEquals(5.0, sellerEntity.getSellerRating());
        assertNotNull(ratingEntity.getWrittenAt());
        verify(ratingDAO).save(ratingEntity);
    }

    @Test
    void addRatingUserNotFoundExceptionSeller() {
        //Given
        seller.setLogin("ghuewfhcuweoi@mail.ru");

        //When
        when(userDAO.findByLogin("ghuewfhcuweoi@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> ratingService.addRating(rating));

        verify(ratingDAO, never()).save(any());
    }

    @Test
    void addRatingUserNotFoundExceptionReviewer() {
        //Given
        reviewer.setLogin("ghuewfhcuweoi@mail.ru");

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(sellerEntity));
        when(userDAO.findByLogin("ghuewfhcuweoi@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> ratingService.addRating(rating));

        verify(ratingDAO, never()).save(any());
    }

    @Test
    void addRatingRatingInvalidException() {
        //Given
        Rating selfRating = Rating.builder()
                .seller(seller)
                .reviewer(seller)
                .score(5)
                .comment("Great Seller!")
                .build();

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(sellerEntity));

        //Then
        assertThrows(RatingInvalidException.class,
                () -> ratingService.addRating(selfRating));

        verify(ratingDAO, never()).save(any());
    }
}