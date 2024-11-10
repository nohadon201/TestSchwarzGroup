package schwarz.jobs.interview.coupon.core.services;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import schwarz.jobs.interview.coupon.core.model.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.model.Basket;
import schwarz.jobs.interview.coupon.core.model.dto.CouponDTO;
import schwarz.jobs.interview.coupon.core.model.dto.CouponRequestDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    public Optional<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    public Optional<Basket> apply(Basket basket, String code) {
         return getCoupon(code.toLowerCase()).map(coupon -> {
             if(basket.getValue().doubleValue() < 0.0) {
                 log.error("Error applying discount. Basket value is minor than 0. For Basket id: (basketID)");
                 return null;
             } else if (basket.getValue().doubleValue() > 0.0 && basket.getValue().doubleValue() >= coupon.getMinBasketValue().doubleValue()) {
                 basket.applyDiscount(coupon.getDiscount());
             }

            return basket;
        });
    }


    public Coupon createCoupon(final CouponDTO couponDTO) throws Exception {
        Coupon coupon = null;

        List<Coupon> list = getCoupons( CouponRequestDTO.builder().codes( Arrays.asList( couponDTO.getCode() ) ).build() );

        if(list != null && list.isEmpty()) {
            coupon = Coupon.builder()
                    .code(couponDTO.getCode().toLowerCase())
                    .discount(couponDTO.getDiscount())
                    .minBasketValue(couponDTO.getMinBasketValue())
                    .build();

            couponRepository.save(coupon); // May produce an error
        }

        return coupon;
    }

    public List<Coupon> getCoupons(CouponRequestDTO couponRequestDTO) {

        ArrayList<Coupon> foundCoupons = new ArrayList<>();

        couponRequestDTO.getCodes().forEach(code -> {
            Optional<Coupon> optional = couponRepository.findByCode(code);
            optional.ifPresent(foundCoupons::add);
        });

        return foundCoupons;
    }

}
