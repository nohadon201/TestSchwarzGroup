package schwarz.jobs.interview.coupon.core.model.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CouponDTO {

    @NotNull
    private BigDecimal discount;

    @NotBlank
    private String code;

    @NotNull
    private BigDecimal minBasketValue;

}
