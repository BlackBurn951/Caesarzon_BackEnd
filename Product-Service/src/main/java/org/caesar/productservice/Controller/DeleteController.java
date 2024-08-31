package org.caesar.productservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.caesar.productservice.Data.Entities.WishlistProduct;
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

    @PostMapping("/user/delete")
    public ResponseEntity<String> validateUserDelete(@RequestParam("rollback") boolean rollback) {
        String username= httpServletRequest.getAttribute("preferred_username").toString();


        List<WishlistDTO> wishlists= wishlistService.validateOrRollbackDeleteUserWishlist(username, rollback);

        boolean orders= orderService.validateDeleteUserOrders(username, rollback),
                wishlistProd= true,
                review= reviewService.validateDeleteReviewsForUserDelete(username, rollback),
                productOrder= productOrderService.validateOrRollbackDeleteUserCart(username, rollback);

        if(wishlists!=null && !wishlists.isEmpty())
            wishlistProd= wishlistProductService.validateOrRollbackDeleteUserWish(wishlists, rollback);


        if(wishlists!=null && orders && wishlistProd && review && productOrder)
            return new ResponseEntity<>("Validazione avvenuta con successo", HttpStatus.OK);

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
