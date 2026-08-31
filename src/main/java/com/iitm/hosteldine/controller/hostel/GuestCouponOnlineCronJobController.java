package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.GuestCouponOnlinePaymentService;
import com.iitm.hosteldine.service.student.AccommodationMessConvocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${url.online.payment.cronjob}")
public class GuestCouponOnlineCronJobController {
    private final SimsConfigDataService simsConfigDataService;
    private final AccommodationMessConvocationService accommodationMessConvocationService;
    private final GuestCouponOnlinePaymentService couponOnlinePaymentService;

    // ====== MESS COUPON CRON STATE ======
    private final AtomicBoolean messProcessing = new AtomicBoolean(false);
    private volatile long messLastExecutionTime = 0;

    // ====== CONVOCATION COUPON CRON STATE ======
    private final AtomicBoolean convProcessing = new AtomicBoolean(false);
    private volatile long convLastExecutionTime = 0;
    private static final long TIMEOUT = 1 * 60 * 1000; // 10 minutes


    @GetMapping(value = "${url.mess.coupon.cron}")
    public int updateOnlineMessCouponPendingRequestStatus(HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        System.out.println("messProcessing..."+messProcessing);

        long now = System.currentTimeMillis();
        // Reset if stuck
        if (messProcessing.get() && (now - messLastExecutionTime) > TIMEOUT) {
            System.out.println("Force resetting stuck cron flag...");
            messProcessing.set(false);
        }
        // ✅ Check if already running
        if (!messProcessing.compareAndSet(false, true)) {
            System.out.println("Cron already running...");
            return 0; // Already running
        }
        messLastExecutionTime = now;
        int count = 0;
        try {
            String version = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_CRON_JOB_VERSION);
            System.out.println("----version---------"+version);
            if(version!=null && version.equals("v2")) {
                count = couponOnlinePaymentService.updateOnlineCouponAllPendingRequestsV2(request);
            }else{
                count = couponOnlinePaymentService.updateOnlineCouponAllPendingRequestsV1(request);
            }
        } finally {
            // ✅ Reset flag always (even if exception occurs)
            messProcessing.set(false);
        }
        System.out.println("isProcessing..."+messProcessing);
        return count;
    }


    @GetMapping(value = "${url.conv.coupon.cron}")
    public int updateOnlineConvCouponPendingRequestStatus(HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        System.out.println("convProcessing..."+convProcessing);

        long now = System.currentTimeMillis();
        // Reset if stuck
        if (convProcessing.get() && (now - convLastExecutionTime) > TIMEOUT) {
            System.out.println("Force resetting stuck cron flag...");
            convProcessing.set(false);
        }
        // ✅ Check if already running
        if (!convProcessing.compareAndSet(false, true)) {
            System.out.println("Cron already running...");
            return 0; // Already running
        }
        convLastExecutionTime = now;
        int count = 0;
        try {
            count = accommodationMessConvocationService.updateConvOnlineCouponAllPendingRequests(request);
        } finally {
            // ✅ Reset flag always (even if exception occurs)
            convProcessing.set(false);
        }
        System.out.println("isProcessing..."+convProcessing);
        return count;
    }

}
