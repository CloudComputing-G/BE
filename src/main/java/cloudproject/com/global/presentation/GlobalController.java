package cloudproject.com.global.presentation;

import cloudproject.com.global.common.ApiResponse;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cloudproject.com.global.common.code.SuccessCode.HEALTH_CHECK_SUCCESS;

@RestController
@RequestMapping("/v1/global")
public class GlobalController {
    @GetMapping("/health-check")
    public ApiResponse<String> healthCheck(){
        return ApiResponse.success(HEALTH_CHECK_SUCCESS,"ok");
    }
}
