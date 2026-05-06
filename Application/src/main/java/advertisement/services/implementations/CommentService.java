package advertisement.services.implementations;

import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.ICommentDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.CommentEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.AdvertisementNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.ICommentModelToEntityMapper;
import advertisement.models.Comment;
import advertisement.services.interfaces.ICommentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements ICommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private ICommentDAO commentDAO;
    @Autowired
    private ICommentModelToEntityMapper commentModelToEntityMapper;
    @Autowired
    private IAdvertisementDAO advertisementDAO;
    @Autowired
    private IUserDAO userDAO;

    @Override
    @Transactional
    public Comment addComment(Comment comment) {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO
                .findByAdNumber(comment.getAdvertisement().getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(comment.getUser().getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

        CommentEntity commentEntity = commentModelToEntityMapper.toEntity(comment);
        commentEntity.setUser(userEntity);
        commentEntity.setAdvertisement(advertisementEntity);

        Instant instant = Instant.now();
        commentEntity.setSentAt(instant);
        commentDAO.save(commentEntity);

        logger.info("Комментарий успешно добавлен.");
        return commentModelToEntityMapper.toModel(commentEntity);
    }

    @Override
    @Transactional
    public List<Comment> getAdvertisementComments(Long adNumber) {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO
                .findByAdNumber(adNumber);
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });

        logger.info("Комментарии успешно получены.");
        return commentModelToEntityMapper.toModelList(advertisementEntity.getComments());
    }
}