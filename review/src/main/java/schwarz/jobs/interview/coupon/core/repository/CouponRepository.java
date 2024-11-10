package schwarz.jobs.interview.coupon.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import schwarz.jobs.interview.coupon.core.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(final String code);

}
