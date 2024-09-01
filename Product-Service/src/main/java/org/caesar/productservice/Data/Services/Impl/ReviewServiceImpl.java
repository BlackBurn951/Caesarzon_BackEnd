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
            Review review = new Review();

            if(reviewDTO.getId()==null && reviewRepository.findReviewByUsernameAndProduct(reviewDTO.getUsername(),modelMapper.map(productDTO, Product.class))!=null)
                return "Limite recensioni raggiunto...";
            System.out.println("Stampa 2");
            review.setProduct(modelMapper.map(productDTO, Product.class));
            review.setDate(LocalDate.now());

            if(checkText(reviewDTO.getText()) && checkEvaluation(reviewDTO.getEvaluation())) {  //Convalida dei dati in arrivo
                review.setText(reviewDTO.getText());
                review.setEvaluation(reviewDTO.getEvaluation());
            }
            else
                return "Problemi nell'aggiunta della recensione...";

            System.out.println("Stampa 3");
            review.setUsername(reviewDTO.getUsername());
            review.setOnChanges(false);


            System.out.println("Dati della recensione inviati: "+reviewDTO.getText()+"\n"+reviewDTO.getEvaluation()+"\n"+reviewDTO.getUsername());
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

            if(review==null || review.isOnChanges())
                return null;

            return modelMapper.map(review, ReviewDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }


    @Override
    public List<ReviewDTO> getReviewsByProduct(ProductDTO productDTO, int str) {
        List<Review> reviews= reviewRepository.findAllByproduct(modelMapper.map(productDTO, Product.class), PageRequest.of(str, 10));

        List<Review> result= new Vector<>();
        for(Review review : reviews) {
            if(review.isOnChanges())
                continue;
            result.add(review);
        }

        if(result.isEmpty())
            return new Vector<>();

        return result.stream()
                .map(a -> modelMapper.map(a, ReviewDTO.class))
                .toList();
    }

    @Override
    public ReviewDTO validateDeleteReviewById(UUID reviewId, boolean rollback) {
        try {
            Review review= reviewRepository.findById(reviewId).orElse(null);

            if(review==null || (review.isOnChanges() && !rollback))
                return null;

            review.setOnChanges(!rollback);

            reviewRepository.save(review);
            return modelMapper.map(review, ReviewDTO.class);
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }

    @Override
    public boolean completeDeleteReviewById(UUID reviewId) {
        try {
            Review review= reviewRepository.findById(reviewId).orElse(null);

            if(review==null)
                return false;

            review.setDate(null);
            review.setText(null);
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
    public boolean validateDeleteReview(ReviewDTO reviewDTO, boolean rollback) {
        try {
            Review review= reviewRepository.findById(reviewDTO.getId()).orElse(null);

            if(review==null || (review.isOnChanges() && !rollback))
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
    public List<ReviewDTO> validateDeleteReviews(String username, boolean rollback) {
        try {
            List<Review> reviews= reviewRepository.findAllByUsername(username);

            if(reviews.isEmpty())
                return new Vector<>();

            List<Review> result= new Vector<>();
            for(Review review : reviews) {
                if(review.isOnChanges() && !rollback)
                    continue;

                review.setOnChanges(!rollback);
                result.add(review);
            }

            if(result.isEmpty())
                return new Vector<>();

            reviewRepository.saveAll(result);
            return result.stream()
                    .map(review -> modelMapper.map(review, ReviewDTO.class)).toList();
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return null;
        }
    }

    @Override
    public boolean completeDeleteReviews(String username) {
        try {
            System.out.println("Prima della presa della recensione");
            List<Review> reviews= reviewRepository.findAllByUsername(username);

            System.out.println("Dopo la presa delle recensioni");

            for(Review review : reviews) {
                review.setDate(null);
                review.setText(null);
                review.setEvaluation(0);
                review.setUsername(null);
            }
            reviewRepository.saveAll(reviews);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della recensione");
            return false;
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
    public boolean validateDeleteReviewsForUserDelete(String username, boolean rollback) {
        try {
            List<Review> reviews= reviewRepository.findAllByUsername(username);

            if(reviews.isEmpty())
                return true;


            List<Review> result= new Vector<>();
            for(Review review : reviews) {
                if(review.isOnChanges() && !rollback)
                    continue;

                review.setOnChanges(!rollback);
                result.add(review);
            }

            if(result.isEmpty())
                return true;

            reviewRepository.saveAll(result);
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
            if(review.isOnChanges())
                continue;
            average += review.getEvaluation();
        }
        AverageDTO averageDTO = new AverageDTO();
        averageDTO.setAverage(average / reviewDTOS.size());
        averageDTO.setNumberOfReview(reviewDTOS.size());
        return averageDTO;

    }

    @Override
    public String getTextReview(UUID reviewId) {
        Review review= reviewRepository.findById(reviewId).orElse(null);

        if(review==null || review.isOnChanges())
            return "";

        return review.getText();
    }

    @Override
    public int getNumberOfReview(ProductDTO productDTO, int star) {
        List<Review> reviews= reviewRepository.findAllByProductAndEvaluation(modelMapper.map(productDTO, Product.class), star);

        List<Review> result= new Vector<>();
        for(Review review : reviews) {
            if(review.isOnChanges())
                continue;
            result.add(review);
        }

        return result.isEmpty()? 0: result.size();
    }


    //METODI DI SERVIZIO
    private boolean checkText(String text) {
        return !text.isEmpty() && text.length()<=256;
    }

    private boolean checkEvaluation(int evaluation) {
        return evaluation>=1 && evaluation<=5;
    }
}