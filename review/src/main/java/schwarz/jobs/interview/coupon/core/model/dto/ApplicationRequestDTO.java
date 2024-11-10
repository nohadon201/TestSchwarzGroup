package schwarz.jobs.interview.coupon.core.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import schwarz.jobs.interview.coupon.core.model.Basket;

@Data
@Builder
public class ApplicationRequestDTO {

    @NotBlank
    private String code;

    @NotNull
    private Basket basket;

}
