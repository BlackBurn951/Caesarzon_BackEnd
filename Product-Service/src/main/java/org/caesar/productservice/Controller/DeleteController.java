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
import java.util.Vector;

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
        if(result.getWishlists()!=null && !result.getWishlists().isEmpty())
            result.setWishlistProduct(wishlistProductService.validateOrRollbackDeleteUserWish(wishlists, rollback));

        result.setReview(reviewService.validateDeleteReviews(username, rollback));
        result.setProductOrder(productOrderService.validateOrRollbackDeleteUserCart(username, rollback));
        result.setOrders(orders);

        if(result.getWishlists()==null || result.getOrders()==null || result.getWishlistProduct()==null || result.getReview()==null ||
            result.getProductOrder()==null)
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/user/delete")
    public ResponseEntity<UserDeleteCompleteDTO> completeUserDelete(@RequestParam("review") boolean review, @RequestParam("product") boolean product, @RequestParam("order") boolean order, @RequestParam("wish-prod") boolean wishProd, @RequestBody List<WishlistDTO> wishlists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        UserDeleteCompleteDTO result= new UserDeleteCompleteDTO();

        if(review)
            result.setReviews(reviewService.completeDeleteReviews(username));

        if(product)
            result.setProductOrder(productOrderService.completeDeleteUserCart(username));

        if(!wishlists.isEmpty())
            result.setWishlists(wishlistProductService.completeDeleteUserWish(wishlists));

        if(order)
            result.setOrders(orderService.completeDeleteUserOrders(username));

        if(wishProd)
            result.setWishlists(wishlistService.completeDeleteUserWishlist(username));

        if((review && result.isReviews()) && (product && result.isProductOrder()) && (!wishlists.isEmpty() && result.isWishlists())
            && (order && result.isOrders()) && (wishProd && result.isWishlistProduct()))
            return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> releaseUserDelete(@RequestBody RollbackUserDeleteDTO lists) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();

        boolean productOrder= true, productWish= true, wishlist= true, order= true, review= true;
        if(!lists.getProductOrders().isEmpty())
            productOrder= productOrderService.releaseLockDeleteUserCart(username);
        if(!lists.getOrders().isEmpty())
            order= orderService.releaseLockDeleteUserOrders(username);
        if(!lists.getReviews().isEmpty())
            review= reviewService.releaseLock(lists.getReviews().stream().map(ReviewDTO::getId).toList());
        if(!lists.getWishlists().isEmpty())
            wishlist= wishlistService.releaseLockDeleteUserWishlist(username);
        if(!lists.getWishListProducts().isEmpty())
            productWish= wishlistProductService.releaseLockDeleteUserWish(lists.getWishlists());

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
