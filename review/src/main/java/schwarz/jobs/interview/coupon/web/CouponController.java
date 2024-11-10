package schwarz.jobs.interview.coupon.web;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schwarz.jobs.interview.coupon.core.model.Coupon;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.model.Basket;
import schwarz.jobs.interview.coupon.core.model.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.core.model.dto.CouponDTO;
import schwarz.jobs.interview.coupon.core.model.dto.CouponRequestDTO;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class CouponController {

    private final CouponService couponService;

    /**
     * This function is an endpoint that activate a process to check if exist a Coupon with the code provided and if it exists apply it.
     * @param applicationRequestDTO An object that contains the information about a shop process like: the Basket and the code of the Coupon
     * @return The basket with the coupon applied (or not).
     */
    @ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @PostMapping(value = "/apply")
    public ResponseEntity<Basket> apply(
        @ApiParam(value = "Provides the necessary basket and customer information required for the coupon application", required = true)
        @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO
    ) {
        log.info("Applying coupon");

        Optional<Basket> basket = couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode());

        if (basket.isEmpty()) {
            log.warn("Invalid Basket. The value is less than 0.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else if (!applicationRequestDTO.getBasket().isApplicationSuccessful()) {
            log.warn("Request is valid, but coupon not applied.");
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok().body(applicationRequestDTO.getBasket()); // Success

    }

    /**
     * This function is and Endpoint that it's called to create new Coupongs
     * @param couponDTO The DTO containing the information of the Coupong created
     * @return It can return 3 things: the first (200 OK) is the id of the coupon in case that it's created successfully, the second (400 Bad Request) is in the case that there is on or more properties of the request object wrong or null and the last one (406 Not Acceptable) in case that there is another Coupon with the same code in the BBDD.
     */
    @ApiOperation(value = "Creates a Coupon - Version 1")
    @PostMapping("/create")
    public ResponseEntity<String> create(
            @ApiParam(value="Provide all the information to build the Coupon", required = true)
            @RequestBody @Valid final CouponDTO couponDTO
    ) {
        log.info("Creating a Coupon.");
        if(couponDTO.getMinBasketValue().doubleValue() > 0.0d && couponDTO.getDiscount().doubleValue() > 0.0d && !couponDTO.getCode().isBlank()) {
            try {
                final Coupon coupon = couponService.createCoupon(couponDTO);
                if(coupon != null) {
                    log.info("Coupon Created: " + coupon);
                    return ResponseEntity.ok().body(coupon.getCode());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The code of the coupon is repeated. A Coupon with code: " + couponDTO.getCode() + " exists.");
                }
            } catch (Exception e) {
                log.error("The creation of Coupon transaction failed. Error: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The coupon creation transaction failed. Error: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The code values are invalid. There is one ore more values that are negative. " + couponDTO);
        }
    }

    /**
     * Get all the Coupons that match with one of the codes provided in the list.
     * @param couponRequestDTO The Object wrapper that contains the list of Coupon codes.
     * @return A list of coupons that each of them match with one of the codes in the list.
     */
    @ApiOperation(value="Provide a list of coupons that each of it's codes matches with the a code provided in parameter.")
    @GetMapping(value = "/coupons", consumes = "application/json")
    public ResponseEntity<List<Coupon>> getCoupons(
            @ApiParam(value = "The list of Coupon codes.", required = true)
            @RequestBody @Valid final CouponRequestDTO couponRequestDTO
    ) {
        try {
            List<Coupon> list = couponService.getCoupons(couponRequestDTO);
            if(!list.isEmpty()) {
                return ResponseEntity.ok(list);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
