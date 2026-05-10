package advertisement.services;

import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.ICommentDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.CommentEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.AdvertisementNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.ICommentModelToEntityMapper;
import advertisement.models.Advertisement;
import advertisement.models.Comment;
import advertisement.models.User;
import advertisement.services.implementations.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTestClass {
    @Mock
    private ICommentDAO commentDAO;
    @Mock
    private ICommentModelToEntityMapper commentModelToEntityMapper;
    @Mock
    private IAdvertisementDAO advertisementDAO;
    @Mock
    private IUserDAO userDAO;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private UserEntity userEntity;
    private Advertisement advertisement;
    private AdvertisementEntity advertisementEntity;
    private Comment comment;
    private CommentEntity commentEntity;

    @BeforeEach
    void setUp() {
        user = User.builder().login("ngusev341@mail.ru").build();
        userEntity = UserEntity.builder().id(1L).login("ngusev341@mail.ru").build();

        advertisement = Advertisement.builder().adNumber(100L).build();
        advertisementEntity = AdvertisementEntity.builder()
                .id(100L)
                .comments(List.of())
                .build();

        comment = Comment.builder()
                .advertisement(advertisement)
                .user(user)
                .content("Nice ad!")
                .build();

        commentEntity = CommentEntity.builder()
                .id(1L)
                .content("Nice ad!")
                .build();
    }

    // ---------- addComment ----------

    @Test
    void addCommentSuccessfully() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));
        when(commentModelToEntityMapper.toEntity(comment)).thenReturn(commentEntity);
        when(commentModelToEntityMapper.toModel(commentEntity)).thenReturn(comment);

        //Then
        Comment result = commentService.addComment(comment);

        assertEquals(comment, result);
        assertEquals(userEntity, commentEntity.getUser());
        assertEquals(advertisementEntity, commentEntity.getAdvertisement());
        assertNotNull(commentEntity.getSentAt());
        verify(commentDAO).save(commentEntity);
    }

    @Test
    void addCommentAdvertisementNotFoundException() {
        //Given
        advertisement.setAdNumber(101L);

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> commentService.addComment(comment));

        verify(commentDAO, never()).save(any());
    }

    @Test
    void addCommentUserNotFoundException() {
        //Given
        user.setLogin("adsfasdgsrfbddfv@mail.ru");

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(userDAO.findByLogin("adsfasdgsrfbddfv@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> commentService.addComment(comment));

        verify(commentDAO, never()).save(any());
    }

    // ---------- getAdvertisementComments ----------

    @Test
    void getAdvertisementCommentsSuccessfully() {
        //Given
        List<CommentEntity> commentEntities = List.of(commentEntity);
        AdvertisementEntity adWithComments = AdvertisementEntity.builder()
                .id(100L)
                .comments(commentEntities)
                .build();
        List<Comment> expected = List.of(comment);

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(adWithComments));
        when(commentModelToEntityMapper.toModelList(commentEntities)).thenReturn(expected);

        //Then
        List<Comment> result = commentService.getAdvertisementComments(100L);

        assertEquals(expected, result);
    }

    @Test
    void getAdvertisementCommentsAdvertisementNotFoundException() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> commentService.getAdvertisementComments(101L));

        verify(commentModelToEntityMapper, never()).toModelList(any());
    }
}