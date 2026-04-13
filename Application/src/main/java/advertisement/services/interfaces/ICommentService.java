package advertisement.services.interfaces;

import advertisement.models.Comment;

import java.util.List;

public interface ICommentService {
    Comment addComment(Comment comment);
    List<Comment> getAdvertisementComments(Long adNumber);
}
