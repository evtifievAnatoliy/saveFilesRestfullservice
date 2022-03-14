package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageDbRepository extends JpaRepository<Image,Long> {

    @Query("select i from Image i inner join User u on i.user.id = u.id " +
            "where i.id = :imageId and u.id = :userId")
    Image findByImageIdAndUserId(@Param("imageId") Long imageId, @Param("userId") Long userId);
}
