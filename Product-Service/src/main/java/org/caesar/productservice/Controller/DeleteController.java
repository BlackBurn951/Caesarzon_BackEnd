package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.caesar.productservice.Data.Services.*;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.GeneralService.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product-api")
public class DeleteController {

    private final HttpServletRequest httpServletRequest;
    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;
    private final ReviewService reviewService;
    private final ProductOrderService productOrderService;
    private final OrderService orderService;
    private final GeneralService generalService;

    @PostMapping("/user/delete")
    public ResponseEntity<UserDeleteValidationDTO> validateUserDelete(@RequestParam("rollback") boolean rollback) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        UserDeleteValidationDTO result= new UserDeleteValidationDTO();

        List<WishlistDTO> wishlists= wishlistService.validateOrRollbackDeleteUserWishlist(username, rollback);
        List<OrderDTO> orders= orderService.validateDeleteUserOrders(username, rollback);

        result.setWishlists(wishlists);
        result.setWishlistProduct(wishlistProductService.validateOrRollbackDeleteUserWish(wishlists, rollback));
        result.setReview(reviewService.validateDeleteReviews(username, rollback));
        result.setProductOrder(productOrderService.validateOrRollbackDeleteUserCart(username, rollback));
        result.setOrders(orders);

        if(result.getOrders()!=null && result.getWishlists()!=null && result.isProductOrder() && result.isWishlistProduct() && result.isReview())
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/delete")
    public ResponseEntity<UserDeleteCompleteDTO> completeUserDelete(@RequestBody List<WishlistDTO> wishlists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        UserDeleteCompleteDTO result= new UserDeleteCompleteDTO();

        List<ReviewDTO> reviews= reviewService.completeDeleteReviews(username);
        List<ProductOrderDTO> productOrders= productOrderService.completeDeleteUserCart(username);
        List<WishListProductDTO> wishlistProducts= wishlistProductService.completeDeleteUserWish(wishlists);

        result.setWishlists(wishlistService.completeDeleteUserWishlist(username));
        result.setWishlistProduct(wishlistProducts);
        result.setOrders(orderService.completeDeleteUserOrders(username));
        result.setProductOrder(productOrders);
        result.setReviews(reviews);

        if(result.getProductOrder()!=null && result.getWishlistProduct()!=null && result.getReviews()!=null
            && result.isOrders() && result.isWishlists())
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> releaseUserDelete(@RequestBody RollbackUserDeleteDTO lists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean productWish= wishlistProductService.releaseLockDeleteUserWish(lists.getWishlists()),
                wishlist= wishlistService.releaseLockDeleteUserWishlist(username),
                productOrder= productOrderService.releaseLockDeleteUserCart(username),
                order= orderService.releaseLockDeleteUserOrders(username),
                review= reviewService.releaseLock(lists.getReviews().stream().map(ReviewDTO::getId).toList());

        if(productWish && wishlist && productOrder && order && review)
            return new ResponseEntity<>("Eliminazione avvenuta con successo!", HttpStatus.OK);
        return new ResponseEntity<>("Problemi nell'eliminazione...", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user/delete/rollback")
    public ResponseEntity<String> rollbackUserDelete(@RequestBody RollbackUserDeleteDTO lists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        for(WishlistDTO wishlist: lists.getWishlists()) {
            wishlistService.addOrUpdateWishlist(wishlist, username);
        }

        for(WishListProductDTO product: lists.getWishListProducts()) {
            wishlistProductService.addOrUpdateWishlistProduct(product);
        }

        for(ReviewDTO review: lists.getReviews()) {
            generalService.addReview(review, username);
        }

        orderService.rollbackDeleteUserOrders(lists.getOrders());
        productOrderService.rollbackProductInOrder(lists.getProductOrders());

        return new ResponseEntity<>("Rollback eseguito!", HttpStatus.OK);
    }
}
