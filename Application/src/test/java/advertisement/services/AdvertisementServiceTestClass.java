package advertisement.services;

import advertisement.AdvertisementFilter;
import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.CategoryEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.AdvertisementNotFoundException;
import advertisement.exceptions.notfound.CategoryNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.exceptions.other.AdvertisementIllegalEditException;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IAdvertisementModelToEntityMapper;
import advertisement.models.Advertisement;
import advertisement.models.Category;
import advertisement.models.User;
import advertisement.services.implementations.AdvertisementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTestClass {
    @Mock
    private IFileManager fileManager;
    @Mock
    private IAdvertisementDAO advertisementDAO;
    @Mock
    private IAdvertisementModelToEntityMapper advertisementModelToEntityMapper;
    @Mock
    private IUserDAO userDAO;
    @Mock
    private ICategoryDAO categoryDAO;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AdvertisementService advertisementService;

    private UserEntity userEntity;
    private User user;
    private CategoryEntity electronicsEntity;
    private CategoryEntity booksEntity;
    private Category electronics;
    private AdvertisementEntity advertisementEntity;
    private Advertisement advertisement;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("ngusev341@mail.ru")
                .password("12345678")
                .username("nikita")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .roles(Set.of("ROLE_USER"))
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .login("ngusev341@mail.ru")
                .password("encodedPassword")
                .username("nikita")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .avatarLink("oldAvatar.png")
                .build();

        electronicsEntity = CategoryEntity.builder().id(10L).name("Электроника").build();
        booksEntity = CategoryEntity.builder().id(20L).name("Книги").build();
        electronics = Category.builder().name("Электроника").build();

        advertisementEntity = AdvertisementEntity.builder()
                .id(100L)
                .user(userEntity)
                .title("Phone")
                .description("Good phone")
                .previewLink("oldPreview.png")
                .price(500.0)
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .paid(false)
                .closed(false)
                .categories(new ArrayList<>())
                .build();

        advertisement = Advertisement.builder()
                .adNumber(100L)
                .user(user)
                .title("Phone")
                .description("Good phone")
                .price(500.0)
                .country("Russia")
                .region("Moscow")
                .town("Moscow")
                .categories(List.of(electronics))
                .build();
    }

    // ---------- getAdvertisements ----------

    @Test
    void getAdvertisementsSuccessfully() {
        //Given
        AdvertisementFilter filter = new AdvertisementFilter("Phone", null, List.of());

        Advertisement closedHighRated = Advertisement.builder()
                .adNumber(1L).title("Phone").closed(true).paid(true)
                .user(User.builder().sellerRating(5.0).build()).build();
        Advertisement openPaidHighRated = Advertisement.builder()
                .adNumber(2L).title("Phone").closed(false).paid(true)
                .user(User.builder().sellerRating(5.0).build()).build();
        Advertisement openUnpaidHighRated = Advertisement.builder()
                .adNumber(3L).title("Phone").closed(false).paid(false)
                .user(User.builder().sellerRating(4.0).build()).build();
        Advertisement openPaidLowRated = Advertisement.builder()
                .adNumber(4L).title("Phone").closed(false).paid(true)
                .user(User.builder().sellerRating(2.0).build()).build();

        List<Advertisement> unsorted = new ArrayList<>(
                List.of(closedHighRated, openUnpaidHighRated, openPaidLowRated, openPaidHighRated));

        //When
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));
        when(advertisementDAO.findWithFilter("Phone", null, List.of())).thenReturn(List.of());
        when(advertisementModelToEntityMapper.toModelList(any())).thenReturn(unsorted);

        //Then
        List<Advertisement> result = advertisementService.getAdvertisements(filter);

        assertEquals(4, result.size());
        assertEquals(2L, result.get(0).getAdNumber());
        assertEquals(4L, result.get(1).getAdNumber());
        assertEquals(3L, result.get(2).getAdNumber());
        assertEquals(1L, result.get(3).getAdNumber());
    }

    @Test
    void getAdvertisementsCategoryNotFoundException() {
        //Given
        AdvertisementFilter filter = new AdvertisementFilter(null, null, List.of("Unknown"));

        //When
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));

        //Then
        assertThrows(CategoryNotFoundException.class,
                () -> advertisementService.getAdvertisements(filter));

        verify(advertisementDAO, never()).findWithFilter(any(), any(), any());
    }

    // ---------- getSalesHistory ----------

    @Test
    void getSalesHistorySuccessfully() {
        //Given
        AdvertisementEntity closedAd = AdvertisementEntity.builder().id(1L).closed(true).build();
        AdvertisementEntity openAd = AdvertisementEntity.builder().id(2L).closed(false).build();

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L))
                .thenReturn(List.of(closedAd, openAd));

        List<Advertisement> closedModels = List.of(
                Advertisement.builder().adNumber(1L).closed(true).build());
        when(advertisementModelToEntityMapper.toModelList(any())).thenReturn(closedModels);

        //Then
        List<Advertisement> result = advertisementService.getSalesHistory("ngusev341@mail.ru");

        ArgumentCaptor<List<AdvertisementEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(advertisementModelToEntityMapper).toModelList(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertTrue(captor.getValue().get(0).isClosed());
        assertEquals(closedModels, result);
    }

    @Test
    void getSalesHistoryUserNotFoundException() {
        //Given
        user.setLogin("kdsjvkjsdv@mail.ru");
        advertisement.setUser(user);
        //When
        when(userDAO.findByLogin("kdsjvkjsdv@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> advertisementService.getSalesHistory("kdsjvkjsdv@mail.ru"));

        verify(advertisementDAO, never()).findAdvertisementsByUserId(anyLong());
    }

    // ---------- addAdvertisement ----------

    @Test
    void addAdvertisementSuccessfully() throws IOException {
        //Given

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));
        when(fileManager.savePreview(multipartFile)).thenReturn("newPreview.png");
        when(advertisementModelToEntityMapper.toEntity(advertisement)).thenReturn(advertisementEntity);
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));
        when(advertisementDAO.save(advertisementEntity)).thenReturn(advertisementEntity);
        when(advertisementModelToEntityMapper.toModel(advertisementEntity)).thenReturn(advertisement);

        //Then
        Advertisement result = advertisementService.addAdvertisement(advertisement, multipartFile);

        assertEquals(advertisement, result);
        assertEquals("newPreview.png", advertisement.getPreviewLink());
        assertEquals(userEntity, advertisementEntity.getUser());
        assertTrue(advertisementEntity.getCategories().contains(electronicsEntity));
        verify(advertisementDAO).save(advertisementEntity);
    }

    @Test
    void addAdvertisementUserNotFoundException() {
        //Given
        user.setLogin("kdsjvkjsdv@mail.ru");
        advertisement.setUser(user);

        //When
        when(userDAO.findByLogin("kdsjvkjsdv@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> advertisementService.addAdvertisement(advertisement, multipartFile));

        verify(advertisementDAO, never()).save(any());
    }

    @Test
    void addAdvertisementCategoryNotFoundException() throws IOException {
        //Given
        Advertisement adWithUnknownCategory = Advertisement.builder()
                .user(user)
                .title("Phone")
                .categories(List.of(Category.builder().name("Unknown").build()))
                .build();

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));
        when(fileManager.savePreview(multipartFile)).thenReturn("newPreview.png");
        when(advertisementModelToEntityMapper.toEntity(adWithUnknownCategory)).thenReturn(advertisementEntity);
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));

        //Then
        assertThrows(CategoryNotFoundException.class,
                () -> advertisementService.addAdvertisement(adWithUnknownCategory, multipartFile));

        verify(advertisementDAO, never()).save(any());
    }

    // ---------- editAdvertisement ----------

    @Test
    void editAdvertisementSuccessfully() throws IOException {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L)).thenReturn(List.of(advertisementEntity));
        when(advertisementModelToEntityMapper.toModelList(any()))
                .thenReturn(List.of(Advertisement.builder().user(user).build()));
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));
        when(fileManager.savePreview(multipartFile)).thenReturn("newPreview.png");
        when(advertisementModelToEntityMapper.toModel(advertisementEntity)).thenReturn(advertisement);

        //Then
        Advertisement result = advertisementService.editAdvertisement(advertisement, multipartFile);

        assertEquals(advertisement, result);
        assertEquals("Phone", advertisementEntity.getTitle());
        assertEquals("Good phone", advertisementEntity.getDescription());
        assertEquals(500.0, advertisementEntity.getPrice());
        assertEquals("newPreview.png", advertisementEntity.getPreviewLink());
        assertTrue(advertisementEntity.getCategories().contains(electronicsEntity));
        verify(fileManager).deleteOldPreview("oldPreview.png");
        verify(advertisementDAO).update(advertisementEntity);
    }

    @Test
    void editAdvertisementAdvertisementNotFoundException() {
        //Given
        advertisement.setAdNumber(101L);

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.editAdvertisement(advertisement, multipartFile));

        verify(advertisementDAO, never()).update(any());
    }

    @Test
    void editAdvertisementAdvertisementIllegalEditException() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L)).thenReturn(List.of(advertisementEntity));
        when(advertisementModelToEntityMapper.toModelList(any()))
                .thenReturn(List.of(Advertisement.builder()
                        .user(User.builder().login("somelogin@mail.ru").build())
                        .build()));

        //Then
        assertThrows(AdvertisementIllegalEditException.class,
                () -> advertisementService.editAdvertisement(advertisement, multipartFile));

        verify(advertisementDAO, never()).update(any());
    }

    @Test
    void editAdvertisementCategoryNotFoundException() {
        //Given
        Advertisement adWithUnknownCategory = Advertisement.builder()
                .adNumber(100L)
                .user(user)
                .title("Phone")
                .categories(List.of(Category.builder().name("Unknown").build()))
                .build();

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L)).thenReturn(List.of(advertisementEntity));
        when(advertisementModelToEntityMapper.toModelList(any()))
                .thenReturn(List.of(Advertisement.builder().user(user).build()));
        when(categoryDAO.findAll()).thenReturn(List.of(electronicsEntity, booksEntity));

        //Then
        assertThrows(CategoryNotFoundException.class,
                () -> advertisementService.editAdvertisement(adWithUnknownCategory, multipartFile));

        verify(advertisementDAO, never()).update(any());
    }

    // ---------- prepayAdvertisement ----------

    @Test
    void prepayAdvertisementSuccessfully() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));

        //Then
        advertisementService.prepayAdvertisement(100L);

        assertTrue(advertisementEntity.isPaid());
        verify(advertisementDAO).update(advertisementEntity);
    }

    @Test
    void prepayAdvertisementAdvertisementNotFoundException() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.prepayAdvertisement(101L));

        verify(advertisementDAO, never()).update(any());
    }

    // ---------- closeAdvertisement ----------

    @Test
    void closeAdvertisementSuccessfully() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L)).thenReturn(List.of(advertisementEntity));
        when(advertisementModelToEntityMapper.toModelList(any()))
                .thenReturn(List.of(Advertisement.builder().user(user).build()));

        //Then
        advertisementService.closeAdvertisement(100L, "ngusev341@mail.ru");

        assertTrue(advertisementEntity.isClosed());
        verify(advertisementDAO).update(advertisementEntity);
    }

    @Test
    void closeAdvertisementAdvertisementNotFoundException() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.closeAdvertisement(101L, "ngusev341@mail.ru"));

        verify(advertisementDAO, never()).update(any());
    }

    @Test
    void closeAdvertisementAdvertisementIllegalEditException() {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));
        when(advertisementDAO.findAdvertisementsByUserId(1L)).thenReturn(List.of(advertisementEntity));
        when(advertisementModelToEntityMapper.toModelList(any()))
                .thenReturn(List.of(Advertisement.builder()
                        .user(User.builder().login("otherUser").build())
                        .build()));

        //Then
        assertThrows(AdvertisementIllegalEditException.class,
                () -> advertisementService.closeAdvertisement(100L, "ngusev341@mail.ru"));

        verify(advertisementDAO, never()).update(any());
    }

    // ---------- deleteAdvertisement ----------

    @Test
    void deleteAdvertisementSuccessfully() throws IOException {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(100L)).thenReturn(Optional.of(advertisementEntity));

        //Then
        advertisementService.deleteAdvertisement(100L);

        verify(advertisementDAO).delete(advertisementEntity);
        verify(fileManager).deleteOldPreview("oldPreview.png");
    }

    @Test
    void deleteAdvertisementAdvertisementNotFoundException() throws IOException {
        //Given

        //When
        when(advertisementDAO.findByAdNumber(101L)).thenReturn(Optional.empty());

        //Then
        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.deleteAdvertisement(101L));

        verify(advertisementDAO, never()).delete(any());
        verify(fileManager, never()).deleteOldPreview(anyString());
    }
}