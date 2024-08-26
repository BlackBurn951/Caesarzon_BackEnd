package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ReviewRepository;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.caesar.productservice.Data.Services.ReviewService;
import org.caesar.productservice.Dto.AverageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.ReviewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;


    @Override
    public String addReview(ReviewDTO reviewDTO, ProductDTO productDTO) {
        try {
            Review review = reviewRepository.findById(reviewDTO.getId()).orElse(null);

            if(review == null)  //Controllo per l'update
                review = new Review();

            if(reviewDTO.getId()==null && reviewRepository.findReviewByUsernameAndProduct(reviewDTO.getUsername(),modelMapper.map(productDTO, Product.class))!=null)
                return "Limite recensioni raggiunto...";

            review.setProduct(modelMapper.map(productDTO, Product.class));
            review.setDate(LocalDate.now());

            if(checkText(reviewDTO.getText()) && checkEvaluation(reviewDTO.getEvaluation())) {  //Convalida dei dati in arrivo
                review.setText(reviewDTO.getText());
                review.setEvaluation(reviewDTO.getEvaluation());
            }
            else
                return "Problemi nell'aggiunta della recensione...";

            review.setUsername(reviewDTO.getUsername());
            review.setOnChanges(false);

            reviewRepository.save(review);

            return "Recensione aggiunta con successo!";
        } catch (Exception | Error e) {
            log.debug("Errore nell'inserimento della recensione");
            return "Problemi nell'aggiunta della recensione...";
        }
    }

    @Override
    public Review getReviewById(UUID reviewID) {
        return reviewRepository.findById(reviewID).orElse(null);
    }

    @Override
    public ReviewDTO getReviewByUsernameAndProduct(String username, ProductDTO productDTO) {
        try {
            Review review= reviewRepository.findReviewByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(review==null)
                return null;

            return modelMapper.map(review, ReviewDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }


    @Override
    public List<ReviewDTO> getReviewsByProduct(ProductDTO productDTO, int str) {
        return reviewRepository.findAllByproduct(modelMapper.map(productDTO, Product.class), PageRequest.of(str, 10)).stream()
                .map(a -> modelMapper.map(a, ReviewDTO.class))
                .toList();
    }

    @Override
    public boolean validateDeleteReviewById(UUID reviewId, boolean rollback) {
        try {
            Review review= reviewRepository.findById(reviewId).orElse(null);

            if(review==null)
                return false;

            review.setOnChanges(!rollback);

            reviewRepository.save(review);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
        }
    }

    @Override
    public ReviewDTO completeDeleteReviewById(UUID reviewId) {
        try {
            Review review= reviewRepository.findById(reviewId).orElse(null);

            if(review==null)
                return null;

            ReviewDTO result= modelMapper.map(review, ReviewDTO.class);

            review.setDate(null);
            review.setText(null);
            review.setProduct(null);
            review.setEvaluation(0);
            review.setUsername(null);

            reviewRepository.save(review);

            return result;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }


    @Override
    public boolean validateDeleteReview(ReviewDTO reviewDTO, boolean rollback) {
        try {
            Review review= reviewRepository.findById(reviewDTO.getId()).orElse(null);

            if(review==null)
                return false;

            review.setOnChanges(!rollback);

            reviewRepository.save(review);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
        }
    }

    @Override
    public boolean completeDeleteReview(ReviewDTO reviewDTO) {
        try {
            Review review= reviewRepository.findById(reviewDTO.getId()).orElse(null);

            if(review==null)
                return false;

            review.setDate(null);
            review.setText(null);
            review.setProduct(null);
            review.setEvaluation(0);
            review.setUsername(null);

            reviewRepository.save(review);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
        }
    }


    @Override
    public int validateDeleteReviews(String username, boolean rollback) {
        try {
            List<Review> reviews= reviewRepository.findAllByUsername(username);

            if(reviews.isEmpty())
                return 2;

            for(Review review : reviews) {
                review.setOnChanges(!rollback);
            }

            reviewRepository.saveAll(reviews);
            return 0;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return 1;
        }
    }

    @Override
    public List<ReviewDTO> completeDeleteReviews(String username) {
        try {
            List<Review> reviews= reviewRepository.findAllByUsername(username);

            List<ReviewDTO> rollbackList= reviews.stream()
                    .map(review -> modelMapper.map(review, ReviewDTO.class)).toList();

            for(Review review : reviews) {
                review.setDate(null);
                review.setText(null);
                review.setProduct(null);
                review.setEvaluation(0);
                review.setUsername(null);
            }
            reviewRepository.saveAll(reviews);

            return rollbackList;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }

    @Override
    public boolean releaseLock(List<UUID> reviewId) {
        try {
            reviewRepository.deleteAllById(reviewId);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
        }
    }


    @Override
    public AverageDTO getReviewAverage(ProductDTO productDTO) {
        List<Review> reviewDTOS = reviewRepository.findByproduct(modelMapper.map(productDTO, Product.class));

        if(reviewDTOS==null || reviewDTOS.isEmpty())
            return null;

        double average = 0;
        for (Review review : reviewDTOS) {
            average += review.getEvaluation();
        }
        AverageDTO averageDTO = new AverageDTO();
        averageDTO.setAverage(average / reviewDTOS.size());
        averageDTO.setNumberOfReview(reviewDTOS.size());
        return averageDTO;

    }

    @Override
    public String getTextReview(UUID reviewId) {
        return Objects.requireNonNull(reviewRepository.findById(reviewId).orElse(null)).getText();
    }

    @Override
    public int getNumberOfReview(ProductDTO productDTO, int star) {
        List<Review> result= reviewRepository.findAllByProductAndEvaluation(modelMapper.map(productDTO, Product.class), star);

        return result==null || result.isEmpty()? 0: result.size();
    }


    //METODI DI SERVIZIO
    private boolean checkText(String text) {
        return !text.isEmpty() && text.length()<=256;
    }

    private boolean checkEvaluation(int evaluation) {
        return evaluation>=1 && evaluation<=5;
    }
}