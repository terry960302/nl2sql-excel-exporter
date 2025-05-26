package com.pandaterry.auth_microservice.infrastructure.config.init;

import com.pandaterry.auth_microservice.domain.entity.Plan;
import com.pandaterry.auth_microservice.domain.repository.PlanRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PlanInitializer implements ApplicationRunner {

    private final PlanRepository planRepository;

    public PlanInitializer(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initPlan("BASIC", 100, 1); // 월 100건, 초당 1건
        initPlan("PRO", 500, 3); // 월 500건, 초당 3건
    }

    private void initPlan(String name, int monthlyQuota, int rateLimitRps) {
        if (!planRepository.existsByName(name)) {
            Plan plan = new Plan();
            plan.setName(name);
            plan.setMonthlyQuota(monthlyQuota);
            plan.setRateLimitRps(rateLimitRps);
            planRepository.save(plan);
        }
    }
}