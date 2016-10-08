package com.accelerator.dpwc.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ClockRepository extends JpaRepository<Clock, Clock.Id> {

    List<Clock> findByIdUserUsernameAndIdDateBetween(String username, Date minDate, Date maxDate);

}
